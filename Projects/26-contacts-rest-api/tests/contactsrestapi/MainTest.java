package contactsrestapi;

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
        t.assertContains(help.out, "server <port>", "help documents the server command");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== Create ==", "demo shows create step");
        t.assertContains(demo.out, "== Search 'compiler' ==", "demo shows search step");
        t.assertContains(demo.out, "== Pagination (offset=1, limit=2) ==",
                "demo shows pagination step");
        t.assertContains(demo.out, "Deleted: true", "demo shows delete step");
        t.assertContains(demo.out, "C-1", "demo shows generated IDs");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult serviceDemo = runCli("service-demo");
        t.assertEquals(0, serviceDemo.exitCode, "service-demo exits 0");
        t.assertContains(serviceDemo.out, "== Update", "service-demo shows update step");

        CliResult httpDemo = runCli("http-demo");
        t.assertEquals(0, httpDemo.exitCode, "http-demo exits 0");
        t.assertContains(httpDemo.out, "curl -i", "http-demo prints curl examples");

        CliResult unknown = runCli("bogus");
        t.assertNotEquals(0, unknown.exitCode, "unknown command exits non-zero");
        t.assertContains(unknown.err, "Unknown command: bogus",
                "unknown command reports itself on stderr");

        CliResult missingPort = runCli("server");
        t.assertNotEquals(0, missingPort.exitCode, "server without port exits non-zero");
        t.assertContains(missingPort.err, "Missing port", "missing port reported on stderr");

        CliResult badPort = runCli("server", "bad");
        t.assertNotEquals(0, badPort.exitCode, "non-numeric port exits non-zero");
        t.assertContains(badPort.err, "Port must be a number", "bad port reported on stderr");

        CliResult outOfRange = runCli("server", "99999");
        t.assertNotEquals(0, outOfRange.exitCode, "out-of-range port exits non-zero");
        t.assertContains(outOfRange.err, "Could not start server",
                "out-of-range port reported on stderr");
    }
}
