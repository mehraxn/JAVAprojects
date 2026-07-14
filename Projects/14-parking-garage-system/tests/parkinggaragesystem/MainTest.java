package parkinggaragesystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static parkinggaragesystem.TestSupport.assertContains;
import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertTrue;

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
            assertContains(result.out, "Receipt", "shows receipt");
        });

        runner.test("Main: parking-demo returns 0", () -> {
            Captured result = run("parking-demo");
            assertEquals(0, result.exitCode, "parking-demo exit 0");
            assertContains(result.out, "duplicate vehicle entry", "shows duplicate rejection");
        });

        runner.test("Main: exit-demo returns 0", () -> {
            Captured result = run("exit-demo");
            assertEquals(0, result.exitCode, "exit-demo exit 0");
            assertContains(result.out, "second exit", "shows double-exit rejection");
        });

        runner.test("Main: fee-demo returns 0", () -> {
            Captured result = run("fee-demo");
            assertEquals(0, result.exitCode, "fee-demo exit 0");
            assertContains(result.out, "started-hour", "shows started-hour billing");
        });

        runner.test("Main: full-garage-demo returns 0", () -> {
            Captured result = run("full-garage-demo");
            assertEquals(0, result.exitCode, "full-garage-demo exit 0");
            assertContains(result.out, "unchanged", "shows state unchanged");
        });

        runner.test("Main: report-demo returns 0", () -> {
            Captured result = run("report-demo");
            assertEquals(0, result.exitCode, "report-demo exit 0");
            assertContains(result.out, "Total revenue", "shows revenue");
        });

        runner.test("Main: validation-demo returns 0", () -> {
            Captured result = run("validation-demo");
            assertEquals(0, result.exitCode, "validation-demo exit 0");
            assertContains(result.out, "rejected", "shows validation rejections");
        });

        runner.test("Main: unknown command returns non-zero and writes to stderr", () -> {
            Captured result = run("bogus");
            assertTrue(result.exitCode != 0, "non-zero exit");
            assertContains(result.err, "Unknown command", "error on stderr");
            assertEquals("", result.out, "nothing on stdout");
        });
    }
}
