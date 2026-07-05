package service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Generated for payments-api. Identical to the template source: the service
 * name and port come from environment variables (set by the ConfigMap the Helm
 * chart renders), so the code is the same across every generated service.
 * NOT built or run.
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
