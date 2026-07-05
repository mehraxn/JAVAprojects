package centralizedloggingstack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

public class StructuredLogger {
    private final Path logPath;

    public StructuredLogger(String configuredPath) {
        this.logPath = configuredPath == null || configuredPath.trim().isEmpty()
                ? null : Paths.get(configuredPath.trim());
    }

    public void info(String event, String message) throws IOException {
        String line = "{\"timestamp\":\"" + Instant.now()
                + "\",\"level\":\"INFO\",\"event\":\"" + escape(event)
                + "\",\"message\":\"" + escape(message) + "\"}";
        if (logPath == null) {
            System.out.println(line);
            return;
        }
        Path parent = logPath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(logPath, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        // TODO: Add size limits or rotation ownership before sustained use.
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
