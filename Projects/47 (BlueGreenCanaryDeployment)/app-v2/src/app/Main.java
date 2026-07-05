package app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * app-v2 — the "new" / "green" / "canary" version.
 *
 * Identical shape to app-v1 but reports v2 and adds one extra field to the "/"
 * response ("feature":"new-greeting") to represent a behavior change worth
 * canarying. It can be compiled and run locally; see TESTING.md.
 *
 *   GET /version  -> {"version":"v2"}
 *   GET /health   -> liveness
 *   GET /ready    -> readiness
 *   GET /         -> greeting + the new feature flag
 */
public final class Main {

    private static final String VERSION = env("APP_VERSION", "v2");

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(env("APP_PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/version", ex -> respond(ex, 200,
                "{\"version\":\"" + VERSION + "\"}"));
        server.createContext("/health", ex -> respond(ex, 200, "ok"));
        server.createContext("/ready", ex -> respond(ex, 200, "ready"));
        server.createContext("/", ex -> respond(ex, 200,
                "{\"message\":\"hello from " + VERSION + "\",\"version\":\"" + VERSION
                        + "\",\"feature\":\"new-greeting\"}"));
        server.start();
        System.out.println("app " + VERSION + " listening on :" + port);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange ex, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        String contentType = body.strip().startsWith("{")
                ? "application/json; charset=utf-8"
                : "text/plain; charset=utf-8";
        ex.getResponseHeaders().add("Content-Type", contentType);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }
}
