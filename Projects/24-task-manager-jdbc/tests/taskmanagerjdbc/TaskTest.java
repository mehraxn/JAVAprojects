package taskmanagerjdbc;

import java.lang.reflect.Modifier;
import java.time.LocalDate;

public final class TaskTest {
    private TaskTest() {
    }

    static void run(Assert t) {
        LocalDate due = LocalDate.of(2026, 8, 1);

        // Valid creation and getters
        Task task = new Task(7, "Write report", "Quarterly numbers", due, Task.Status.OPEN);
        t.assertEquals(7L, task.getId(), "getId returns constructor value");
        t.assertEquals("Write report", task.getTitle(), "getTitle returns constructor value");
        t.assertEquals("Quarterly numbers", task.getDescription(),
                "getDescription returns constructor value");
        t.assertEquals(due, task.getDueDate(), "getDueDate returns constructor value");
        t.assertEquals(Task.Status.OPEN, task.getStatus(), "getStatus returns constructor value");

        t.assertTrue(Modifier.isFinal(Task.class.getModifiers()),
                "Task is final (fixes strict-compile this-escape warning)");

        // ID rules: negative rejected; 0 is the documented "not yet saved" sentinel
        t.assertThrows(IllegalArgumentException.class,
                () -> new Task(-1, "Title", "", null, Task.Status.OPEN),
                "negative ID rejected");
        t.assertEquals(0L, new Task(0, "Unsaved", "", null, Task.Status.OPEN).getId(),
                "ID 0 is accepted as the unsaved-task sentinel");

        // Title validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Task(1, null, "", null, Task.Status.OPEN), "null title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Task(1, "", "", null, Task.Status.OPEN), "empty title rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> new Task(1, "   ", "", null, Task.Status.OPEN),
                "whitespace-only title rejected");
        t.assertEquals("Trimmed", new Task(1, "  Trimmed  ", "", null, Task.Status.OPEN).getTitle(),
                "title is trimmed");

        // Description and due date are optional by design
        t.assertEquals("", new Task(1, "Title", null, null, Task.Status.OPEN).getDescription(),
                "null description stored as empty string");
        t.assertEquals("", new Task(1, "Title", "   ", null, Task.Status.OPEN).getDescription(),
                "blank description stored as empty string");
        t.assertNull(new Task(1, "Title", "", null, Task.Status.OPEN).getDueDate(),
                "null due date is allowed");

        // Status validation
        t.assertThrows(IllegalArgumentException.class,
                () -> new Task(1, "Title", "", null, null), "null status rejected");

        // updateDetails
        Task editable = new Task(2, "Old title", "Old description", due, Task.Status.OPEN);
        editable.updateDetails("New title", "New description", due.plusDays(3));
        t.assertEquals("New title", editable.getTitle(), "updateDetails changes title");
        t.assertEquals("New description", editable.getDescription(),
                "updateDetails changes description");
        t.assertEquals(due.plusDays(3), editable.getDueDate(), "updateDetails changes due date");
        t.assertEquals(2L, editable.getId(), "updateDetails never changes the ID");

        // Failed update must not partially mutate the task
        t.assertThrows(IllegalArgumentException.class,
                () -> editable.updateDetails("  ", "Other description", null),
                "update with blank title rejected");
        t.assertEquals("New title", editable.getTitle(), "failed update leaves title unchanged");
        t.assertEquals("New description", editable.getDescription(),
                "failed update leaves description unchanged");
        t.assertEquals(due.plusDays(3), editable.getDueDate(),
                "failed update leaves due date unchanged");

        // setStatus
        editable.setStatus(Task.Status.IN_PROGRESS);
        t.assertEquals(Task.Status.IN_PROGRESS, editable.getStatus(), "setStatus changes status");
        editable.setStatus(Task.Status.DONE);
        t.assertEquals(Task.Status.DONE, editable.getStatus(), "setStatus reaches DONE");
        t.assertThrows(IllegalArgumentException.class,
                () -> editable.setStatus(null), "null status update rejected");
        t.assertEquals(Task.Status.DONE, editable.getStatus(),
                "failed status update leaves status unchanged");

        // toString formats both due-date variants
        t.assertContains(new Task(3, "Dated", "", due, Task.Status.OPEN).toString(),
                "due 2026-08-01", "toString shows the due date");
        t.assertContains(new Task(3, "Undated", "", null, Task.Status.OPEN).toString(),
                "no due date", "toString handles a missing due date");

        // Status enum has exactly the documented values
        t.assertEquals(3, Task.Status.values().length,
                "Status enum has OPEN, IN_PROGRESS, DONE");
    }
}
