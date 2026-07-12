package urlshortenerbackend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UrlShortenerHttpServer {
    private static final int MAX_REQUEST_BYTES = 65_536;
    private final ShortenerService service;
    private HttpServer server;

    public UrlShortenerHttpServer(ShortenerService service) {
        if (service == null) {
            throw new IllegalArgumentException("Shortener service cannot be null.");
        }
        this.service = service;
    }

    public synchronized void start(int port) throws IOException {
        if (port < 0 || port > 65_535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535 (0 picks a free port).");
        }
        if (server != null) {
            throw new IllegalStateException("Server is already running.");
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleUnknown);
        server.createContext("/links", this::handleLinks);
        server.createContext("/r", this::handleRedirect);
        server.setExecutor(null);
        server.start();
    }

    public synchronized int getPort() {
        if (server == null) {
            throw new IllegalStateException("Server is not running.");
        }
        return server.getAddress().getPort();
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private void handleUnknown(HttpExchange exchange) throws IOException {
        try {
            sendJson(exchange, 404, errorJson("Endpoint not found."));
        } finally {
            exchange.close();
        }
    }

    private void handleLinks(HttpExchange exchange) throws IOException {
        try {
            if (!"/links".equals(exchange.getRequestURI().getPath())) {
                sendJson(exchange, 404, errorJson("Endpoint not found."));
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 200, entriesJson(service.listEntries()));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseForm(readBody(exchange));
                String customCode = form.get("code");
                if (customCode == null || customCode.trim().isEmpty()) {
                    sendJson(exchange, 201, entryJson(service.shorten(form.get("url"))));
                } else if (service.find(UrlEntry.validateShortCode(customCode)) != null) {
                    sendJson(exchange, 409, errorJson("Custom code already exists."));
                } else {
                    sendJson(exchange, 201, entryJson(service.shorten(form.get("url"), customCode)));
                }
            } else {
                exchange.getResponseHeaders().set("Allow", "GET, POST");
                sendJson(exchange, 405, errorJson("Method not allowed."));
            }
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, errorJson(exception.getMessage()));
        } catch (PayloadTooLargeException exception) {
            sendJson(exchange, 413, errorJson(exception.getMessage()));
        } catch (IOException exception) {
            sendJson(exchange, 400, errorJson(exception.getMessage()));
        } catch (RuntimeException exception) {
            sendJson(exchange, 500, errorJson("Internal server error."));
        } finally {
            exchange.close();
        }
    }

    private void handleRedirect(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Allow", "GET");
                sendJson(exchange, 405, errorJson("Method not allowed."));
                return;
            }
            String path = exchange.getRequestURI().getPath();
            if ("/r".equals(path) || "/r/".equals(path)) {
                sendJson(exchange, 404, errorJson("Short code is missing."));
                return;
            }
            if (!path.startsWith("/r/")) {
                sendJson(exchange, 404, errorJson("Endpoint not found."));
                return;
            }
            String code = path.substring(3);
            if (code.contains("/")) {
                sendJson(exchange, 404, errorJson("Endpoint not found."));
                return;
            }
            String originalUrl = service.resolve(code);
            exchange.getResponseHeaders().set("Location", originalUrl);
            exchange.sendResponseHeaders(302, -1);
        } catch (NoSuchElementException exception) {
            sendJson(exchange, 404, errorJson(exception.getMessage()));
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, errorJson(exception.getMessage()));
        } catch (RuntimeException exception) {
            sendJson(exchange, 500, errorJson("Internal server error."));
        } finally {
            exchange.close();
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = exchange.getRequestBody();
        byte[] buffer = new byte[4096];
        int total = 0;
        int count;
        while ((count = input.read(buffer)) != -1) {
            total += count;
            if (total > MAX_REQUEST_BYTES) {
                throw new PayloadTooLargeException("Request body is too large.");
            }
            output.write(buffer, 0, count);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    private static final class PayloadTooLargeException extends IOException {
        private static final long serialVersionUID = 1L;

        PayloadTooLargeException(String message) {
            super(message);
        }
    }

    private Map<String, String> parseForm(String body) throws IOException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        if (body == null || body.trim().isEmpty()) {
            return values;
        }
        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";
            if (values.put(key, value) != null) {
                throw new IOException("Duplicate form field: " + key);
            }
        }
        return values;
    }

    private String decode(String value) throws IOException {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid URL-encoded form data.", exception);
        }
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
    }

    private String entriesJson(List<UrlEntry> entries) {
        StringBuilder json = new StringBuilder("[");
        for (int index = 0; index < entries.size(); index++) {
            if (index > 0) {
                json.append(',');
            }
            json.append(entryJson(entries.get(index)));
        }
        return json.append(']').toString();
    }

    private String entryJson(UrlEntry entry) {
        return "{\"shortCode\":\"" + escapeJson(entry.getShortCode())
                + "\",\"originalUrl\":\"" + escapeJson(entry.getOriginalUrl())
                + "\",\"createdAt\":\"" + entry.getCreatedAt()
                + "\",\"hitCount\":" + entry.getHitCount() + "}";
    }

    private String errorJson(String message) {
        return "{\"error\":\"" + escapeJson(message == null ? "Request failed." : message) + "\"}";
    }

    private String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (character == '"') {
                escaped.append("\\\"");
            } else if (character == '\\') {
                escaped.append("\\\\");
            } else if (character == '\n') {
                escaped.append("\\n");
            } else if (character == '\r') {
                escaped.append("\\r");
            } else if (character == '\t') {
                escaped.append("\\t");
            } else if (character < 0x20) {
                escaped.append(String.format("\\u%04x", (int) character));
            } else {
                escaped.append(character);
            }
        }
        return escaped.toString();
    }
}
