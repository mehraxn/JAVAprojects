package prometheusgrafanamonitoring;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {
    /** Safety cap for /work?ms= so a request can never stall a worker for long. */
    private static final long MAX_WORK_MS = 5_000;

    public static void main(String[] args) throws IOException {
        int port = readPort(System.getenv("APP_PORT"));
        String version = readVersion(System.getenv("APP_VERSION"));
        MetricsRegistry metrics = new MetricsRegistry(version);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // HttpServer contexts are prefix-matched, so every handler checks the
        // exact path. Unknown paths get 404 and are recorded with the bounded
        // metric label route="not_found" (never the raw URL).
        server.createContext("/health", route("/health", metrics, exchange ->
                send(exchange, 200, "application/json", "{\"status\":\"UP\"}")));
        server.createContext("/metrics", route("/metrics", metrics, exchange ->
                send(exchange, 200, "text/plain; version=0.0.4", metrics.renderPrometheusText())));
        server.createContext("/work", route("/work", metrics, Main::handleWork));
        // The "/" context also catches every otherwise-unmatched path.
        server.createContext("/", route("/", metrics, exchange ->
                send(exchange, 200, "application/json", "{\"message\":\"Monitoring example\"}")));

        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Metrics example listening on port " + port
                + " (version " + version + ")");
    }

    /**
     * Wraps a handler with exact-path checking, method validation, and
     * request metrics. The metric route label is the stable route name for
     * known paths and "not_found" for everything else.
     */
    private static HttpHandler route(String expectedPath, MetricsRegistry metrics,
            HttpHandler handler) {
        return exchange -> {
            long startedAt = System.nanoTime();
            String routeLabel = expectedPath;
            int status = 200;
            try {
                if (!expectedPath.equals(exchange.getRequestURI().getPath())) {
                    routeLabel = "not_found";
                    status = 404;
                    send(exchange, status, "application/json", "{\"error\":\"endpoint not found\"}");
                    return;
                }
                if (!"GET".equals(exchange.getRequestMethod())) {
                    status = 405;
                    exchange.getResponseHeaders().set("Allow", "GET");
                    send(exchange, status, "application/json", "{\"error\":\"method not allowed\"}");
                    return;
                }
                status = handleAndReturnStatus(handler, exchange);
            } catch (RuntimeException exception) {
                status = 500;
                send(exchange, status, "application/json", "{\"error\":\"unexpected error\"}");
            } finally {
                double durationSeconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;
                metrics.recordRequest(
                        exchange.getRequestMethod(), routeLabel, status, durationSeconds);
            }
        };
    }

    private static int handleAndReturnStatus(HttpHandler handler, HttpExchange exchange)
            throws IOException {
        handler.handle(exchange);
        // sendResponseHeaders has been called by the handler at this point.
        return exchange.getResponseCode();
    }

    /**
     * Demo traffic endpoint for exercising the dashboard and alerts:
     *   /work           fast 200
     *   /work?ms=100    simulates ~100ms of work (capped at MAX_WORK_MS)
     *   /work?fail=1    intentional 500, to drive the error-ratio alert
     */
    private static void handleWork(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        long ms = 0;
        boolean fail = false;
        if (query != null) {
            for (String pair : query.split("&")) {
                int eq = pair.indexOf('=');
                String name = eq > 0 ? pair.substring(0, eq) : pair;
                String value = eq > 0 ? pair.substring(eq + 1) : "";
                if ("ms".equals(name)) {
                    try {
                        ms = Math.max(0, Math.min(Long.parseLong(value), MAX_WORK_MS));
                    } catch (NumberFormatException ignored) {
                        ms = 0;
                    }
                }
                if ("fail".equals(name) && "1".equals(value)) {
                    fail = true;
                }
            }
        }
        if (ms > 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        if (fail) {
            send(exchange, 500, "application/json",
                    "{\"error\":\"intentional failure for alert testing\"}");
            return;
        }
        send(exchange, 200, "application/json", "{\"worked_ms\":" + ms + "}");
    }

    private static void send(HttpExchange exchange, int status, String contentType, String text)
            throws IOException {
        byte[] body = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static int readPort(String value) {
        if (value == null || value.isBlank()) {
            return 8080;
        }
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65_535) {
                throw new IllegalArgumentException("APP_PORT must be between 1 and 65535.");
            }
            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("APP_PORT must be a whole number.", exception);
        }
    }

    private static String readVersion(String value) {
        if (value == null || value.isBlank()) {
            return "0.1.0";
        }
        String clean = value.trim();
        if (clean.length() > 40) {
            throw new IllegalArgumentException("APP_VERSION is too long.");
        }
        return clean;
    }
}
