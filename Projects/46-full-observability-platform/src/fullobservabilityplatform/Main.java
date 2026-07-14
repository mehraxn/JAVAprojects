package fullobservabilityplatform;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A single, dependency-free Java app that emits the observability signals the
 * stack in docker-compose.yml consumes:
 *
 *   METRICS  GET /metrics  Prometheus text exposition (scraped by Prometheus).
 *   LOGS     stdout + file one structured JSON line per request, written to both
 *                          stdout and /var/log/app/app.log (tailed by Promtail,
 *                          shipped to Loki).
 *   TRACES   per request   a W3C trace_id / span_id is generated, returned in a
 *                          `traceparent` response header, and stamped into every
 *                          log line so logs carry trace context (correlation).
 *
 * Trace context here is CORRELATION-ONLY: the app generates W3C trace_id/span_id
 * and propagates them in headers/logs, but it does NOT export OpenTelemetry
 * spans. The OTel Collector + Tempo in docker-compose.yml are an example tracing
 * backend that the app does not currently feed; see docs/traces.md for how real
 * span export would be added.
 *
 * Endpoints (exact-match routing; unknown paths return 404):
 *   GET /         hello, does a little work
 *   GET /health   liveness
 *   GET /work     variable-latency endpoint (?fail=1 forces a 500)
 *   GET /metrics  Prometheus metrics
 */
public final class Main {

    private static final String SERVICE = env("OTEL_SERVICE_NAME", "observable-java-app");
    private static final String ENVIRONMENT = env("APP_ENV", "dev");
    private static final Random RANDOM = new Random();

    /** Optional second sink so Promtail can tail a file (stdout is always used). */
    private static final PrintStream FILE_LOG = openFileLog();

    // ---- metrics state -----------------------------------------------------
    private static final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private static final double[] BUCKETS =
            {0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0};
    private static final AtomicLong[] bucketCounts = newCounters(BUCKETS.length);
    private static final AtomicLong durationCount = new AtomicLong();
    private static final java.util.concurrent.atomic.DoubleAdder durationSum =
            new java.util.concurrent.atomic.DoubleAdder();
    private static final long startNanos = System.nanoTime();

    /** A request handler that returns the HTTP status it sent. */
    @FunctionalInterface
    private interface Route {
        int handle(HttpExchange exchange) throws IOException;
    }

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(env("APP_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Single root context does EXACT-match routing so unknown paths get a
        // real 404 instead of being swallowed by a catch-all "/" handler.
        HttpHandler home = observed("/", ex -> {
            work(5, 40);
            return send(ex, 200, "application/json", "{\"message\":\"observability example\"}");
        });
        HttpHandler health = observed("/health", ex ->
                send(ex, 200, "application/json", "{\"status\":\"UP\"}"));
        HttpHandler work = observed("/work", ex -> {
            boolean fail = ex.getRequestURI().getQuery() != null
                    && ex.getRequestURI().getQuery().contains("fail=1");
            work(20, 300);
            if (fail) {
                throw new RuntimeException("forced failure for demo");
            }
            return send(ex, 200, "application/json", "{\"worked\":true}");
        });
        HttpHandler metrics = ex ->
                send(ex, 200, "text/plain; version=0.0.4", renderMetrics());
        HttpHandler notFound = observed("/unknown", ex ->
                send(ex, 404, "application/json", "{\"error\":\"not found\"}"));

        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            switch (path) {
                case "/" -> home.handle(exchange);
                case "/health" -> health.handle(exchange);
                case "/work" -> work.handle(exchange);
                case "/metrics" -> metrics.handle(exchange);
                default -> notFound.handle(exchange);
            }
        });

