package miniecommercebackend;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
final class CliSmokeTest {
    private CliSmokeTest() { }
    static void run() {
        command("help", "Usage:"); command("demo", "Order summary:");
        command("catalog-demo", "Catalog demo"); command("checkout-demo", "Checkout successful:");
        command("cancel-demo", "Stock restored:"); command("failure-demo", "Expected failure handled:");
        ByteArrayOutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream();
        Assertions.assertNotEquals(0, Main.run(new String[] {"unknown"}, stream(out), stream(err)), "unknown nonzero");
        Assertions.assertTrue(text(err).contains("Unknown command"), "unknown stderr");
        Assertions.assertEquals("", text(out), "unknown stdout empty");
        Assertions.assertNotEquals(0, Main.run(new String[0], stream(out), stream(err)), "missing nonzero");
        Assertions.assertTrue(text(err).contains("Expected exactly one command"), "missing stderr");
    }
    private static void command(String name, String expected) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream();
        Assertions.assertEquals(0, Main.run(new String[] {name}, stream(out), stream(err)), name + " exit");
        Assertions.assertTrue(text(out).contains(expected), name + " output");
        Assertions.assertEquals("", text(err), name + " stderr");
    }
    private static PrintStream stream(ByteArrayOutputStream value) { return new PrintStream(value, true, StandardCharsets.UTF_8); }
    private static String text(ByteArrayOutputStream value) { return value.toString(StandardCharsets.UTF_8); }
}
