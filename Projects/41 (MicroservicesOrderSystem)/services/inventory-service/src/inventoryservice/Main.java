package inventoryservice;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = readPort(8081);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        InventoryController controller = new InventoryController(new InventoryService());
        server.createContext("/inventory", controller);
        server.createContext("/health", exchange -> InventoryController.send(exchange, 200,
                "{\"status\":\"UP\",\"service\":\"inventory-service\"}"));
        server.setExecutor(Executors.newFixedThreadPool(4));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(1)));
        server.start();
        System.out.println("Inventory service listening on port " + port);
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