        server.setExecutor(Executors.newFixedThreadPool(8));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        logEvent("INFO", "startup", "listening on :" + port);
        System.out.println("observable-java-app on :" + port);
    }

    /**
     * Wraps a route so every request produces a trace id + span id, a metric,
     * and a correlated structured log line — the signals from one place. The
     * status recorded is exactly the status the route (or an error) returned.
     */
    private static HttpHandler observed(String routeLabel, Route route) {
        return exchange -> {
            String traceId = extractOrNewTraceId(exchange);
            String spanId = newSpanId();
            exchange.getResponseHeaders().set("traceparent",
                    "00-" + traceId + "-" + spanId + "-01");
            String method = exchange.getRequestMethod();
            long start = System.nanoTime();
            int status = 200;
            try {
                if (!"GET".equals(method)) {
                    exchange.getResponseHeaders().set("Allow", "GET");
                    status = send(exchange, 405, "application/json",
                            "{\"error\":\"method not allowed\"}");
                    return;
                }
                status = route.handle(exchange);
            } catch (RuntimeException e) {
                status = 500;
                safeSend(exchange, status, "{\"error\":\"internal\"}");
            } finally {
                double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
                record(method, routeLabel, status, seconds);
                logRequest(status >= 500 ? "ERROR" : "INFO",
                        method, routeLabel, status, seconds * 1000.0, traceId, spanId);
            }
        };
    }

    // ---- work simulation ---------------------------------------------------
    private static void work(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + RANDOM.nextInt(Math.max(1, maxMs - minMs)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---- metrics -----------------------------------------------------------
    private static void record(String method, String route, int status, double seconds) {
        String key = method + "|" + route + "|" + status;
        requestCounts.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
        durationCount.incrementAndGet();
        durationSum.add(seconds);
        for (int i = 0; i < BUCKETS.length; i++) {
            if (seconds <= BUCKETS[i]) {
                bucketCounts[i].incrementAndGet();
            }
        }
    }

    private static String renderMetrics() {
        StringBuilder out = new StringBuilder();
        out.append("# HELP http_requests_total Total HTTP requests.\n");
        out.append("# TYPE http_requests_total counter\n");
        for (Map.Entry<String, AtomicLong> e : requestCounts.entrySet()) {
            String[] p = e.getKey().split("\\|");
            out.append("http_requests_total{method=\"").append(p[0])
                    .append("\",route=\"").append(p[1])
                    .append("\",status=\"").append(p[2])
                    .append("\"} ").append(e.getValue().get()).append('\n');
        }
        out.append("# HELP http_request_duration_seconds Request duration.\n");
        out.append("# TYPE http_request_duration_seconds histogram\n");
        long cumulative = 0;
        for (int i = 0; i < BUCKETS.length; i++) {
            cumulative = bucketCounts[i].get();
            out.append("http_request_duration_seconds_bucket{le=\"")
                    .append(BUCKETS[i]).append("\"} ").append(cumulative).append('\n');
        }
        out.append("http_request_duration_seconds_bucket{le=\"+Inf\"} ")
                .append(durationCount.get()).append('\n');
        out.append("http_request_duration_seconds_sum ").append(durationSum.sum()).append('\n');
        out.append("http_request_duration_seconds_count ").append(durationCount.get()).append('\n');

        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        out.append("# HELP jvm_memory_used_bytes JVM heap used.\n");
        out.append("# TYPE jvm_memory_used_bytes gauge\n");
        out.append("jvm_memory_used_bytes{area=\"heap\"} ").append(heap.getUsed()).append('\n');
        out.append("# HELP process_uptime_seconds Uptime.\n");
        out.append("# TYPE process_uptime_seconds gauge\n");
        out.append("process_uptime_seconds ")
                .append((System.nanoTime() - startNanos) / 1_000_000_000.0).append('\n');
        out.append("# HELP app_info Build info.\n# TYPE app_info gauge\n");
        out.append("app_info{service=\"").append(SERVICE)
                .append("\",env=\"").append(ENVIRONMENT).append("\"} 1\n");
        return out.toString();
    }

    // ---- structured logging (correlated to traces) -------------------------

    /** Per-request log line with method/path/status/duration and trace context. */
    private static void logRequest(String level, String method, String path,
                                   int status, double durationMs,
                                   String traceId, String spanId) {
        String line = "{\"timestamp\":\"" + Instant.now()
                + "\",\"level\":\"" + level
                + "\",\"service\":\"" + SERVICE
                + "\",\"env\":\"" + ENVIRONMENT
                + "\",\"event\":\"http_request\""
                + ",\"method\":\"" + method + "\""
                + ",\"path\":\"" + escape(path) + "\""
                + ",\"status\":" + status
                + ",\"duration_ms\":" + String.format(Locale.ROOT, "%.1f", durationMs)
                + ",\"trace_id\":\"" + traceId + "\""
                + ",\"span_id\":\"" + spanId + "\"}";
        write(line);
    }

    /** Non-request log line (startup, etc.). */
    private static void logEvent(String level, String event, String message) {
        String line = "{\"timestamp\":\"" + Instant.now()
                + "\",\"level\":\"" + level
                + "\",\"service\":\"" + SERVICE
                + "\",\"env\":\"" + ENVIRONMENT
                + "\",\"event\":\"" + escape(event)
                + "\",\"message\":\"" + escape(message) + "\"}";
        write(line);
    }

    private static void write(String line) {
        System.out.println(line);
        if (FILE_LOG != null) {
            FILE_LOG.println(line);
        }
    }

    private static PrintStream openFileLog() {
        String path = env("APP_LOG_FILE", "/var/log/app/app.log");
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            return new PrintStream(new FileOutputStream(path, true), true, "UTF-8");
        } catch (Exception e) {
            System.err.println("log file unavailable (" + path + "): "
                    + e.getMessage() + " — logging to stdout only");
            return null;
        }
    }

    // ---- trace context helpers (W3C) ---------------------------------------
    private static String extractOrNewTraceId(HttpExchange exchange) {
        String traceparent = exchange.getRequestHeaders().getFirst("traceparent");
        if (traceparent != null) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 2 && parts[1].matches("[0-9a-f]{32}")) {
                return parts[1];
            }
        }
        return newTraceId();
    }

    private static String newTraceId() {
        return hex(16);
    }

    private static String newSpanId() {
        return hex(8);
    }

    private static String hex(int bytes) {
        StringBuilder sb = new StringBuilder(bytes * 2);
        for (int i = 0; i < bytes; i++) {
            sb.append(String.format(Locale.ROOT, "%02x", RANDOM.nextInt(256)));
        }
        return sb.toString();
    }

    // ---- http + util -------------------------------------------------------

    /** Sends a response and returns the status code (for the observed wrapper). */
    private static int send(HttpExchange ex, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (var out = ex.getResponseBody()) {
            out.write(bytes);
        }
        return status;
    }

    private static void safeSend(HttpExchange ex, int status, String body) {
        try {
            send(ex, status, "application/json", body);
        } catch (IOException ignored) {
            // response already committed
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    private static AtomicLong[] newCounters(int n) {
        AtomicLong[] a = new AtomicLong[n];
        for (int i = 0; i < n; i++) {
            a[i] = new AtomicLong();
        }
        return a;
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }
}
