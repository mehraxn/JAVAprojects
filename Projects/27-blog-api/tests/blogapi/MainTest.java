package blogapi;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
final class MainTest {
    private MainTest() { }
    static void run() {
        command("help", "Usage:"); command("demo", "Comment cleanup confirmed"); command("service-demo", "Search results:");
        invalid(new String[] {"unknown"}, "Unknown or invalid"); invalid(new String[] {"server"}, "requires one port");
        invalid(new String[] {"server", "invalid"}, "Port must be"); invalid(new String[] {"server", "70000"}, "Port must be");
        invalid(new String[0], "Missing command");
    }
    private static void command(String name, String expected) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream();
        Assertions.assertEquals(0, Main.run(new String[] {name}, stream(out), stream(err)), name + " exit");
        Assertions.assertContains(text(out), expected, name + " output"); Assertions.assertEquals("", text(err), name + " stderr");
    }
    private static void invalid(String[] args, String expected) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(), err = new ByteArrayOutputStream();
        Assertions.assertNotEquals(0, Main.run(args, stream(out), stream(err)), "invalid exit"); Assertions.assertContains(text(err), expected, "invalid error");
    }
    private static PrintStream stream(ByteArrayOutputStream value) { return new PrintStream(value, true, StandardCharsets.UTF_8); }
    private static String text(ByteArrayOutputStream value) { return value.toString(StandardCharsets.UTF_8); }
}
