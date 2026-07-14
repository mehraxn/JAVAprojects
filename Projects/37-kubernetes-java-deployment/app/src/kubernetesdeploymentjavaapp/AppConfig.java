package kubernetesdeploymentjavaapp;

public class AppConfig {
    private final int port;
    private final String environment;
    private final String version;
    private final String message;
    private final boolean secretConfigured;

    private AppConfig(int port, String environment, String version, String message,
            boolean secretConfigured) {
        this.port = port;
        this.environment = environment;
        this.version = version;
        this.message = message;
        this.secretConfigured = secretConfigured;
    }

    public static AppConfig fromEnvironment() {
        // The optional Secret value is read only to detect its presence.
        // It is deliberately never stored or exposed by the application.
        String demoToken = System.getenv("APP_DEMO_TOKEN");
        return new AppConfig(
                readPort(System.getenv("APP_PORT")),
                readText("APP_ENVIRONMENT", "learning", 40),
                readText("APP_VERSION", "0.1.0", 40),
                readText("APP_MESSAGE", "Hello from Kubernetes", 200),
                demoToken != null && !demoToken.isBlank());
    }

    private static int readPort(String value) {
        if (value == null || value.isBlank()) {
            return 8080;
        }
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65_535) {
                throw new IllegalArgumentException("APP_PORT must be between 1 and 65535.");
            }
            return port;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("APP_PORT must be a whole number.", exception);
        }
    }

    private static String readText(String name, String defaultValue, int maximumLength) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        String cleanValue = value.trim();
        if (cleanValue.length() > maximumLength) {
            throw new IllegalArgumentException(name + " is too long.");
        }
        return cleanValue;
    }

    public int getPort() {
        return port;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getVersion() {
        return version;
    }

    public String getMessage() {
        return message;
    }

    /** Whether the optional APP_DEMO_TOKEN secret is set — never its value. */
    public boolean isSecretConfigured() {
        return secretConfigured;
    }
}
