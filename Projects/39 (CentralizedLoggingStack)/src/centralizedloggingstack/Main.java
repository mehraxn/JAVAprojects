package centralizedloggingstack;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        StructuredLogger logger = new StructuredLogger(
                System.getenv("LOG_FILE"),
                environmentOrDefault("APP_SERVICE", "java-app"),
                environmentOrDefault("APP_ENVIRONMENT", "learning"));

        logger.info("application_started", "Structured logging example started", "startup");
        long heartbeatNumber = 1;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                logger.info(
                        "heartbeat",
                        "Application heartbeat " + heartbeatNumber,
                        "heartbeat-" + heartbeatNumber);
                heartbeatNumber++;
                Thread.sleep(30_000L);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.info("application_stopping", "Structured logging example stopping", "shutdown");
        }
    }

    private static String environmentOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
