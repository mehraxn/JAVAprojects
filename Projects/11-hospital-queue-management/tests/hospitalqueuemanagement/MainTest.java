package hospitalqueuemanagement;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class MainTest {
    private MainTest() { }
    static void run() {
        String[] commands = {"help", "demo", "queue-demo", "emergency-demo", "status-demo", "statistics-demo", "validation-demo"};
        for (String command : commands) {
            Result result = invoke(new String[] {command});
            TestSupport.assertEquals(0, result.code, command + " succeeds");
            TestSupport.assertTrue(!result.out.isEmpty(), command + " has output");
            TestSupport.assertEquals("", result.err, command + " has no error");
        }
        Result invalid = invoke(new String[] {"bad-command"});
        TestSupport.assertNotEquals(0, invalid.code, "invalid command fails");
        TestSupport.assertContains(invalid.err, "Unknown command", "invalid command error");
        TestSupport.assertEquals(0, invoke(new String[0]).code, "no command defaults to help");
    }

    private static Result invoke(String[] args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        int code = Main.run(args, new PrintStream(out), new PrintStream(err));
        return new Result(code, new String(out.toByteArray(), StandardCharsets.UTF_8),
                new String(err.toByteArray(), StandardCharsets.UTF_8));
    }

    private static final class Result {
        private final int code;
        private final String out;
        private final String err;
        Result(int code, String out, String err) { this.code = code; this.out = out; this.err = err; }
    }
}
