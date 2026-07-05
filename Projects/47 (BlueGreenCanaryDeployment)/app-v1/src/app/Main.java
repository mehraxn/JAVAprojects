package app;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * app-v1 — the "stable" / "blue" version.
 *
 * A version-reporting HTTP service. In blue-green and canary demos the whole
 * point is that clients can tell which version served them, so traffic shifting
 * is observable. This app is NOT built or run in this repo.
 *
 *   GET /version  -> {"version":"v1"}
 *   GET /health   -> liveness
 *   GET /ready    -> readiness
 *   GET /         -> greeting including the version
 *
 * The reported version comes from APP_VERSION (set per Deployment) with a
 * compiled-in default, so the same code can back any track.
 */
public final class Main {

    private static final String VERSION = env("APP_VERSION", "v1");

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
                "{\"message\":\"hello from " + VERSION + "\",\"version\":\"" + VERSION + "\"}"));
        server.start();
        System.out.println("app " + VERSION + " listening on :" + port);
    }

    private static void respond(com.sun.net.httpserver.HttpExchange ex, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
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
}
