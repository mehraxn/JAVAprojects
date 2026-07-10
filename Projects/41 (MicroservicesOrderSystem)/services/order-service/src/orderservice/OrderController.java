package orderservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderController implements HttpHandler {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // HttpServer contexts are prefix-based, so "/orders-wrong" and
        // "/orders/anything" land here too. Only the exact path is served.
        if (!"/orders".equals(exchange.getRequestURI().getPath())) {
            send(exchange, 404, "{\"error\":\"endpoint not found\"}");
            return;
        }
        try {
            Map<String, String> query = queryParameters(exchange);
            if ("POST".equals(exchange.getRequestMethod())) {
                Order order = service.create(required(query, "sku"),
                        Integer.parseInt(required(query, "quantity")),
                        new BigDecimal(required(query, "unitPrice")));
                send(exchange, 201, order.toJson());
                return;
            }
            if ("GET".equals(exchange.getRequestMethod())) {
                String id = query.get("id");
                if (id != null) {
                    Order order = service.find(id).orElse(null);
                    if (order == null) {
                        send(exchange, 404, "{\"error\":\"order not found\"}");
                    } else {
                        send(exchange, 200, order.toJson());
                    }
                } else {
                    String body = service.findAll().stream().map(Order::toJson)
                            .collect(Collectors.joining(",", "[", "]"));
                    send(exchange, 200, body);
                }
                return;
            }
            exchange.getResponseHeaders().set("Allow", "GET, POST");
            send(exchange, 405, "{\"error\":\"method not allowed\"}");
        } catch (NumberFormatException exception) {
            send(exchange, 400, "{\"error\":\"quantity and unitPrice must be valid numbers\"}");
        } catch (IllegalArgumentException exception) {
            send(exchange, 400, "{\"error\":\"" + escape(exception.getMessage()) + "\"}");
        }
    }

    static Map<String, String> queryParameters(HttpExchange exchange) {
        Map<String, String> result = new HashMap<>();
        String rawQuery = exchange.getRequestURI().getRawQuery();
        if (rawQuery == null || rawQuery.isBlank()) {
            return result;
        }
        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length == 2 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }

    static String required(Map<String, String> query, String name) {
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

    static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
