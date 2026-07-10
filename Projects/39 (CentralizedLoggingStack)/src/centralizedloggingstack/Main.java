package centralizedloggingstack;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        StructuredLogger logger = new StructuredLogger(
                System.getenv("LOG_FILE"),
                environmentOrDefault("APP_SERVICE", "java-app"),
                environmentOrDefault("APP_ENVIRONMENT", "learning"));

        long heartbeatIntervalMs = longEnvironmentOrDefault("HEARTBEAT_INTERVAL_MS", 30_000L, 100L, 3_600_000L);
        long maxEvents = longEnvironmentOrDefault("APP_MAX_EVENTS", 0L, 0L, 1_000_000L);
        long errorEveryN = longEnvironmentOrDefault("ERROR_EVERY_N", 0L, 0L, 1_000_000L);

        logger.info("application_started", "Structured logging example started", "startup");
        long emittedEvents = 1;
        long heartbeatNumber = 1;

        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (maxEvents > 0 && emittedEvents >= maxEvents) {
                    logger.info("application_stopping", "Structured logging example reached APP_MAX_EVENTS", "shutdown");
                    return;
                }

                if (errorEveryN > 0 && heartbeatNumber % errorEveryN == 0) {
                    logger.error(
                            "demo_error",
                            "Intentional demo error log for Loki alert testing " + heartbeatNumber,
                            "demo-error-" + heartbeatNumber);
                } else {
                    logger.info(
                            "heartbeat",
                            "Application heartbeat " + heartbeatNumber,
                            "heartbeat-" + heartbeatNumber);
                }
                emittedEvents++;
                heartbeatNumber++;
                Thread.sleep(heartbeatIntervalMs);
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

    private static long longEnvironmentOrDefault(String name, long defaultValue, long minimum, long maximum) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed < minimum || parsed > maximum) {
                throw new IllegalArgumentException(name + " must be between " + minimum + " and " + maximum + ".");
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(name + " must be a whole number.", exception);
        }
    }
}
