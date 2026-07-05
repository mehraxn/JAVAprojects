package dockerizedjavapostgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Database configuration is required.");
        }
        this.config = config;
    }

    public Connection open() throws SQLException {
        try {
            return DriverManager.getConnection(
                    config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException exception) {
            throw new SQLException(
                    "Could not connect to PostgreSQL. Check the JDBC driver and DB_URL, DB_USER, and DB_PASSWORD.",
                    exception.getSQLState(), exception.getErrorCode(), exception);
        }
    }
}
