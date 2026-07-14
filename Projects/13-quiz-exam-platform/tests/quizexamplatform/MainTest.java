package quizexamplatform;

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
        t.assertContains(help.out, "scoreboard-demo", "help documents scoreboard-demo");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0 (defaults to help)");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== Answer feedback ==", "demo shows answer feedback");
        t.assertContains(demo.out, "== Result ==", "demo shows a result");
        t.assertContains(demo.out, "== Scoreboard ==", "demo shows the scoreboard");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult attemptDemo = runCli("attempt-demo");
        t.assertEquals(0, attemptDemo.exitCode, "attempt-demo exits 0");
        t.assertContains(attemptDemo.out, "Replaced answer to Q1",
                "attempt-demo shows answer replacement");
        t.assertContains(attemptDemo.out, "Attempt still has 3 questions",
                "attempt-demo shows snapshot independence");
        t.assertContains(attemptDemo.out, "Editing after finish is rejected",
                "attempt-demo shows post-finish locking");

        CliResult validation = runCli("validation-demo");
        t.assertEquals(0, validation.exitCode,
                "validation-demo exits 0 (failures are intentional)");
        t.assertContains(validation.out, "Rejected as expected",
                "validation-demo shows rejections");
        t.assertContains(validation.out, "Question options must be unique",
                "validation-demo shows duplicate-option rejection");
        t.assertFalse(validation.out.contains("this should not happen"),
                "no validation case unexpectedly succeeded");
        t.assertEquals("", validation.err, "validation-demo writes nothing to stderr");

        CliResult scoreboard = runCli("scoreboard-demo");
        t.assertEquals(0, scoreboard.exitCode, "scoreboard-demo exits 0");
        t.assertContains(scoreboard.out, "Ada's best score: 100%",
                "scoreboard-demo shows best-score-per-participant");
        t.assertContains(scoreboard.out, "Cleo's best score (kept): 75%",
                "scoreboard-demo shows a lower repeated score being ignored");
        t.assertContains(scoreboard.out, "1. Ada: 100%", "scoreboard-demo ranks correctly");

        CliResult unknown = runCli("bogus");
        t.assertNotEquals(0, unknown.exitCode, "unknown command exits non-zero");
        t.assertContains(unknown.err, "Unknown command: bogus",
                "unknown command reports itself on stderr");
        t.assertFalse(unknown.err.contains("Exception"),
                "unknown command does not print a stack trace");
    }
}
