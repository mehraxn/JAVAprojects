package prometheusgrafanamonitoring;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = readPort(System.getenv("APP_PORT"));
        MetricsRegistry metrics = new MetricsRegistry();
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", measured(metrics, exchange ->
                send(exchange, 200, "application/json", "{\"message\":\"Monitoring example\"}")));
        server.createContext("/health", measured(metrics, exchange ->
                send(exchange, 200, "application/json", "{\"status\":\"UP\"}")));
        server.createContext("/metrics", measured(metrics, exchange ->
                send(exchange, 200, "text/plain; version=0.0.4", metrics.renderPrometheusText())));

        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Metrics example listening on port " + port);
    }

    private static HttpHandler measured(MetricsRegistry metrics, HttpHandler handler) {
        return exchange -> {
            long startedAt = System.nanoTime();
            int status = 200;
            try {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    status = 405;
                    exchange.getResponseHeaders().set("Allow", "GET");
                    send(exchange, status, "application/json", "{\"error\":\"Method not allowed\"}");
                    return;
                }
                handler.handle(exchange);
            } catch (RuntimeException exception) {
                status = 500;
                send(exchange, status, "application/json", "{\"error\":\"Unexpected error\"}");
            } finally {
                double durationSeconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;
                metrics.recordRequest(
                        exchange.getRequestMethod(),
                        exchange.getHttpContext().getPath(),
                        status,
                        durationSeconds);
            }
        };
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
}
