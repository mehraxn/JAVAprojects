package app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Golden-path Java service.
 *
 * A tiny dependency-free HTTP service built on the JDK's built-in
 * com.sun.net.httpserver. The service name and port are read from environment
 * variables so the same code runs unchanged in every environment; only the
 * configuration (ConfigMap / Docker ENV) differs.
 *
 * Endpoints:
 *   GET /        -> {"service":"...","message":"hello from ..."}
 *   GET /health  -> {"status":"ok","service":"..."}
 *   GET /ready   -> {"status":"ready","service":"..."}
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        String serviceName = env("SERVICE_NAME", "__SERVICE_NAME__");
        int port = readPort("SERVICE_PORT", "__SERVICE_PORT__");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", ex ->
                respondJson(ex, 200, "{\"status\":\"ok\",\"service\":\"" + serviceName + "\"}"));
        server.createContext("/ready", ex ->
                respondJson(ex, 200, "{\"status\":\"ready\",\"service\":\"" + serviceName + "\"}"));
        server.createContext("/", ex -> {
            // The "/" context also catches unknown paths; keep 404s honest.
            if (!"/".equals(ex.getRequestURI().getPath())) {
                respondJson(ex, 404, "{\"error\":\"not found\"}");
                return;
            }
            respondJson(ex, 200, "{\"service\":\"" + serviceName
                    + "\",\"message\":\"hello from " + serviceName + "\"}");
        });
        server.start();
        System.out.println(serviceName + " listening on port " + port);
    }

    /** Sends a JSON body with the correct Content-Type. */
    private static void respondJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }

    /** Reads a port from the environment and fails fast with a clear message if it is invalid. */
    private static int readPort(String name, String fallback) {
        String raw = env(name, fallback);
        int port;
        try {
            port = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a number between 1 and 65535, got: " + raw);
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(name + " must be between 1 and 65535, got: " + port);
        }
        return port;
    }
}
