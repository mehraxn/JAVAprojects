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

        server.createContext("/health", exchange -> respond(exchange, "{\"status\":\"UP\"}"));
        server.createContext("/ready", exchange -> respond(exchange, "{\"status\":\"READY\"}"));
        server.createContext("/config", exchange -> respond(exchange,
                "{\"environment\":\"" + escape(config.getEnvironment())
                        + "\",\"version\":\"" + escape(config.getVersion()) + "\"}"));

        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("GitOps example app listening on port " + config.getPort());
    }

    private static void respond(HttpExchange exchange, String json) throws IOException {
        int status = 200;
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
