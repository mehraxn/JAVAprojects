package gitopsdeploymentplatform;

public class AppConfig {
    private final int port;
    private final String environment;
    private final String version;

    private AppConfig(int port, String environment, String version) {
        this.port = port;
        this.environment = environment;
        this.version = version;
    }

    public static AppConfig fromEnvironment() {
        return new AppConfig(
                readPort(System.getenv("APP_PORT")),
                readText("APP_ENVIRONMENT", "unconfigured", 40),
                readText("APP_VERSION", "development", 80));
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
}
