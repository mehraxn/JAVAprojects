package taskmanagerjdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTaskRepository implements TaskRepository {
    private final Map<Long, Task> tasks = new LinkedHashMap<Long, Task>();
    private long nextId = 1;

    @Override
    public Task add(Task task) {
        requireTask(task);
        long id = task.getId() == 0 ? nextId : task.getId();
        if (tasks.containsKey(id)) {
            throw new IllegalArgumentException("A task with ID " + id + " already exists.");
        }

        Task storedTask = copyWithId(task, id);
        tasks.put(id, storedTask);
        if (id >= nextId) {
            nextId = id + 1;
        }
        return copy(storedTask);
    }

    @Override
    public Optional<Task> findById(long taskId) {
        requireStoredId(taskId);
        Task task = tasks.get(taskId);
        return task == null ? Optional.<Task>empty() : Optional.of(copy(task));
    }

    @Override
    public List<Task> findAll() {
        List<Task> result = new ArrayList<Task>();
        for (Task task : tasks.values()) {
            result.add(copy(task));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Task> findByStatus(Task.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null.");
        }
        List<Task> result = new ArrayList<Task>();
        for (Task task : tasks.values()) {
            if (task.getStatus() == status) {
                result.add(copy(task));
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean update(Task task) {
        requireTask(task);
        requireStoredId(task.getId());
        if (!tasks.containsKey(task.getId())) {
            return false;
        }
        tasks.put(task.getId(), copy(task));
        return true;
    }

    @Override
    public boolean delete(long taskId) {
        requireStoredId(taskId);
        return tasks.remove(taskId) != null;
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

    private Task copy(Task task) {
        return copyWithId(task, task.getId());
    }

    private Task copyWithId(Task task, long id) {
        return new Task(id, task.getTitle(), task.getDescription(), task.getDueDate(), task.getStatus());
    }
}
