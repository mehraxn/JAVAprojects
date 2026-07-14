package notificationservice;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = readPort(8083);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/notifications", new NotificationController(new NotificationService()));
        // Contexts are prefix-based: enforce the exact path and GET method.
        server.createContext("/health", exchange -> {
            if (!"/health".equals(exchange.getRequestURI().getPath())) {
                NotificationController.send(exchange, 404, "{\"error\":\"endpoint not found\"}");
            } else if (!"GET".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                NotificationController.send(exchange, 405, "{\"error\":\"method not allowed\"}");
            } else {
                NotificationController.send(exchange, 200, "{\"status\":\"UP\",\"service\":\"notification-service\"}");
            }
        });
        // Catch-all: JSON 404 for anything no other context matches.
        server.createContext("/", exchange ->
                NotificationController.send(exchange, 404, "{\"error\":\"endpoint not found\"}"));
        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Mock notification service listening on port " + port);
    }

    private static int readPort(int defaultValue) {
        String value = System.getenv("PORT");
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        int port = Integer.parseInt(value);
        if (port < 1 || port > 65_535) {
            throw new IllegalArgumentException("PORT must be between 1 and 65535.");
        }
        return port;
    }
}
