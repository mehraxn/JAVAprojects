package dockercomposefullstack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.Executors;

public class ApiServer {
    private final HttpServer server;
    private final NoteRepository repository;

    public ApiServer(int port, NoteRepository repository) throws IOException {
        if (repository == null) {
            throw new IllegalArgumentException("Note repository is required.");
        }
        this.repository = repository;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        // HttpServer contexts are prefix-matched ("/health" would also catch
        // "/health/test"), so every handler checks the exact path, and the
        // "/" context turns every unmatched path into a JSON 404.
        server.createContext("/health", this::handleHealth);
        server.createContext("/api/notes", this::handleNotes);
        server.createContext("/", exchange -> notFound(exchange));
        server.setExecutor(Executors.newFixedThreadPool(4));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    private void handleHealth(HttpExchange exchange) throws IOException {
        if (!exactPath(exchange, "/health")) {
            notFound(exchange);
            return;
        }
        if (!method(exchange, "GET")) {
            methodNotAllowed(exchange, "GET");
            return;
        }
        boolean databaseAvailable = repository.databaseIsAvailable();
        int status = databaseAvailable ? 200 : 503;
        String value = databaseAvailable ? "UP" : "DATABASE_UNAVAILABLE";
        sendJson(exchange, status, JsonUtil.message("status", value));
    }

    private void handleNotes(HttpExchange exchange) throws IOException {
        if (!exactPath(exchange, "/api/notes")) {
            notFound(exchange);
            return;
        }
        try {
            switch (exchange.getRequestMethod()) {
                case "GET" -> sendJson(exchange, 200, JsonUtil.notes(repository.findAll()));
                case "POST" -> createNote(exchange);
                default -> methodNotAllowed(exchange, "GET, POST");
            }
        } catch (IllegalArgumentException exception) {
            sendJson(exchange, 400, JsonUtil.message("error", exception.getMessage()));
        } catch (SQLException exception) {
            // Never leak driver details or stack traces to clients.
            sendJson(exchange, 503, JsonUtil.message("error", "Database unavailable."));
        }
    }

    private void createNote(HttpExchange exchange) throws IOException, SQLException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        String text = JsonUtil.extractStringField(body, "text");
        if (text == null) {
            sendJson(exchange, 400, JsonUtil.message("error",
                    "Request body must be JSON like {\"text\":\"your note\"}."));
            return;
        }
        Note note = repository.add(text);
        sendJson(exchange, 201, JsonUtil.note(note));
    }

    private static boolean exactPath(HttpExchange exchange, String expected) {
        return expected.equals(exchange.getRequestURI().getPath());
    }

    private static boolean method(HttpExchange exchange, String expected) {
        return expected.equals(exchange.getRequestMethod());
    }

    private void notFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, 404, JsonUtil.message("error", "Not found"));
    }

    private void methodNotAllowed(HttpExchange exchange, String allowedMethods) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowedMethods);
        sendJson(exchange, 405, JsonUtil.message("error", "Method not allowed"));
    }

    private void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }
}
