package kubernetesdeploymentjavaapp;

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

        server.createContext("/health", exchange ->
                sendJson(exchange, 200, "{\"status\":\"UP\"}"));
        server.createContext("/ready", exchange ->
                sendJson(exchange, 200, "{\"status\":\"READY\"}"));
        server.createContext("/config", exchange -> sendJson(exchange, 200,
                "{\"environment\":\"" + escape(config.getEnvironment())
                        + "\",\"message\":\"" + escape(config.getMessage()) + "\"}"));

        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Java app listening on port " + config.getPort());
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            status = 405;
            json = "{\"error\":\"Method not allowed\"}";
            exchange.getResponseHeaders().set("Allow", "GET");
        }
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
