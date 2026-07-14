package taskmanagerjdbc;

import java.time.LocalDate;
import java.util.List;

public final class TaskServiceTest {
    private TaskServiceTest() {
    }

    static void run(Assert t) throws Exception {
        t.assertThrows(IllegalArgumentException.class,
                () -> new TaskService(null), "null repository rejected");

        TaskService service = new TaskService(new InMemoryTaskRepository());
        LocalDate due = LocalDate.of(2026, 8, 15);

        // Create
        Task review = service.addTask("Review notes", "JDBC chapter", due);
        Task plan = service.addTask("Plan sprint", "", null);
        t.assertEquals(1L, review.getId(), "first task gets ID 1");
        t.assertEquals(2L, plan.getId(), "second task gets ID 2");
        t.assertEquals(Task.Status.OPEN, review.getStatus(), "new tasks start OPEN");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.addTask("", "description", null), "blank title rejected on create");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.addTask(null, "description", null), "null title rejected on create");
        t.assertEquals(2, service.listAllTasks().size(),
                "failed creations do not add tasks");

        // Find
        t.assertTrue(service.findTask(1).isPresent(), "findTask returns existing task");
        t.assertFalse(service.findTask(999).isPresent(),
                "findTask returns empty Optional for missing ID");

        // Update details
        t.assertTrue(service.updateTaskDetails(1, "Review all notes", "Both chapters",
                due.plusDays(1)), "updateTaskDetails returns true for existing task");
        t.assertEquals("Review all notes", service.findTask(1).get().getTitle(),
                "detail update is persisted");
        t.assertFalse(service.updateTaskDetails(999, "Ghost", "", null),
                "updateTaskDetails returns false for missing task");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.updateTaskDetails(1, "  ", "x", null),
                "invalid detail update rejected");
        t.assertEquals("Review all notes", service.findTask(1).get().getTitle(),
                "failed detail update does not corrupt the stored task");
        t.assertEquals("Both chapters", service.findTask(1).get().getDescription(),
                "failed detail update leaves description intact");

        // Update status
        t.assertTrue(service.updateTaskStatus(1, Task.Status.IN_PROGRESS),
                "updateTaskStatus returns true for existing task");
        t.assertEquals(Task.Status.IN_PROGRESS, service.findTask(1).get().getStatus(),
                "status update is persisted");
        t.assertFalse(service.updateTaskStatus(999, Task.Status.DONE),
                "updateTaskStatus returns false for missing task");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.updateTaskStatus(1, null), "null status rejected");
        t.assertEquals(Task.Status.IN_PROGRESS, service.findTask(1).get().getStatus(),
                "failed status update leaves stored status unchanged");

        // Filtering by every status
        service.addTask("Third", "", null);
        service.updateTaskStatus(3, Task.Status.DONE);
        t.assertEquals(1, service.searchByStatus(Task.Status.OPEN).size(),
                "OPEN filter matches only open tasks");
        t.assertEquals(2L, service.searchByStatus(Task.Status.OPEN).get(0).getId(),
                "OPEN filter finds the expected task");
        t.assertEquals(1, service.searchByStatus(Task.Status.IN_PROGRESS).size(),
                "IN_PROGRESS filter matches only in-progress tasks");
        t.assertEquals(1, service.searchByStatus(Task.Status.DONE).size(),
                "DONE filter matches only done tasks");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.searchByStatus(null), "null status filter rejected");

        // Defensive lists and copies
        List<Task> listed = service.listAllTasks();
        t.assertEquals(3, listed.size(), "listAllTasks returns all tasks");
        t.assertThrows(UnsupportedOperationException.class,
                () -> listed.add(review), "listAllTasks result is unmodifiable");
        listed.get(0).updateDetails("Hacked", "", null);
        t.assertEquals("Review all notes", service.findTask(1).get().getTitle(),
                "mutating a listed task does not change stored state");
        t.assertThrows(UnsupportedOperationException.class,
                () -> service.searchByStatus(Task.Status.OPEN).add(review),
                "searchByStatus result is unmodifiable");

        // Delete
        t.assertTrue(service.deleteTask(3), "delete of existing task returns true");
        t.assertFalse(service.deleteTask(3), "delete of missing task returns false");
        t.assertThrows(IllegalArgumentException.class,
                () -> service.deleteTask(0), "invalid ID rejected on delete");
        t.assertEquals(2, service.listAllTasks().size(), "two tasks remain after delete");

        // End-to-end workflow: create -> update -> status change -> filter -> delete
        TaskService flow = new TaskService(new InMemoryTaskRepository());
        Task item = flow.addTask("Workflow task", "start", null);
        flow.updateTaskDetails(item.getId(), "Workflow task v2", "revised", due);
        flow.updateTaskStatus(item.getId(), Task.Status.IN_PROGRESS);
        t.assertEquals(1, flow.searchByStatus(Task.Status.IN_PROGRESS).size(),
                "workflow: task is found via status filter after update");
        t.assertEquals("Workflow task v2",
                flow.searchByStatus(Task.Status.IN_PROGRESS).get(0).getTitle(),
                "workflow: filtered task carries the updated details");
        flow.updateTaskStatus(item.getId(), Task.Status.DONE);
        t.assertTrue(flow.deleteTask(item.getId()), "workflow: done task can be deleted");
        t.assertEquals(0, flow.listAllTasks().size(), "workflow: repository ends empty");
    }
}
