package taskmanagerjdbc;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public final class MainTest {
    private MainTest() {
    }

    private static final class CliResult {
        final int exitCode;
        final String out;
        final String err;

        CliResult(int exitCode, String out, String err) {
            this.exitCode = exitCode;
            this.out = out;
            this.err = err;
        }
    }

    private static CliResult runCli(String... args) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        int exitCode;
        try (PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
                PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8)) {
            exitCode = Main.run(args, out, err);
        }
        return new CliResult(exitCode,
                outBytes.toString(StandardCharsets.UTF_8),
                errBytes.toString(StandardCharsets.UTF_8));
    }

    static void run(Assert t) {
        CliResult help = runCli("help");
        t.assertEquals(0, help.exitCode, "help exits 0");
        t.assertContains(help.out, "Usage:", "help prints usage");
        t.assertContains(help.out, "jdbc-info", "help documents the jdbc-info command");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0 (defaults to help)");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== Create tasks ==", "demo creates tasks");
        t.assertContains(demo.out, "== Update task details ==", "demo updates details");
        t.assertContains(demo.out, "== Update task status ==", "demo updates status");
        t.assertContains(demo.out, "== Filter by status ==", "demo filters by status");
        t.assertContains(demo.out, "Deleted: true", "demo deletes a task");
        t.assertContains(demo.out, "== Final task list ==", "demo prints the final list");
        t.assertContains(demo.out, "no database was contacted",
                "demo states that no database is used");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult inMemory = runCli("in-memory-demo");
        t.assertEquals(0, inMemory.exitCode, "in-memory-demo exits 0");
        t.assertContains(inMemory.out, "InMemoryTaskRepository",
                "in-memory-demo names the repository it uses");

        CliResult validation = runCli("validation-demo");
        t.assertEquals(0, validation.exitCode,
                "validation-demo exits 0 (failures are intentional)");
        t.assertContains(validation.out, "Rejected as expected: Title cannot be empty.",
                "validation-demo shows blank-title rejection");
        t.assertContains(validation.out, "Rejected as expected: Task status cannot be null.",
                "validation-demo shows null-status rejection");
        t.assertContains(validation.out, "(false = not found)",
                "validation-demo shows missing-task behavior");
        t.assertFalse(validation.out.contains("this should not happen"),
                "no validation case unexpectedly succeeded");
        t.assertEquals("", validation.err, "validation-demo writes nothing to stderr");

        CliResult jdbcInfo = runCli("jdbc-info");
        t.assertEquals(0, jdbcInfo.exitCode, "jdbc-info exits 0");
        t.assertContains(jdbcInfo.out, "PreparedStatement", "jdbc-info explains SQL safety");
        t.assertContains(jdbcInfo.out, "CREATE TABLE tasks", "jdbc-info shows the schema");
        t.assertContains(jdbcInfo.out, "does not connect to any database",
                "jdbc-info states it never connects");

        CliResult unknown = runCli("bogus");
        t.assertNotEquals(0, unknown.exitCode, "unknown command exits non-zero");
        t.assertContains(unknown.err, "Unknown command: bogus",
                "unknown command reports itself on stderr");
        t.assertFalse(unknown.err.contains("Exception"),
                "unknown command does not print a stack trace");
    }
}
