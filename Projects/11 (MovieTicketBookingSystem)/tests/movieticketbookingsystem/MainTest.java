package movieticketbookingsystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static movieticketbookingsystem.TestSupport.assertContains;
import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertTrue;

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
            assertContains(result.out, "Total price", "shows total price");
        });

        runner.test("Main: booking-demo returns 0", () -> {
            Captured result = run("booking-demo");
            assertEquals(0, result.exitCode, "booking-demo exit 0");
            assertContains(result.out, "All-or-nothing", "shows all-or-nothing");
        });

        runner.test("Main: cancellation-demo returns 0", () -> {
            Captured result = run("cancellation-demo");
            assertEquals(0, result.exitCode, "cancellation-demo exit 0");
            assertContains(result.out, "Released exactly", "shows released seats");
        });

        runner.test("Main: full-showtime-demo returns 0", () -> {
            Captured result = run("full-showtime-demo");
            assertEquals(0, result.exitCode, "full-showtime-demo exit 0");
            assertContains(result.out, "full=true", "shows full showtime");
        });

        runner.test("Main: availability-demo returns 0", () -> {
            Captured result = run("availability-demo");
            assertEquals(0, result.exitCode, "availability-demo exit 0");
            assertContains(result.out, "Available before booking", "shows availability");
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
