package dockerizedjavapostgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class TaskTest {

    @Test
    void acceptsValidTask() {
        Task task = new Task(1, "Write documentation", false);
        assertEquals(1, task.getId());
        assertEquals("Write documentation", task.getTitle());
        assertFalse(task.isCompleted());
    }

    @Test
    void trimsTitle() {
        assertEquals("Trimmed", new Task(1, "  Trimmed  ", true).getTitle());
    }

    @Test
    void acceptsTitleAtMaximumLength() {
        String longestTitle = "a".repeat(200);
        assertEquals(longestTitle, new Task(1, longestTitle, false).getTitle());
    }

    @Test
    void rejectsTitleOverMaximumLength() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task(1, "a".repeat(201), false));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsBlankTitle(String title) {
        assertThrows(IllegalArgumentException.class, () -> new Task(1, title, false));
    }

    @Test
    void rejectsNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> new Task(-1, "Valid", false));
    }

    @Test
    void allowsZeroIdForUnsavedTasks() {
        assertEquals(0, new Task(0, "Not saved yet", false).getId());
    }

    @Test
    void reportsCompletedFlag() {
        assertTrue(new Task(2, "Done task", true).isCompleted());
    }
}
