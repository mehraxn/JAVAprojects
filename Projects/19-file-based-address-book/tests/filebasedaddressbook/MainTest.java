package filebasedaddressbook;

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
        t.assertContains(help.out, "import-demo", "help documents the import-demo command");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0 (defaults to help)");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== All contacts (sorted by name) ==", "demo lists contacts");
        t.assertContains(demo.out, "== Search 'luca'", "demo searches contacts");
        t.assertContains(demo.out, "== Update C002 ==", "demo updates a contact");
        t.assertContains(demo.out, "Deleted: true", "demo deletes a contact");
        t.assertContains(demo.out, "== Final contacts ==", "demo prints the final list");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult fileDemo = runCli("file-demo");
        t.assertEquals(0, fileDemo.exitCode, "file-demo exits 0");
        t.assertContains(fileDemo.out, "Round trip preserved all 3 contacts",
                "file-demo verifies the round trip");
        t.assertContains(fileDemo.out, "Deleted temporary file.",
                "file-demo cleans up its temp file");

        CliResult importDemo = runCli("import-demo");
        t.assertEquals(0, importDemo.exitCode, "import-demo exits 0");
        t.assertContains(importDemo.out, "After import: 3 contacts",
                "import-demo imports successfully");
        t.assertContains(importDemo.out, "Rejected as expected",
                "import-demo shows a rejected conflicting import");
        t.assertContains(importDemo.out, "the failed import changed nothing",
                "import-demo confirms atomic import behavior");

        CliResult validation = runCli("validation-demo");
        t.assertEquals(0, validation.exitCode,
                "validation-demo exits 0 (failures are intentional)");
        t.assertContains(validation.out, "Rejected as expected: Contact name must not be blank",
                "validation-demo shows blank-name rejection");
        t.assertContains(validation.out, "Email domain must contain a dot",
                "validation-demo shows invalid-email rejection");
        t.assertContains(validation.out, "Stored contact unchanged",
                "validation-demo shows a failed update leaves state intact");
        t.assertContains(validation.out, "(false = not found)",
                "validation-demo shows missing-update behavior");
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
