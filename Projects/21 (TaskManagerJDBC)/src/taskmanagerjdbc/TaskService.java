package taskmanagerjdbc;

import java.sql.SQLException;
import java.util.List;

public class TaskService {
    private final JdbcTaskRepository repository;

    public TaskService(JdbcTaskRepository repository) {
        this.repository = repository;
    }

    public Task createTask(String title, String description,
            java.time.LocalDate dueDate) throws SQLException {
        // TODO: Validate input and delegate task creation.
        throw new UnsupportedOperationException("TODO: create a task through the service");
    }

    public void markDone(long taskId) throws SQLException {
        // TODO: Load the task, change status, and persist it.
        throw new UnsupportedOperationException("TODO: mark a task done");
    }

    public List<Task> listTasks(Task.Status status) throws SQLException {
        // TODO: Return all tasks or filter by status.
        throw new UnsupportedOperationException("TODO: list tasks through the service");
    }
}
