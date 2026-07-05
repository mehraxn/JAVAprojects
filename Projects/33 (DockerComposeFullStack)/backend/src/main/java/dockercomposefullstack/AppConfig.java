package dockercomposefullstack;

public class AppConfig {
    private final int port;
    private final String databaseUrl;
    private final String databaseUser;
    private final String databasePassword;

    private AppConfig(int port, String databaseUrl, String databaseUser,
            String databasePassword) {
        this.port = port;
        this.databaseUrl = databaseUrl;
        this.databaseUser = databaseUser;
        this.databasePassword = databasePassword;
    }

    public static AppConfig fromEnvironment() {
        int port = readPort(System.getenv("APP_PORT"));
        String databaseUrl = requireEnvironmentVariable("DB_URL");
        String databaseUser = requireEnvironmentVariable("DB_USER");
        String databasePassword = requireEnvironmentVariable("DB_PASSWORD");
        return new AppConfig(port, databaseUrl, databaseUser, databasePassword);
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

    private static String requireEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set.");
        }
        return value.trim();
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }
}
