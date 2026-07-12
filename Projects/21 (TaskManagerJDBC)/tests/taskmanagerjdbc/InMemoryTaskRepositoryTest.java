package taskmanagerjdbc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public final class InMemoryTaskRepositoryTest {
    private InMemoryTaskRepositoryTest() {
    }

    static void run(Assert t) throws Exception {
        InMemoryTaskRepository repository = new InMemoryTaskRepository();

        // Empty repository
        t.assertEquals(0, repository.findAll().size(), "new repository is empty");
        t.assertEquals(0, repository.findByStatus(Task.Status.OPEN).size(),
                "status filter on empty repository is empty");
        t.assertFalse(repository.findById(1).isPresent(),
                "find on empty repository returns empty Optional");
        t.assertFalse(repository.delete(1), "delete on empty repository returns false");

        // Save with generated sequential IDs (ID 0 = "assign one for me")
        Task first = repository.add(new Task(0, "First", "", null, Task.Status.OPEN));
        Task second = repository.add(new Task(0, "Second", "", null, Task.Status.OPEN));
        t.assertEquals(1L, first.getId(), "first generated ID is 1");
        t.assertEquals(2L, second.getId(), "generated IDs are sequential");

        // Explicit IDs are respected and duplicates rejected
        Task explicit = repository.add(new Task(10, "Explicit", "", null, Task.Status.OPEN));
        t.assertEquals(10L, explicit.getId(), "explicit ID is preserved");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.add(new Task(10, "Duplicate", "", null, Task.Status.OPEN)),
                "duplicate explicit ID rejected");
        t.assertEquals(11L, repository.add(new Task(0, "Next", "", null, Task.Status.OPEN)).getId(),
                "generated IDs continue after the highest explicit ID");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.add(null), "adding null task rejected");

        // Find
        Optional<Task> found = repository.findById(1);
        t.assertTrue(found.isPresent(), "findById returns saved task");
        t.assertEquals("First", found.get().getTitle(), "found task has saved data");
        t.assertFalse(repository.findById(999).isPresent(),
                "findById for missing ID returns empty Optional");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById(0), "ID 0 rejected on find");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById(-5), "negative ID rejected on find");

        // findAll: order, completeness, unmodifiable
        List<Task> all = repository.findAll();
        t.assertEquals(4, all.size(), "findAll returns all tasks");
        t.assertEquals(1L, all.get(0).getId(), "findAll preserves insertion order");
        t.assertThrows(UnsupportedOperationException.class,
                () -> all.add(first), "findAll list is unmodifiable");

        // findByStatus
        repository.update(withStatus(repository.findById(2).get(), Task.Status.DONE));
        List<Task> open = repository.findByStatus(Task.Status.OPEN);
        List<Task> done = repository.findByStatus(Task.Status.DONE);
        t.assertEquals(3, open.size(), "findByStatus returns only matching tasks");
        t.assertEquals(1, done.size(), "findByStatus finds the DONE task");
        t.assertEquals(2L, done.get(0).getId(), "the DONE task is the updated one");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findByStatus(null), "null status filter rejected");
        t.assertThrows(UnsupportedOperationException.class,
                () -> open.add(first), "findByStatus list is unmodifiable");

        // Defensive copies: mutating returned tasks must not change stored state
        repository.findById(1).get().setStatus(Task.Status.DONE);
        t.assertEquals(Task.Status.OPEN, repository.findById(1).get().getStatus(),
                "mutating a found task does not change the stored record");
        all.get(0).updateDetails("Hacked", "", null);
        t.assertEquals("First", repository.findById(1).get().getTitle(),
                "mutating a listed task does not change the stored record");
        Task original = new Task(0, "Original", "", null, Task.Status.OPEN);
        Task stored = repository.add(original);
        original.updateDetails("Changed after add", "", null);
        t.assertEquals("Original", repository.findById(stored.getId()).get().getTitle(),
                "mutating the input task after add does not change the stored record");

        // Update
        Task updated = new Task(1, "First updated", "New description",
                LocalDate.of(2026, 9, 1), Task.Status.IN_PROGRESS);
        t.assertTrue(repository.update(updated), "update of existing task returns true");
        t.assertEquals("First updated", repository.findById(1).get().getTitle(),
                "update replaces stored data");
        t.assertFalse(repository.update(new Task(999, "Ghost", "", null, Task.Status.OPEN)),
                "update of missing task returns false");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.update(null), "updating null task rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.update(new Task(0, "Unsaved", "", null, Task.Status.OPEN)),
                "updating a task with the unsaved ID 0 rejected");

        // Delete
        t.assertTrue(repository.delete(10), "delete of existing task returns true");
        t.assertFalse(repository.findById(10).isPresent(), "deleted task is gone");
        t.assertFalse(repository.delete(10), "second delete returns false");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.delete(0), "ID 0 rejected on delete");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.delete(-1), "negative ID rejected on delete");

        // ID exhaustion: after storing Long.MAX_VALUE, no positive IDs remain
        InMemoryTaskRepository exhausted = new InMemoryTaskRepository();
        exhausted.add(new Task(Long.MAX_VALUE, "Last possible", "", null, Task.Status.OPEN));
        t.assertThrows(IllegalStateException.class,
                () -> exhausted.add(new Task(0, "One too many", "", null, Task.Status.OPEN)),
                "generated-ID exhaustion fails cleanly instead of producing invalid IDs");
        t.assertEquals(1, exhausted.findAll().size(),
                "failed exhausted add does not store a task");
    }

    private static Task withStatus(Task task, Task.Status status) {
        Task copy = new Task(task.getId(), task.getTitle(), task.getDescription(),
                task.getDueDate(), task.getStatus());
        copy.setStatus(status);
        return copy;
    }
}
