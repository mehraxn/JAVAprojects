package authenticationsystem;

import static authenticationsystem.TestSupport.assertEquals;
import static authenticationsystem.TestSupport.assertFalse;
import static authenticationsystem.TestSupport.assertTrue;
import static authenticationsystem.TestSupport.test;

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
            assertTrue(result.out.contains("expiry-demo"), "help lists commands");
        });

        test("demo exits 0 and never prints a raw password or full token", () -> {
            CliResult result = runCli("demo");
            assertEquals(0, result.exitCode, "demo exit code");
            assertTrue(result.out.contains("Demo completed successfully"), "success line");
            assertTrue(result.out.contains("USER cannot access ADMIN"), "role denial shown");
            assertFalse(result.out.contains("LearnJava10!"), "no raw password in output");
            assertFalse(result.out.contains("AdminStudy2!"), "no admin password in output");
            // Full tokens are 43 characters of URL-safe Base64; masked ones end in "...".
            assertTrue(result.out.contains("..."), "masked tokens used");
        });

        test("register-demo exits 0 and shows all three cases", () -> {
            CliResult result = runCli("register-demo");
            assertEquals(0, result.exitCode, "register-demo exit code");
            assertTrue(result.out.contains("valid registration"), "valid case");
            assertTrue(result.out.contains("duplicate username"), "duplicate case");
            assertTrue(result.out.contains("weak password rejected"), "weak case");
        });

        test("login-demo exits 0 and shows success and both failures", () -> {
            CliResult result = runCli("login-demo");
            assertEquals(0, result.exitCode, "login-demo exit code");
            assertTrue(result.out.contains("correct password: Session"), "success case");
            assertTrue(result.out.contains("wrong password:   null"), "wrong-password case");
            assertTrue(result.out.contains("unknown username: null"), "unknown-user case");
        });

        test("authorization-demo exits 0 and shows the role matrix", () -> {
            CliResult result = runCli("authorization-demo");
            assertEquals(0, result.exitCode, "authorization-demo exit code");
            assertTrue(result.out.contains("USER token -> ADMIN action: false"),
                    "USER denied ADMIN");
            assertTrue(result.out.contains("ADMIN token -> ADMIN action: true"),
                    "ADMIN allowed ADMIN");
            assertTrue(result.out.contains("made-up token -> USER action: false"),
                    "invalid token denied");
        });

        test("expiry-demo exits 0 and shows expiry without waiting", () -> {
            CliResult result = runCli("expiry-demo");
            assertEquals(0, result.exitCode, "expiry-demo exit code");
            assertTrue(result.out.contains("at 10:29 token authenticates: true"),
                    "valid before expiry");
            assertTrue(result.out.contains("at 10:31 token authenticates: false"),
                    "invalid after expiry");
        });

        test("unknown command and no command exit non-zero", () -> {
            CliResult unknown = runCli("frobnicate");
            assertEquals(1, unknown.exitCode, "unknown command");
            assertTrue(unknown.err.contains("Unknown command"), "error on stderr");
            assertEquals(1, runCli().exitCode, "no command");
        });
    }
}
