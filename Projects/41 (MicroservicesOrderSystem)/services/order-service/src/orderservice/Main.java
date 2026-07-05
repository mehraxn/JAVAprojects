package orderservice;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = readPort("PORT", 8080);
        DownstreamGateway gateway = new HttpDownstreamGateway(
                environment("INVENTORY_SERVICE_URL", "http://localhost:8081"),
                environment("PAYMENT_SERVICE_URL", "http://localhost:8082"),
                environment("NOTIFICATION_SERVICE_URL", "http://localhost:8083"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/orders", new OrderController(new OrderService(gateway)));
        server.createContext("/health", exchange ->
                OrderController.send(exchange, 200, "{\"status\":\"UP\",\"service\":\"order-service\"}"));
        server.setExecutor(Executors.newFixedThreadPool(8));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Order service listening on port " + port);
    }

    private static String environment(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static int readPort(String name, int defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        int port = Integer.parseInt(value);
        if (port < 1 || port > 65_535) {
            throw new IllegalArgumentException(name + " must be between 1 and 65535.");
        }
        return port;
    }
}
