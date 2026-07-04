package taskmanagerjdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Task repository cannot be null.");
        }
        this.repository = repository;
    }

    public Task addTask(String title, String description, LocalDate dueDate) throws SQLException {
        return repository.add(new Task(0, title, description, dueDate, Task.Status.OPEN));
    }

    public boolean updateTaskStatus(long taskId, Task.Status status) throws SQLException {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        Optional<Task> existing = repository.findById(taskId);
        if (!existing.isPresent()) {
            return false;
        }
        Task task = existing.get();
        task.setStatus(status);
        return repository.update(task);
    }

    public boolean deleteTask(long taskId) throws SQLException {
        return repository.delete(taskId);
    }

    public List<Task> listAllTasks() throws SQLException {
        return repository.findAll();
    }

    public List<Task> searchByStatus(Task.Status status) throws SQLException {
        return repository.findByStatus(status);
    }
}
