package multienvironmentcloudnativeapp;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Config-aware cloud-native service. The SAME immutable image runs in every
 * environment; only injected configuration differs. That is the core idea of a
 * multi-environment setup: build once, configure per environment, promote the
 * exact same artifact forward.
 *
 * All configuration arrives through environment variables (populated by the
 * per-environment ConfigMap; secrets, when present, come from a Secret — never
 * baked into the image). This app is NOT built or run in this repo.
 *
 *   GET /health   liveness
 *   GET /ready    readiness
 *   GET /config   non-secret effective config (proves which env is active)
 *   GET /         greeting
 */
public final class Main {

    private static final String ENVIRONMENT = env("APP_ENVIRONMENT", "unconfigured");
    private static final String LOG_LEVEL = env("LOG_LEVEL", "INFO");
    private static final String FEATURE_FLAG = env("FEATURE_NEW_UI", "false");

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(env("APP_PORT", "8080"));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", ex -> respond(ex, 200, "ok"));
        server.createContext("/ready", ex -> respond(ex, 200, "ready"));
        server.createContext("/config", ex -> respond(ex, 200,
                "{\"environment\":\"" + ENVIRONMENT + "\","
                        + "\"logLevel\":\"" + LOG_LEVEL + "\","
                        + "\"featureNewUi\":" + booleanOf(FEATURE_FLAG) + "}"));
        server.createContext("/", ex -> respond(ex, 200,
                "{\"message\":\"cloud-native app\",\"environment\":\"" + ENVIRONMENT + "\"}"));
        server.start();
        System.out.println("cloud-native app [" + ENVIRONMENT + "] on :" + port);
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

    private static String booleanOf(String value) {
        return String.valueOf("true".equalsIgnoreCase(value));
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }
}
