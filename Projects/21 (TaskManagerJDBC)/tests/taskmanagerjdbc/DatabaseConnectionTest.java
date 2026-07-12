package taskmanagerjdbc;

import java.sql.SQLException;

public final class DatabaseConnectionTest {
    private DatabaseConnectionTest() {
    }

    static void run(Assert t) {
        // Constructor validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConnection(null, "user", "password"),
                "null JDBC URL rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConnection("", "user", "password"),
                "empty JDBC URL rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new DatabaseConnection("   ", "user", "password"),
                "whitespace-only JDBC URL rejected");
        t.assertNotNull(new DatabaseConnection("jdbc:example://localhost/db", null, null),
                "null username/password are tolerated at construction time");

        // Opening a connection with no driver installed fails with a wrapped,
        // helpful SQLException. This does NOT test a real database - it tests
        // the failure path, which is the only path runnable without a driver.
        DatabaseConnection noDriver = new DatabaseConnection(
                "jdbc:nosuchdriver://localhost/tasks", "demo-user", "demo-password-XYZZY");
        try {
            noDriver.open();
            t.assertTrue(false, "open() without a driver should have thrown SQLException");
        } catch (SQLException exception) {
            t.assertContains(exception.getMessage(), "Could not open the JDBC connection",
                    "failure is wrapped with clear context");
            t.assertContains(exception.getMessage(), "driver",
                    "failure message mentions the driver as a likely cause");
            t.assertFalse(exception.getMessage().contains("demo-password-XYZZY"),
                    "failure message does not expose the password");
            t.assertNotNull(exception.getCause(), "original SQLException is preserved as cause");
        }

        // Same failure path when connecting without credentials
        DatabaseConnection anonymous = new DatabaseConnection("jdbc:nosuchdriver://x/y", "", "");
        t.assertThrows(SQLException.class, anonymous::open,
                "credential-less open without a driver also fails with SQLException");
    }
}
