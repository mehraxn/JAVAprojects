package dockerizedjavapostgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Task repository is required.");
        }
        this.repository = repository;
    }

    public Task addTask(String title) throws SQLException {
        return repository.create(new Task(0, title, false));
    }

    public List<Task> listTasks() throws SQLException {
        return repository.findAll();
    }

    public Optional<Task> findTask(long taskId) throws SQLException {
        return repository.findById(taskId);
    }

    public boolean markCompleted(long taskId) throws SQLException {
        return repository.updateCompleted(taskId, true);
    }

    public boolean deleteTask(long taskId) throws SQLException {
        return repository.delete(taskId);
    }
}
