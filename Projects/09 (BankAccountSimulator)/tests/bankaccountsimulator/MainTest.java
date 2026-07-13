package bankaccountsimulator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static bankaccountsimulator.TestSupport.assertContains;
import static bankaccountsimulator.TestSupport.assertEquals;
import static bankaccountsimulator.TestSupport.assertTrue;

/** In-process smoke tests for {@link Main#run}. No separate JVM is launched. */
final class MainTest {

    private MainTest() {
    }

    private static final class Captured {
        final int exitCode;
        final String out;
        final String err;

        Captured(int exitCode, String out, String err) {
            this.exitCode = exitCode;
            this.out = out;
            this.err = err;
        }
    }

    private static Captured run(String... args) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8);
        int exitCode = Main.run(args, out, err);
        return new Captured(exitCode,
                outBytes.toString(StandardCharsets.UTF_8),
                errBytes.toString(StandardCharsets.UTF_8));
    }

    static void register(TestRunner runner) {
        runner.test("Main: help returns 0 and lists commands", () -> {
            Captured result = run("help");
            assertEquals(0, result.exitCode, "help exit 0");
            assertContains(result.out, "Commands:", "lists commands");
        });

        runner.test("Main: no command defaults to help (exit 0)", () -> {
            Captured result = run();
            assertEquals(0, result.exitCode, "default exit 0");
            assertContains(result.out, "Usage:", "shows usage");
        });

        runner.test("Main: demo returns 0", () -> {
            Captured result = run("demo");
            assertEquals(0, result.exitCode, "demo exit 0");
            assertContains(result.out, "Total bank balance", "shows total");
        });

        runner.test("Main: deposit-demo returns 0", () -> {
            Captured result = run("deposit-demo");
            assertEquals(0, result.exitCode, "deposit-demo exit 0");
            assertContains(result.out, "Balance after", "shows balance after");
        });

        runner.test("Main: withdraw-demo returns 0", () -> {
            Captured result = run("withdraw-demo");
            assertEquals(0, result.exitCode, "withdraw-demo exit 0");
            assertContains(result.out, "overdraft", "shows overdraft rejection");
        });

        runner.test("Main: transfer-demo returns 0", () -> {
            Captured result = run("transfer-demo");
            assertEquals(0, result.exitCode, "transfer-demo exit 0");
            assertContains(result.out, "All-or-nothing", "shows all-or-nothing");
        });

        runner.test("Main: statement-demo returns 0", () -> {
            Captured result = run("statement-demo");
            assertEquals(0, result.exitCode, "statement-demo exit 0");
            assertContains(result.out, "Statement for", "shows statement");
        });

        runner.test("Main: validation-demo returns 0", () -> {
            Captured result = run("validation-demo");
            assertEquals(0, result.exitCode, "validation-demo exit 0");
            assertContains(result.out, "rejected", "shows rejections");
        });

        runner.test("Main: unknown command returns non-zero and writes to stderr", () -> {
            Captured result = run("bogus");
            assertTrue(result.exitCode != 0, "non-zero exit");
            assertContains(result.err, "Unknown command", "error on stderr");
            assertEquals("", result.out, "nothing on stdout");
        });
    }
}
