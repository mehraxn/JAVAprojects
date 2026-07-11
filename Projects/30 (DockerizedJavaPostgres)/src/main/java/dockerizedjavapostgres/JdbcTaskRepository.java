package dockerizedjavapostgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class JdbcTaskRepository implements TaskRepository {
    private static final String INSERT_SQL =
            "INSERT INTO tasks (title, completed) VALUES (?, ?)";
    private static final String FIND_ALL_SQL =
            "SELECT id, title, completed FROM tasks ORDER BY id";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, title, completed FROM tasks WHERE id = ?";
    private static final String UPDATE_COMPLETED_SQL =
            "UPDATE tasks SET completed = ? WHERE id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM tasks WHERE id = ?";

    private final DatabaseConnection databaseConnection;

    public JdbcTaskRepository(DatabaseConnection databaseConnection) {
        if (databaseConnection == null) {
            throw new IllegalArgumentException("Database connection is required.");
        }
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Task create(Task task) throws SQLException {
        if (task == null) {
            throw new IllegalArgumentException("Task is required.");
        }
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, task.getTitle());
            statement.setBoolean(2, task.isCompleted());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Task insert did not affect exactly one row.");
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("PostgreSQL did not return a generated task ID.");
                }
                long id = keys.getLong(1);
                if (keys.wasNull() || id <= 0) {
                    throw new SQLException("PostgreSQL returned an invalid task ID.");
                }
                return new Task(id, task.getTitle(), task.isCompleted());
            }
        }
    }

    @Override
    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<Task>();
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                tasks.add(mapTask(results));
            }
        }
        return Collections.unmodifiableList(tasks);
    }

    @Override
    public Optional<Task> findById(long taskId) throws SQLException {
        requirePositiveId(taskId);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, taskId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next()
                        ? Optional.of(mapTask(results))
                        : Optional.<Task>empty();
            }
        }
    }

    @Override
    public boolean updateCompleted(long taskId, boolean completed) throws SQLException {
        requirePositiveId(taskId);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(UPDATE_COMPLETED_SQL)) {
            statement.setBoolean(1, completed);
            statement.setLong(2, taskId);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long taskId) throws SQLException {
        requirePositiveId(taskId);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, taskId);
            return statement.executeUpdate() == 1;
        }
    }

    private Task mapTask(ResultSet results) throws SQLException {
        try {
            return new Task(
                    results.getLong("id"),
                    results.getString("title"),
                    results.getBoolean("completed"));
        } catch (IllegalArgumentException exception) {
            throw new SQLException("Database contains an invalid task row.", exception);
        }
    }

    private void requirePositiveId(long taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Stored task ID must be positive.");
        }
    }
}
