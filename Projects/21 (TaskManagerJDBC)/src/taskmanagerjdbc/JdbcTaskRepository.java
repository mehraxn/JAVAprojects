package taskmanagerjdbc;

import java.sql.Connection;
import java.sql.Date;
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
            "INSERT INTO tasks (title, description, due_date, status) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, title, description, due_date, status FROM tasks WHERE id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT id, title, description, due_date, status FROM tasks ORDER BY id";
    private static final String FIND_BY_STATUS_SQL =
            "SELECT id, title, description, due_date, status FROM tasks WHERE status = ? ORDER BY id";
    private static final String UPDATE_SQL =
            "UPDATE tasks SET title = ?, description = ?, due_date = ?, status = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM tasks WHERE id = ?";

    private final DatabaseConnection databaseConnection;

    public JdbcTaskRepository(DatabaseConnection databaseConnection) {
        if (databaseConnection == null) {
            throw new IllegalArgumentException("Database connection cannot be null.");
        }
        this.databaseConnection = databaseConnection;
    }

    @Override
    public Task add(Task task) throws SQLException {
        requireTask(task);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(
                        INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            setTaskValues(statement, task);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Task insert did not affect exactly one row.");
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Database did not return a generated task ID.");
                }
                return new Task(keys.getLong(1), task.getTitle(), task.getDescription(),
                        task.getDueDate(), task.getStatus());
            }
        }
    }

    @Override
    public Optional<Task> findById(long taskId) throws SQLException {
        requireStoredId(taskId);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, taskId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapTask(results)) : Optional.<Task>empty();
            }
        }
    }

    @Override
    public List<Task> findAll() throws SQLException {
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
                ResultSet results = statement.executeQuery()) {
            return readTasks(results);
        }
    }

    @Override
    public List<Task> findByStatus(Task.Status status) throws SQLException {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_STATUS_SQL)) {
            statement.setString(1, status.name());
            try (ResultSet results = statement.executeQuery()) {
                return readTasks(results);
            }
        }
    }

    @Override
    public boolean update(Task task) throws SQLException {
        requireTask(task);
        requireStoredId(task.getId());
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setTaskValues(statement, task);
            statement.setLong(5, task.getId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long taskId) throws SQLException {
        requireStoredId(taskId);
        try (Connection connection = databaseConnection.open();
                PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, taskId);
            return statement.executeUpdate() == 1;
        }
    }

    private void setTaskValues(PreparedStatement statement, Task task) throws SQLException {
        statement.setString(1, task.getTitle());
        statement.setString(2, task.getDescription());
        if (task.getDueDate() == null) {
            statement.setNull(3, java.sql.Types.DATE);
        } else {
            statement.setDate(3, Date.valueOf(task.getDueDate()));
        }
        statement.setString(4, task.getStatus().name());
    }

    private List<Task> readTasks(ResultSet results) throws SQLException {
        List<Task> tasks = new ArrayList<Task>();
        while (results.next()) {
            tasks.add(mapTask(results));
        }
        return Collections.unmodifiableList(tasks);
    }

    private Task mapTask(ResultSet results) throws SQLException {
        Date dueDate = results.getDate("due_date");
        try {
            return new Task(
                    results.getLong("id"),
                    results.getString("title"),
                    results.getString("description"),
                    dueDate == null ? null : dueDate.toLocalDate(),
                    Task.Status.valueOf(results.getString("status")));
        } catch (IllegalArgumentException exception) {
            throw new SQLException("Database contains an invalid task value.", exception);
        }
    }

    private void requireTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
    }

    private void requireStoredId(long taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Stored task ID must be positive.");
        }
    }
}
