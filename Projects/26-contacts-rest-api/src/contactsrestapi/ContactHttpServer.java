package contactsrestapi;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class ContactHttpServer {
    private static final int MAX_REQUEST_BYTES = 65_536;
    private final ContactService service;
    private HttpServer server;

    public ContactHttpServer(ContactService service) {
        if (service == null) {
            throw new IllegalArgumentException("Contact service cannot be null.");
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
        server.createContext("/contacts", this::handleContacts);
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
            sendJson(exchange, 404, JsonUtil.error("Endpoint not found."));
        } finally {
            exchange.close();
        }
    }

    private void handleContacts(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if ("/contacts".equals(path) || "/contacts/".equals(path)) {
                handleCollection(exchange);
            } else if (path.startsWith("/contacts/")) {
                String encodedId = path.substring("/contacts/".length());
                if (encodedId.isEmpty() || encodedId.contains("/")) {
                    sendJson(exchange, 404, JsonUtil.error("Endpoint not found."));
                } else {
                    handleItem(exchange, decode(encodedId));
                }
            } else {
                sendJson(exchange, 404, JsonUtil.error("Endpoint not found."));
            }
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, JsonUtil.error(exception.getMessage()));
        } catch (PayloadTooLargeException exception) {
            sendJson(exchange, 413, JsonUtil.error(exception.getMessage()));
        } catch (IOException exception) {
            sendJson(exchange, 400, JsonUtil.error(exception.getMessage()));
        } catch (RuntimeException exception) {
            sendJson(exchange, 500, JsonUtil.error("Internal server error."));
        } finally {
            exchange.close();
        }
    }

    private void handleCollection(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            Map<String, String> query = parseParameters(exchange.getRequestURI().getRawQuery());
            String searchText = query.get("q");
            int offset = parseInteger(query.get("offset"), 0, "offset");
            int limit = parseInteger(query.get("limit"), 100, "limit");
            sendJson(exchange, 200, JsonUtil.toJson(service.search(searchText, offset, limit)));
        } else if ("POST".equalsIgnoreCase(method)) {
            Map<String, String> form = parseParameters(readBody(exchange));
            Contact contact = service.createContact(
                    form.get("name"), form.get("email"), form.get("phone"), form.get("notes"));
            exchange.getResponseHeaders().set("Location", "/contacts/" + contact.getId());
            sendJson(exchange, 201, JsonUtil.toJson(contact));
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, POST");
            sendJson(exchange, 405, JsonUtil.error("Method not allowed."));
        }
    }

    private void handleItem(HttpExchange exchange, String contactId) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            Contact contact = service.findContact(contactId);
            if (contact == null) {
                sendJson(exchange, 404, JsonUtil.error("Contact not found."));
            } else {
                sendJson(exchange, 200, JsonUtil.toJson(contact));
            }
        } else if ("PUT".equalsIgnoreCase(method)) {
            Map<String, String> form = parseParameters(readBody(exchange));
            Contact contact = service.updateContact(
                    contactId, form.get("name"), form.get("email"),
                    form.get("phone"), form.get("notes"));
            if (contact == null) {
                sendJson(exchange, 404, JsonUtil.error("Contact not found."));
            } else {
                sendJson(exchange, 200, JsonUtil.toJson(contact));
            }
        } else if ("DELETE".equalsIgnoreCase(method)) {
            if (!service.deleteContact(contactId)) {
                sendJson(exchange, 404, JsonUtil.error("Contact not found."));
            } else {
                exchange.sendResponseHeaders(204, -1);
            }
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, PUT, DELETE");
            sendJson(exchange, 405, JsonUtil.error("Method not allowed."));
        }
    }

    private int parseInteger(String value, int defaultValue, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be an integer.", exception);
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

    private Map<String, String> parseParameters(String text) throws IOException {
        Map<String, String> values = new LinkedHashMap<String, String>();
        if (text == null || text.trim().isEmpty()) {
            return values;
        }
        for (String pair : text.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";
            if (values.put(key, value) != null) {
                throw new IOException("Duplicate request field: " + key);
            }
        }
        return values;
    }

    private String decode(String value) throws IOException {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (IllegalArgumentException exception) {
            throw new IOException("Invalid URL-encoded data.", exception);
        }
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
    }
}
