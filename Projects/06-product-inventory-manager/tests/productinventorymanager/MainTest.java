package productinventorymanager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static productinventorymanager.TestSupport.assertContains;
import static productinventorymanager.TestSupport.assertEquals;
import static productinventorymanager.TestSupport.assertTrue;

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
            assertContains(result.out, "Total inventory value", "shows total");
        });

        runner.test("Main: stock-demo returns 0 and shows rejection", () -> {
            Captured result = run("stock-demo");
            assertEquals(0, result.exitCode, "stock-demo exit 0");
            assertContains(result.out, "Rejected as expected", "shows underflow rejection");
        });

        runner.test("Main: search-demo returns 0", () -> {
            Captured result = run("search-demo");
            assertEquals(0, result.exitCode, "search-demo exit 0");
            assertContains(result.out, "Search by SKU", "shows search");
        });

        runner.test("Main: sort-demo returns 0", () -> {
            Captured result = run("sort-demo");
            assertEquals(0, result.exitCode, "sort-demo exit 0");
            assertContains(result.out, "descending", "shows descending sort");
        });

        runner.test("Main: report-demo returns 0", () -> {
            Captured result = run("report-demo");
            assertEquals(0, result.exitCode, "report-demo exit 0");
            assertContains(result.out, "Out-of-stock", "shows out-of-stock report");
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
