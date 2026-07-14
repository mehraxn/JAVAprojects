package restaurantorderingsystem;

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
        t.assertContains(help.out, "discount-demo", "help documents discount-demo");

        CliResult noArgs = runCli();
        t.assertEquals(0, noArgs.exitCode, "no arguments exits 0 (defaults to help)");
        t.assertContains(noArgs.out, "Usage:", "no arguments prints usage");

        CliResult demo = runCli("demo");
        t.assertEquals(0, demo.exitCode, "demo exits 0");
        t.assertContains(demo.out, "== Menu ==", "demo lists the menu");
        t.assertContains(demo.out, "Final status: SERVED", "demo runs the lifecycle to SERVED");
        t.assertEquals("", demo.err, "demo writes nothing to stderr");

        CliResult orderDemo = runCli("order-demo");
        t.assertEquals(0, orderDemo.exitCode, "order-demo exits 0");
        t.assertContains(orderDemo.out, "Juice line quantity: 5",
                "order-demo shows merged quantity");
        t.assertContains(orderDemo.out, "Distinct line items: 1",
                "order-demo shows one merged line");

        CliResult discountDemo = runCli("discount-demo");
        t.assertEquals(0, discountDemo.exitCode, "discount-demo exits 0");
        t.assertContains(discountDemo.out, "Below threshold", "discount-demo shows below case");
        t.assertContains(discountDemo.out, "Discount: 0.00",
                "discount-demo shows no discount below threshold");
        t.assertContains(discountDemo.out, "Exactly threshold",
                "discount-demo shows the exact-threshold case");
        t.assertContains(discountDemo.out, "Discount: 5.40",
                "discount-demo formats the above-threshold discount to 2 decimals");
        t.assertFalse(discountDemo.out.contains("5.4000"),
                "discount-demo does not print un-formatted money");

        CliResult statusDemo = runCli("status-demo");
        t.assertEquals(0, statusDemo.exitCode, "status-demo exits 0");
        t.assertContains(statusDemo.out, "Cannot prepare an empty order",
                "status-demo shows empty-order rejection");
        t.assertContains(statusDemo.out, "Now: SERVED", "status-demo reaches SERVED");
        t.assertContains(statusDemo.out, "Rejected as expected",
                "status-demo shows invalid transitions being rejected");

        CliResult validation = runCli("validation-demo");
        t.assertEquals(0, validation.exitCode,
                "validation-demo exits 0 (failures are intentional)");
        t.assertContains(validation.out, "must be greater than zero",
                "validation-demo shows price rejection");
        t.assertContains(validation.out, "already exists",
                "validation-demo shows duplicate rejection");
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
