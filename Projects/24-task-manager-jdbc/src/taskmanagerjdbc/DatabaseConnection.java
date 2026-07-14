package taskmanagerjdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseConnection(String jdbcUrl, String username, String password) {
        if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("JDBC URL cannot be empty.");
        }
        this.jdbcUrl = jdbcUrl.trim();
        this.username = username == null ? "" : username;
        this.password = password == null ? "" : password;
    }

    public Connection open() throws SQLException {
        try {
            if (username.trim().isEmpty()) {
                return DriverManager.getConnection(jdbcUrl);
            }
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException exception) {
            throw new SQLException(
                    "Could not open the JDBC connection. Check that the driver, URL, database, and credentials are available.",
                    exception.getSQLState(), exception.getErrorCode(), exception);
        }
    }
}
