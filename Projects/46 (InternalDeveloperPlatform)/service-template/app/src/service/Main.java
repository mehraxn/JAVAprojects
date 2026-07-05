package service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Golden-path Java service template.
 *
 * The package name is fixed as `service` so this template still COMPILES before
 * generation. Per-service values (name, port) come from environment variables
 * that the generator wires up via the __TOKEN__ placeholders in the Dockerfile,
 * Helm values, and GitOps manifests — not by editing this Java source. That
 * keeps the code identical across services and the config the only thing that
 * varies. Nothing here is built or run in this repo.
 *
 * Endpoints: GET /  GET /health  GET /ready
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        String serviceName = env("SERVICE_NAME", "java-service");
        int port = Integer.parseInt(env("SERVICE_PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", ex -> respond(ex, 200, "ok"));
        server.createContext("/ready", ex -> respond(ex, 200, "ready"));
        server.createContext("/", ex -> respond(ex, 200,
                "{\"service\":\"" + serviceName + "\",\"status\":\"up\"}"));
        server.start();
        System.out.println(serviceName + " listening on :" + port);
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
