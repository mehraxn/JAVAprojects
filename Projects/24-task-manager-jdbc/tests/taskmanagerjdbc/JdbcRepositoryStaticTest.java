package taskmanagerjdbc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Argument validation runs before any connection is opened, so those checks are
 * genuinely executable without a driver. The source-inspection checks support a
 * static safety review only - they do NOT prove real database behavior.
 */
public final class JdbcRepositoryStaticTest {
    private JdbcRepositoryStaticTest() {
    }

    static void run(Assert t) throws Exception {
        // Executable argument validation (throws before any JDBC work happens)
        t.assertThrows(IllegalArgumentException.class,
                () -> new JdbcTaskRepository(null), "null DatabaseConnection rejected");

        JdbcTaskRepository repository = new JdbcTaskRepository(
                new DatabaseConnection("jdbc:nosuchdriver://localhost/tasks", "", ""));
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.add(null), "add(null) rejected before touching JDBC");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById(0), "findById(0) rejected before touching JDBC");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findById(-1), "findById(-1) rejected before touching JDBC");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.findByStatus(null), "findByStatus(null) rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.update(null), "update(null) rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.update(new Task(0, "Unsaved", "", null, Task.Status.OPEN)),
                "update of unsaved task (ID 0) rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> repository.delete(0), "delete(0) rejected");

        // Static source inspection (run from the project root)
        Path source = Paths.get("src", "taskmanagerjdbc", "JdbcTaskRepository.java");
        t.assertTrue(Files.isRegularFile(source),
                "JdbcTaskRepository.java found (run tests from the project root)");
        String code = new String(Files.readAllBytes(source), StandardCharsets.UTF_8);

        t.assertContains(code, "PreparedStatement",
                "source uses PreparedStatement");
        t.assertContains(code, "try (",
                "source uses try-with-resources");
        t.assertContains(code, "Statement.RETURN_GENERATED_KEYS",
                "source requests generated keys on insert");
        t.assertContains(code, "getGeneratedKeys",
                "source reads the generated keys after insert");
        t.assertContains(code, "VALUES (?, ?, ?, ?)",
                "insert SQL uses ? placeholders");
        t.assertContains(code, "WHERE id = ?",
                "lookup SQL uses ? placeholders");
        t.assertFalse(code.contains("executeQuery(\"") || code.contains("executeUpdate(\""),
                "no inline SQL strings are passed to execute methods");
        t.assertFalse(code.contains("\" + task") || code.contains("+ task.getTitle()"),
                "no user input is concatenated into SQL");
        t.assertFalse(code.matches("(?s).*(password|secret|passwd)\\s*=\\s*\"[^\"]+\".*"),
                "no hardcoded credentials in JDBC repository source");
    }
}
