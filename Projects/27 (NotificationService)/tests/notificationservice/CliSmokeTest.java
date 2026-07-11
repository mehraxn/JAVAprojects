package notificationservice;

import static notificationservice.TestSupport.assertEquals;
import static notificationservice.TestSupport.assertTrue;
import static notificationservice.TestSupport.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Smoke tests for the CLI. Main.run is called directly with captured output
 * streams, so no System.exit is involved and exit codes can be asserted.
 */
final class CliSmokeTest {

    private CliSmokeTest() {
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

    static void run() {
        test("help exits 0 and lists the commands", () -> {
            CliResult result = runCli("help");
            assertEquals(0, result.exitCode, "help exit code");
            assertTrue(result.out.contains("missing-sender-demo"), "help lists commands");
        });

        test("demo exits 0 and shows the full workflow", () -> {
            CliResult result = runCli("demo");
            assertEquals(0, result.exitCode, "demo exit code");
            assertTrue(result.out.contains("[MOCK EMAIL]"), "email delivered");
            assertTrue(result.out.contains("[MOCK APP]"), "app delivered");
            assertTrue(result.out.contains("Mock failure"), "sms failed once");
            assertTrue(result.out.contains("Notification history (3)"), "history summary");
            assertTrue(result.out.contains("Demo completed successfully"), "success line");
        });

        test("send with a valid EMAIL recipient exits 0", () -> {
            CliResult result = runCli("send", "EMAIL", "learner@example.com", "Welcome");
            assertEquals(0, result.exitCode, "send exit code");
            assertTrue(result.out.contains("[MOCK EMAIL] to learner@example.com: Welcome"),
                    "delivery line");
            assertTrue(result.out.contains("SENT"), "result status");
        });

        test("send accepts lowercase channel names", () -> {
            assertEquals(0, runCli("send", "app", "user-42", "Hi").exitCode,
                    "lowercase channel");
        });

        test("send with an invalid SMS recipient exits non-zero", () -> {
            CliResult result = runCli("send", "SMS", "abc123", "Hi");
            assertEquals(1, result.exitCode, "invalid recipient exit code");
            assertTrue(result.err.contains("Invalid input"), "error on stderr");
        });

        test("send with an unknown channel exits non-zero", () -> {
            CliResult result = runCli("send", "FAX", "12345", "Hi");
            assertEquals(1, result.exitCode, "unknown channel exit code");
            assertTrue(result.err.contains("Valid channels"), "channel hint");
        });

        test("retry-demo exits 0 and shows both retry outcomes", () -> {
            CliResult result = runCli("retry-demo");
            assertEquals(0, result.exitCode, "retry-demo exit code");
            assertTrue(result.out.contains("retry accepted (limit 3): true"),
                    "retry accepted");
            assertTrue(result.out.contains("false"), "retry exhaustion shown");
            assertTrue(result.out.contains("Retry demo completed successfully"),
                    "success line");
        });

        test("validation-demo exits 0 and shows accepted and rejected samples", () -> {
            CliResult result = runCli("validation-demo");
            assertEquals(0, result.exitCode, "validation-demo exit code");
            assertTrue(result.out.contains("ACCEPTED  EMAIL  learner@example.com"),
                    "accepted email");
            assertTrue(result.out.contains("REJECTED  EMAIL  bad-email"), "rejected email");
            assertTrue(result.out.contains("REJECTED  SMS  abc123"), "rejected sms");
            assertTrue(result.out.contains("ACCEPTED  APP  user-123"), "accepted app");
        });

        test("missing-sender-demo exits 0 with a clean failure record", () -> {
            CliResult result = runCli("missing-sender-demo");
            assertEquals(0, result.exitCode, "missing-sender-demo exit code");
            assertTrue(result.out.contains("No mock sender is registered"),
                    "missing sender error shown");
            assertTrue(result.out.contains("FAILED"), "FAILED status shown");
        });

        test("unknown command, no command, and missing arguments exit non-zero", () -> {
            assertEquals(1, runCli("frobnicate").exitCode, "unknown command");
            assertEquals(1, runCli().exitCode, "no command");
            assertEquals(1, runCli("send").exitCode, "send without arguments");
            assertEquals(1, runCli("send", "EMAIL", "a@b.com").exitCode,
                    "send without message");
            assertTrue(runCli("send").err.contains("Usage:"), "missing-argument usage hint");
        });
    }
}
