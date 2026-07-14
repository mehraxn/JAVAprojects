package taskmanagerjdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task add(Task task) throws SQLException;

    Optional<Task> findById(long taskId) throws SQLException;

    List<Task> findAll() throws SQLException;

    List<Task> findByStatus(Task.Status status) throws SQLException;

    boolean update(Task task) throws SQLException;

    boolean delete(long taskId) throws SQLException;
}
