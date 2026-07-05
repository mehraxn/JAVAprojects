package inventoryservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class InventoryController implements HttpHandler {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Map<String, String> query = query(exchange);
            String path = exchange.getRequestURI().getPath();
            if ("GET".equals(exchange.getRequestMethod()) && "/inventory".equals(path)) {
                InventoryItem item = service.find(required(query, "sku")).orElse(null);
                if (item == null) {
                    send(exchange, 404, "{\"error\":\"SKU not found\"}");
                } else {
                    send(exchange, 200, item.toJson());
                }
                return;
            }
            if ("POST".equals(exchange.getRequestMethod()) && "/inventory/reserve".equals(path)) {
                boolean reserved = service.reserve(required(query, "orderId"), required(query, "sku"),
                        Integer.parseInt(required(query, "quantity")));
                send(exchange, reserved ? 200 : 409, reserved
                        ? "{\"reserved\":true}" : "{\"reserved\":false,\"error\":\"Insufficient stock or conflicting reservation\"}");
                return;
            }
            if ("POST".equals(exchange.getRequestMethod()) && "/inventory/release".equals(path)) {
                boolean released = service.release(required(query, "orderId"));
                send(exchange, released ? 200 : 404, released
                        ? "{\"released\":true}" : "{\"released\":false,\"error\":\"Reservation not found\"}");
                return;
            }
            send(exchange, 404, "{\"error\":\"Endpoint not found\"}");
        } catch (NumberFormatException exception) {
            send(exchange, 400, "{\"error\":\"Quantity must be a whole number\"}");
        } catch (IllegalArgumentException exception) {
            send(exchange, 400, "{\"error\":\"" + escape(exception.getMessage()) + "\"}");
        }
    }

    private static Map<String, String> query(HttpExchange exchange) {
        Map<String, String> values = new HashMap<>();
        String raw = exchange.getRequestURI().getRawQuery();
        if (raw != null && !raw.isBlank()) {
            for (String pair : raw.split("&")) {
                String[] parts = pair.split("=", 2);
                values.put(URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                        parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "");
            }
        }
        return values;
    }

    private static String required(Map<String, String> query, String name) {
        String value = query.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        return value.trim();
    }

    static void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
