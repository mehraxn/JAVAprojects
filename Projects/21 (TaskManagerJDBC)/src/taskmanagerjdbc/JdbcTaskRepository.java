package taskmanagerjdbc;

import java.sql.SQLException;
import java.util.List;

public class JdbcTaskRepository {
    private final DatabaseConnection databaseConnection;

    public JdbcTaskRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public Task create(Task task) throws SQLException {
        // TODO: Insert a task with PreparedStatement and return its stored form.
        throw new UnsupportedOperationException("TODO: create a task");
    }

    public List<Task> findAll() throws SQLException {
        // TODO: Query tasks and map ResultSet rows to Task objects.
        throw new UnsupportedOperationException("TODO: list tasks");
    }

    public List<Task> findByStatus(Task.Status status) throws SQLException {
        // TODO: Query tasks using a status parameter.
        throw new UnsupportedOperationException("TODO: filter tasks");
    }

    public boolean update(Task task) throws SQLException {
        // TODO: Update task fields using a PreparedStatement.
        throw new UnsupportedOperationException("TODO: update a task");
    }

    public boolean delete(long taskId) throws SQLException {
        // TODO: Delete a task by ID.
        throw new UnsupportedOperationException("TODO: delete a task");
    }
}
