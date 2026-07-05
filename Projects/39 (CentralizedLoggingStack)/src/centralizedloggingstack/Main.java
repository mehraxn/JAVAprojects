package centralizedloggingstack;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        StructuredLogger logger = new StructuredLogger(System.getenv("LOG_FILE"));
        try {
            logger.info("application_started", "Centralized logging starter ready");
            // TODO: Add safe request and error events without sensitive data.
        } catch (IOException exception) {
            System.err.println("Could not write starter log: " + exception.getMessage());
        }
    }
}
