package centralizedloggingstack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;

public class StructuredLogger {
    private static final Set<String> LEVELS = Set.of("DEBUG", "INFO", "WARN", "ERROR");

    private final Path logPath;
    private final String service;
    private final String environment;

    public StructuredLogger(String configuredPath, String service, String environment) {
        this.logPath = configuredPath == null || configuredPath.isBlank()
                ? null : Paths.get(configuredPath.trim());
        this.service = validateField("service", service, 60);
        this.environment = validateField("environment", environment, 40);
    }

    public void info(String event, String message, String traceId) throws IOException {
        log("INFO", event, message, traceId);
    }

    public void error(String event, String message, String traceId) throws IOException {
        log("ERROR", event, message, traceId);
    }

    public synchronized void log(String level, String event, String message, String traceId)
            throws IOException {
        String cleanLevel = validateLevel(level);
        String cleanEvent = validateEvent(event);
        String cleanMessage = validateField("message", message, 1_000);
        String cleanTraceId = validateField("traceId", traceId, 100);

        String line = "{\"timestamp\":\"" + Instant.now()
                + "\",\"level\":\"" + cleanLevel
                + "\",\"service\":\"" + escape(service)
                + "\",\"environment\":\"" + escape(environment)
                + "\",\"event\":\"" + escape(cleanEvent)
                + "\",\"message\":\"" + escape(cleanMessage)
                + "\",\"traceId\":\"" + escape(cleanTraceId) + "\"}";

        if (logPath == null) {
            System.out.println(line);
            return;
        }
        Path parent = logPath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(
                logPath,
                line + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    private String validateLevel(String level) {
        String cleanLevel = validateField("level", level, 10).toUpperCase(Locale.ROOT);
        if (!LEVELS.contains(cleanLevel)) {
            throw new IllegalArgumentException("Unsupported log level: " + cleanLevel);
        }
        return cleanLevel;
    }

    private String validateEvent(String event) {
        String cleanEvent = validateField("event", event, 80);
        if (!cleanEvent.matches("[a-z][a-z0-9_]*")) {
            throw new IllegalArgumentException("event must use lowercase snake_case.");
        }
        return cleanEvent;
    }

    private String validateField(String name, String value, int maximumLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " is required.");
        }
        String cleanValue = value.trim();
        if (cleanValue.length() > maximumLength) {
            throw new IllegalArgumentException(name + " exceeds " + maximumLength + " characters.");
        }
        return cleanValue;
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
