package gitopsdeploymentplatform;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        AppConfig config = AppConfig.fromEnvironment();
        HttpServer server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

        // HttpServer contexts are prefix-matched ("/health" would also catch
        // "/health/test"), so every handler checks the exact path.
        server.createContext("/health", exchange ->
                respondExact(exchange, "/health", "{\"status\":\"UP\"}"));
        server.createContext("/ready", exchange ->
                respondExact(exchange, "/ready", "{\"status\":\"READY\"}"));
        server.createContext("/config", exchange ->
                respondExact(exchange, "/config",
                        "{\"environment\":\"" + escape(config.getEnvironment())
                                + "\",\"version\":\"" + escape(config.getVersion()) + "\"}"));
        // Catch-all for every unmatched path (including "/"): JSON 404.
        server.createContext("/", exchange ->
                send(exchange, 404, "{\"error\":\"endpoint not found\"}"));

        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("GitOps example app listening on port " + config.getPort());
    }

    private static void respondExact(HttpExchange exchange, String expectedPath, String json)
            throws IOException {
        if (!expectedPath.equals(exchange.getRequestURI().getPath())) {
            send(exchange, 404, "{\"error\":\"endpoint not found\"}");
            return;
        }
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            send(exchange, 405, "{\"error\":\"method not allowed\"}");
            return;
        }
        send(exchange, 200, json);
    }

    private static void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
