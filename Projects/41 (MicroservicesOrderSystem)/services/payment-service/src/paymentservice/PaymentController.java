package paymentservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PaymentController implements HttpHandler {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Exact path only (contexts are prefix-based); wrong method is 405.
            if (!"/payments/authorize".equals(exchange.getRequestURI().getPath())) {
                send(exchange, 404, "{\"error\":\"endpoint not found\"}");
                return;
            }
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "POST");
                send(exchange, 405, "{\"error\":\"method not allowed\"}");
                return;
            }
            Map<String, String> query = query(exchange);
            Payment payment = service.authorize(required(query, "orderId"),
                    new BigDecimal(required(query, "amount")));
            send(exchange, payment.isApproved() ? 200 : 402, payment.toJson());
        } catch (NumberFormatException exception) {
            send(exchange, 400, "{\"error\":\"amount must be a valid number\"}");
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
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
