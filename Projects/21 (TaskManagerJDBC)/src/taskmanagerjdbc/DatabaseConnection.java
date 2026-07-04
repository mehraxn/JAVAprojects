package taskmanagerjdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseConnection(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public Connection open() throws SQLException {
        // TODO: Validate configuration and call DriverManager.getConnection.
        throw new UnsupportedOperationException("TODO: open a JDBC connection");
    }
}
