package expensetracker;

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
        t.assertContains(help.out, "report-demo", "help documents the report-demo command");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0 (defaults to help)");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== All expenses ==", "demo lists expenses");
        t.assertContains(demo.out, "== Filter by category 'food' (case-insensitive) ==",
                "demo filters by category");
        t.assertContains(demo.out, "== Filter by month 2026-07 ==", "demo filters by month");
        t.assertContains(demo.out, "Total spending: 724.15", "demo totals are exact");
        t.assertContains(demo.out, "== Highest expense ==", "demo shows highest expense");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult csvDemo = runCli("csv-demo");
        t.assertEquals(0, csvDemo.exitCode, "csv-demo exits 0");
        t.assertContains(csvDemo.out, "Round trip preserved all 4 expenses",
                "csv-demo verifies the round trip");
        t.assertContains(csvDemo.out, "Deleted temporary file.",
                "csv-demo cleans up its temp file");

        CliResult reportDemo = runCli("report-demo");
        t.assertEquals(0, reportDemo.exitCode, "report-demo exits 0");
        t.assertContains(reportDemo.out, "== Totals by category ==",
                "report-demo shows category totals");
        t.assertContains(reportDemo.out, "== Totals by month ==",
                "report-demo shows monthly totals");
        t.assertContains(reportDemo.out, "2026-06: 12.90", "report-demo June total is exact");
        t.assertContains(reportDemo.out, "2026-07: 711.25", "report-demo July total is exact");
        t.assertContains(reportDemo.out, "== Highest expense ==",
                "report-demo shows the highest expense");
        t.assertContains(reportDemo.out, "Rent, July", "report-demo highest expense is the rent");

        CliResult validation = runCli("validation-demo");
        t.assertEquals(0, validation.exitCode,
                "validation-demo exits 0 (failures are intentional)");
        t.assertContains(validation.out, "Rejected as expected: Title cannot be empty.",
                "validation-demo shows blank-title rejection");
        t.assertContains(validation.out, "Rejected as expected: Amount must be greater than zero.",
                "validation-demo shows zero-amount rejection");
        t.assertContains(validation.out, "already exists",
                "validation-demo shows duplicate-ID rejection");
        t.assertContains(validation.out, "(false = not found)",
                "validation-demo shows missing-removal behavior");
        t.assertFalse(validation.out.contains("this should not happen"),
                "no validation case unexpectedly succeeded");
        t.assertEquals("", validation.err, "validation-demo writes nothing to stderr");

        CliResult unknown = runCli("bogus");
        t.assertNotEquals(0, unknown.exitCode, "unknown command exits non-zero");
        t.assertContains(unknown.err, "Unknown command: bogus",
                "unknown command reports itself on stderr");
        t.assertFalse(unknown.err.contains("Exception"),
                "unknown command does not print a stack trace");
    }
}
