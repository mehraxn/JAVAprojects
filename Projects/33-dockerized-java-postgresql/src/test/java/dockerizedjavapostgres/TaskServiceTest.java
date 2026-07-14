package dockerizedjavapostgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * TaskService is tested against a small in-memory repository, so these unit
 * tests need no PostgreSQL. The real JDBC repository is exercised by the
 * Docker Compose workflow documented in TESTING.md.
 */
class TaskServiceTest {

    private static class InMemoryTaskRepository implements TaskRepository {
        private final List<Task> tasks = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Task create(Task task) {
            Task saved = new Task(nextId++, task.getTitle(), task.isCompleted());
            tasks.add(saved);
            return saved;
        }

        @Override
        public List<Task> findAll() {
            return new ArrayList<>(tasks);
        }

        @Override
        public Optional<Task> findById(long taskId) {
            return tasks.stream().filter(task -> task.getId() == taskId).findFirst();
        }

        @Override
        public boolean updateCompleted(long taskId, boolean completed) {
            Optional<Task> existing = findById(taskId);
            if (existing.isEmpty()) {
                return false;
            }
            tasks.replaceAll(task -> task.getId() == taskId
                    ? new Task(taskId, task.getTitle(), completed)
                    : task);
            return true;
        }

        @Override
        public boolean delete(long taskId) {
            return tasks.removeIf(task -> task.getId() == taskId);
        }
    }

    @Test
    void rejectsNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> new TaskService(null));
    }

    @Test
    void addTaskAssignsIdAndStartsUncompleted() throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        Task created = service.addTask("Learn Docker Compose");
        assertEquals(1, created.getId());
        assertEquals("Learn Docker Compose", created.getTitle());
        assertFalse(created.isCompleted());
    }

    @Test
    void addTaskRejectsBlankTitle() {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        assertThrows(IllegalArgumentException.class, () -> service.addTask("   "));
    }

    @Test
    void listTasksReturnsAllCreatedTasks() throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        service.addTask("First");
        service.addTask("Second");
        List<Task> tasks = service.listTasks();
        assertEquals(2, tasks.size());
        assertEquals("First", tasks.get(0).getTitle());
        assertEquals("Second", tasks.get(1).getTitle());
    }

    @Test
    void markCompletedUpdatesExistingTask() throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        Task created = service.addTask("Finish validation");
        assertTrue(service.markCompleted(created.getId()));
        assertTrue(service.findTask(created.getId()).orElseThrow().isCompleted());
    }

    @Test
    void markCompletedReturnsFalseForUnknownTask() throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        assertFalse(service.markCompleted(999));
    }

    @Test
    void deleteRemovesTask() throws SQLException {
        TaskService service = new TaskService(new InMemoryTaskRepository());
        Task created = service.addTask("Temporary task");
        assertTrue(service.deleteTask(created.getId()));
        assertTrue(service.listTasks().isEmpty());
    }
}
