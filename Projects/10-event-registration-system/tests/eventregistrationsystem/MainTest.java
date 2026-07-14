package eventregistrationsystem;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class MainTest {
    private MainTest() { }
    static void run() {
        String[] commands = {"help","demo","registration-demo","capacity-demo","cancellation-demo","search-demo","validation-demo"};
        for (String command : commands) Assertions.assertEquals(0, execute(new String[]{command}).code, command + " succeeds");
        Result empty = execute(new String[0]); Assertions.assertEquals(0, empty.code, "empty defaults help"); Assertions.assertContains(empty.out, "commands", "help output");
        Result invalid = execute(new String[]{"bad"}); Assertions.assertNotEquals(0, invalid.code, "invalid nonzero"); Assertions.assertContains(invalid.err, "Unknown command", "invalid stderr");
    }
    private static Result execute(String[] args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); ByteArrayOutputStream err = new ByteArrayOutputStream();
        int code = Main.run(args, new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
        return new Result(code, out.toString(StandardCharsets.UTF_8), err.toString(StandardCharsets.UTF_8));
    }
    private static final class Result { final int code; final String out; final String err; Result(int code,String out,String err){this.code=code;this.out=out;this.err=err;} }
}
