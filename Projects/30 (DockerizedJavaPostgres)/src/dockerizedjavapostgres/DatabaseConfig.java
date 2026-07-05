package dockerizedjavapostgres;

public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig(String url, String username, String password) {
        this.url = requireValue(url, "Database URL");
        this.username = requireValue(username, "Database username");
        this.password = requireSecret(password, "Database password");
    }

    public static DatabaseConfig fromEnvironment() {
        return new DatabaseConfig(
                System.getenv("DB_URL"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD"));
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String describeWithoutPassword() {
        return "DatabaseConfig{url='" + url + "', username='" + username + "'}";
    }

    private static String requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value.trim();
    }

    private static String requireSecret(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return value;
    }
}
