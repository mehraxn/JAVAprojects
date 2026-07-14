package blogapi;

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

public final class BlogHttpServer {
    private static final int MAX_REQUEST_BYTES = 131_072;
    private final BlogService service;
    private HttpServer server;

    public BlogHttpServer(BlogService service) {
        if (service == null) {
            throw new IllegalArgumentException("Blog service cannot be null.");
        }
        this.service = service;
    }

    public synchronized void start(int port) throws IOException {
        if (port < 0 || port > 65_535) {
            throw new IllegalArgumentException("Port must be between 0 and 65535.");
        }
        if (server != null) {
            throw new IllegalStateException("Server is already running.");
        }
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/users", this::handleUsers);
        server.createContext("/posts", this::handlePosts);
        server.createContext("/", this::handleNotFound);
        server.setExecutor(null);
        server.start();
    }

    public synchronized void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public synchronized int getPort() {
        if (server == null) {
            throw new IllegalStateException("Server is not running.");
        }
        return server.getAddress().getPort();
    }

    private void handleNotFound(HttpExchange exchange) throws IOException {
        try {
            send(exchange, 404, BlogJson.error("Endpoint not found."));
        } finally {
            exchange.close();
        }
    }

    private void handleUsers(HttpExchange exchange) throws IOException {
        try {
            if (!"/users".equals(exchange.getRequestURI().getPath())) {
                send(exchange, 404, BlogJson.error("Endpoint not found."));
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                send(exchange, 200, BlogJson.users(service.listUsers()));
            } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                Map<String, String> form = parseParameters(readBody(exchange));
                User user = service.createUser(form.get("name"));
                send(exchange, 201, BlogJson.user(user));
            } else {
                exchange.getResponseHeaders().set("Allow", "GET, POST");
                send(exchange, 405, BlogJson.error("Method not allowed."));
            }
        } catch (PayloadTooLargeException exception) {
            send(exchange, 413, BlogJson.error(exception.getMessage()));
        } catch (IllegalArgumentException | BadRequestException exception) {
            send(exchange, 400, BlogJson.error(exception.getMessage()));
        } catch (RuntimeException exception) {
            send(exchange, 500, BlogJson.error("Internal server error."));
        } finally {
            exchange.close();
        }
    }

    private void handlePosts(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if ("/posts".equals(path) || "/posts/".equals(path)) {
                handlePostCollection(exchange);
                return;
            }
            if (!path.startsWith("/posts/")) {
                send(exchange, 404, BlogJson.error("Endpoint not found."));
                return;
            }

            String remainder = path.substring("/posts/".length());
            String[] segments = remainder.split("/", -1);
            if (segments.length == 1 && !segments[0].isEmpty()) {
                handlePostItem(exchange, decode(segments[0]));
            } else if (segments.length == 2 && !segments[0].isEmpty()
                    && "comments".equals(segments[1])) {
                handleComments(exchange, decode(segments[0]));
            } else {
                send(exchange, 404, BlogJson.error("Endpoint not found."));
            }
        } catch (PayloadTooLargeException exception) {
            send(exchange, 413, BlogJson.error(exception.getMessage()));
        } catch (IllegalArgumentException | BadRequestException exception) {
            send(exchange, 400, BlogJson.error(exception.getMessage()));
        } catch (RuntimeException exception) {
            send(exchange, 500, BlogJson.error("Internal server error."));
        } finally {
            exchange.close();
        }
    }

    private void handlePostCollection(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> query = parseParameters(exchange.getRequestURI().getRawQuery());
            String search = query.get("q");
            send(exchange, 200, BlogJson.posts(search == null || search.trim().isEmpty()
                    ? service.listPosts() : service.searchPosts(search)));
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> form = parseParameters(readBody(exchange));
            Post post = service.createPost(
                    form.get("authorId"), form.get("title"), form.get("content"));
            exchange.getResponseHeaders().set("Location", "/posts/" + post.getId());
            send(exchange, 201, BlogJson.post(post));
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, POST");
            send(exchange, 405, BlogJson.error("Method not allowed."));
        }
    }

    private void handlePostItem(HttpExchange exchange, String postId) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            Post post = service.findPost(postId);
            send(exchange, post == null ? 404 : 200,
                    post == null ? BlogJson.error("Post not found.") : BlogJson.post(post));
        } else if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> form = parseParameters(readBody(exchange));
            Post post = service.updatePost(postId, form.get("title"), form.get("content"));
            send(exchange, post == null ? 404 : 200,
                    post == null ? BlogJson.error("Post not found.") : BlogJson.post(post));
        } else if ("DELETE".equalsIgnoreCase(exchange.getRequestMethod())) {
            if (service.deletePost(postId)) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                send(exchange, 404, BlogJson.error("Post not found."));
            }
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, PUT, DELETE");
            send(exchange, 405, BlogJson.error("Method not allowed."));
        }
    }

    private void handleComments(HttpExchange exchange, String postId) throws IOException {
        if (service.findPost(postId) == null) {
            send(exchange, 404, BlogJson.error("Post not found."));
        } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            send(exchange, 200, BlogJson.comments(service.listComments(postId)));
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, String> form = parseParameters(readBody(exchange));
            Comment comment = service.addComment(postId, form.get("authorId"), form.get("body"));
            send(exchange, 201, BlogJson.comment(comment));
        } else {
            exchange.getResponseHeaders().set("Allow", "GET, POST");
            send(exchange, 405, BlogJson.error("Method not allowed."));
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

    private Map<String, String> parseParameters(String text) throws BadRequestException {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (text == null || text.trim().isEmpty()) {
            return result;
        }
        for (String pair : text.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length == 2 ? decode(parts[1]) : "";
            if (result.put(key, value) != null) {
                throw new BadRequestException("Duplicate request field: " + key);
            }
        }
        return result;
    }

    private String decode(String value) throws BadRequestException {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Invalid URL-encoded data.", exception);
        }
    }

    private void send(HttpExchange exchange, int status, String json) throws IOException {
        byte[] body = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        exchange.getResponseBody().write(body);
    }

    private static class BadRequestException extends IOException {
        private static final long serialVersionUID = 1L;
        BadRequestException(String message) { super(message); }
        BadRequestException(String message, Throwable cause) { super(message, cause); }
    }

    private static final class PayloadTooLargeException extends IOException {
        private static final long serialVersionUID = 1L;
        PayloadTooLargeException(String message) { super(message); }
    }
}
