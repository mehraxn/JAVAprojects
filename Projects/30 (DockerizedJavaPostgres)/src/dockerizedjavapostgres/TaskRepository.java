package dockerizedjavapostgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task create(Task task) throws SQLException;

    List<Task> findAll() throws SQLException;

    Optional<Task> findById(long taskId) throws SQLException;

    boolean updateCompleted(long taskId, boolean completed) throws SQLException;

    boolean delete(long taskId) throws SQLException;
}
