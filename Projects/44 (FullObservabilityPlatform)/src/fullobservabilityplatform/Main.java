package fullobservabilityplatform;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A single, dependency-free Java app that emits all THREE observability
 * signals so the stack in docker-compose.yml has something to observe:
 *
 *   METRICS  GET /metrics  Prometheus text exposition (scraped by Prometheus).
 *   LOGS     stdout        one structured JSON line per request (shipped to Loki
 *                          by Promtail).
 *   TRACES   per request   a W3C trace_id / span_id is generated, returned in a
 *                          `traceparent` response header, and stamped into every
 *                          log line so logs correlate to traces.
 *
 * In a real deployment the OpenTelemetry Java agent would export spans over OTLP
 * to the Collector; here we generate the IDs ourselves to demonstrate
 * correlation without any external dependency. Nothing in this repo is run.
 *
 * Endpoints:
 *   GET /         hello, does a little work
 *   GET /health   liveness
 *   GET /work     variable-latency endpoint (?fail=1 forces an error)
 *   GET /metrics  Prometheus metrics
 */
public final class Main {

    private static final String SERVICE = env("OTEL_SERVICE_NAME", "observable-java-app");
    private static final String ENVIRONMENT = env("APP_ENV", "dev");
    private static final Random RANDOM = new Random();

    // ---- metrics state -----------------------------------------------------
    private static final Map<String, AtomicLong> requestCounts = new ConcurrentHashMap<>();
    private static final double[] BUCKETS =
            {0.005, 0.01, 0.025, 0.05, 0.1, 0.25, 0.5, 1.0, 2.5, 5.0};
    private static final AtomicLong[] bucketCounts = newCounters(BUCKETS.length);
    private static final AtomicLong durationCount = new AtomicLong();
    private static final java.util.concurrent.atomic.DoubleAdder durationSum =
            new java.util.concurrent.atomic.DoubleAdder();
    private static final long startNanos = System.nanoTime();

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(env("APP_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", observed("/", ex -> {
            work(5, 40);
            send(ex, 200, "application/json", "{\"message\":\"observability example\"}");
        }));
        server.createContext("/health", observed("/health", ex ->
                send(ex, 200, "application/json", "{\"status\":\"UP\"}")));
        server.createContext("/work", observed("/work", ex -> {
            boolean fail = ex.getRequestURI().getQuery() != null
                    && ex.getRequestURI().getQuery().contains("fail=1");
            work(20, 300);
            if (fail) {
                throw new RuntimeException("forced failure for demo");
            }
            send(ex, 200, "application/json", "{\"worked\":true}");
        }));
        server.createContext("/metrics", ex ->
                send(ex, 200, "text/plain; version=0.0.4", renderMetrics()));

        server.setExecutor(Executors.newFixedThreadPool(8));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        log("INFO", "startup", "listening on :" + port, newTraceId(), newSpanId());
        System.out.println("observable-java-app on :" + port);
    }

    /**
     * Wraps a handler so every request produces a trace id + span id, a metric,
     * and a correlated structured log line — the three signals from one place.
     */
    private static HttpHandler observed(String route, HttpHandler handler) {
        return exchange -> {
            String traceId = extractOrNewTraceId(exchange);
            String spanId = newSpanId();
            exchange.getResponseHeaders().set("traceparent",
                    "00-" + traceId + "-" + spanId + "-01");
            long start = System.nanoTime();
            int status = 200;
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    status = 405;
                    exchange.getResponseHeaders().set("Allow", "GET");
                    send(exchange, status, "application/json",
                            "{\"error\":\"method not allowed\"}");
                    return;
                }
                handler.handle(exchange);
            } catch (RuntimeException e) {
                status = 500;
                log("ERROR", "request_failed", e.getMessage(), traceId, spanId);
                safeSend(exchange, status, "{\"error\":\"internal\"}");
            } finally {
                double seconds = (System.nanoTime() - start) / 1_000_000_000.0;
                record(exchange.getRequestMethod(), route, status, seconds);
                log(status >= 500 ? "ERROR" : "INFO", "http_request",
                        exchange.getRequestMethod() + " " + route + " -> " + status,
                        traceId, spanId);
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
    private static void log(String level, String event, String message,
                            String traceId, String spanId) {
        String line = "{\"timestamp\":\"" + Instant.now()
                + "\",\"level\":\"" + level
                + "\",\"service\":\"" + SERVICE
                + "\",\"env\":\"" + ENVIRONMENT
                + "\",\"event\":\"" + event
                + "\",\"message\":\"" + escape(String.valueOf(message))
                + "\",\"trace_id\":\"" + traceId
                + "\",\"span_id\":\"" + spanId + "\"}";
        System.out.println(line);
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
    private static void send(HttpExchange ex, int status, String contentType, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (var out = ex.getResponseBody()) {
            out.write(bytes);
        }
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
