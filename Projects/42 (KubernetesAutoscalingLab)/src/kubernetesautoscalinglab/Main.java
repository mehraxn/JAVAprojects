package kubernetesautoscalinglab;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Minimal, dependency-free HTTP app used to demonstrate Kubernetes
 * Horizontal Pod Autoscaling.
 *
 * The point of this app is NOT to be a useful service. It exists so that a
 * load generator (k6 or hey, see load-test/README.md) can drive measurable
 * CPU usage that the metrics-server reports, and that the HPA reacts to.
 *
 * Endpoints:
 *   GET /health   -> liveness probe target, always 200 once running.
 *   GET /ready    -> readiness probe target, always 200 once running.
 *   GET /         -> plain-text index describing the endpoints.
 *   GET /work     -> burns CPU in a busy loop for ~N milliseconds
 *                    (default 200, override with ?ms=NNN, capped).
 *                    This is the endpoint a load test hammers to raise
 *                    average CPU utilization and trigger the HPA.
 *   GET /metrics  -> a tiny Prometheus-style text exposition. NOTE: the
 *                    HPA in this lab uses CPU from metrics-server, NOT this
 *                    endpoint. It is here only for illustration.
 *
 * Configuration comes from environment variables (populated by the
 * ConfigMap in k8s/configmap.yaml):
 *   PORT            listen port           (default 8080)
 *   MAX_WORK_MS     safety cap on /work   (default 2000)
 */
public final class Main {

    private static final AtomicLong requestCount = new AtomicLong();
    private static final AtomicLong workCount = new AtomicLong();

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        int port = envInt("PORT", 8080);
        long maxWorkMs = envInt("MAX_WORK_MS", 2000);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        // A small thread pool so concurrent load actually competes for CPU.
        server.setExecutor(Executors.newFixedThreadPool(
                Math.max(2, Runtime.getRuntime().availableProcessors())));

        server.createContext("/health", exchange -> respond(exchange, 200, "ok"));
        server.createContext("/ready", exchange -> respond(exchange, 200, "ready"));
        server.createContext("/metrics", Main::handleMetrics);
        server.createContext("/work", ex -> handleWork(ex, maxWorkMs));
        server.createContext("/", Main::handleIndex);

        server.start();
        System.out.printf("kubernetes-autoscaling-lab listening on :%d (maxWorkMs=%d)%n",
                port, maxWorkMs);
    }

    private static void handleIndex(HttpExchange exchange) throws IOException {
        requestCount.incrementAndGet();
        if (!"/".equals(exchange.getRequestURI().getPath())) {
            respond(exchange, 404, "not found");
            return;
        }
        String body = """
                kubernetes-autoscaling-lab
                --------------------------
                GET /health   liveness
                GET /ready    readiness
                GET /work?ms= burn CPU for ms milliseconds (drives the HPA)
                GET /metrics  illustrative counters (NOT the HPA source)
                """;
        respond(exchange, 200, body);
    }

    /**
     * Burns CPU in a busy loop. This is deliberately wasteful: it keeps the
     * core busy so that container CPU usage rises toward the request/limit and
     * the HPA sees high averageUtilization.
     */
    private static void handleWork(HttpExchange exchange, long maxWorkMs) throws IOException {
        requestCount.incrementAndGet();
        long ms = clampQueryMs(exchange, 200, maxWorkMs);
        long deadline = System.nanoTime() + ms * 1_000_000L;
        long spins = 0;
        double sink = 0.0;
        while (System.nanoTime() < deadline) {
            // Non-trivial math so the JIT cannot optimize the loop away.
            sink += Math.sqrt(spins * 1.000001) * Math.sin(spins);
            spins++;
        }
        workCount.incrementAndGet();
        String body = String.format("worked_ms=%d spins=%d sink=%.4f%n", ms, spins, sink);
        respond(exchange, 200, body);
    }

    private static void handleMetrics(HttpExchange exchange) throws IOException {
        String body = ""
                + "# HELP app_requests_total Total HTTP requests handled.\n"
                + "# TYPE app_requests_total counter\n"
                + "app_requests_total " + requestCount.get() + "\n"
                + "# HELP app_work_total Total /work invocations completed.\n"
                + "# TYPE app_work_total counter\n"
                + "app_work_total " + workCount.get() + "\n";
        respond(exchange, 200, body);
    }

    private static long clampQueryMs(HttpExchange exchange, long dflt, long max) {
        String query = exchange.getRequestURI().getQuery();
        long ms = dflt;
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                if (eq > 0 && "ms".equals(pair.substring(0, eq))) {
                    try {
                        ms = Long.parseLong(pair.substring(eq + 1));
                    } catch (NumberFormatException ignored) {
                        ms = dflt;
                    }
                }
            }
        }
        return Math.max(0, Math.min(ms, max));
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static int envInt(String name, int dflt) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return dflt;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return dflt;
        }
    }
}
