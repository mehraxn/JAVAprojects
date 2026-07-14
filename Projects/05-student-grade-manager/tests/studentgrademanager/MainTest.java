package studentgrademanager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

final class MainTest {
    private MainTest() {
    }

    static void run() {
        CommandResult help = runCommand("help");
        TestSupport.assertEquals(0, help.exitCode, "help should return 0");
        TestSupport.assertContains(help.out, "Student Grade Manager commands", "help should print command list");

        TestSupport.assertEquals(0, runCommand("demo").exitCode, "demo should return 0");
        TestSupport.assertEquals(0, runCommand("grade-demo").exitCode, "grade-demo should return 0");
        TestSupport.assertEquals(0, runCommand("report-demo").exitCode, "report-demo should return 0");
        TestSupport.assertEquals(0, runCommand("ranking-demo").exitCode, "ranking-demo should return 0");
        TestSupport.assertEquals(0, runCommand("search-demo").exitCode, "search-demo should return 0");
        TestSupport.assertEquals(0, runCommand("validation-demo").exitCode, "validation-demo should return 0");

        CommandResult invalid = runCommand("invalid-command");
        TestSupport.assertNotEquals(0, invalid.exitCode, "Invalid command should return non-zero");
        TestSupport.assertContains(invalid.err, "Unknown command", "Invalid command should write to stderr");
        TestSupport.assertEquals("", invalid.out, "Invalid command should not write normal output");

        CommandResult noCommand = runNoCommand();
        TestSupport.assertEquals(0, noCommand.exitCode, "No command should default to help");
        TestSupport.assertContains(noCommand.out, "Student Grade Manager commands",
                "No command should print help output");
    }

    private static CommandResult runCommand(String command) {
        return runWithArgs(new String[] { command });
    }

    private static CommandResult runNoCommand() {
        return runWithArgs(new String[0]);
    }

    private static CommandResult runWithArgs(String[] args) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes, true, StandardCharsets.UTF_8);
        PrintStream err = new PrintStream(errBytes, true, StandardCharsets.UTF_8);
        int exitCode = Main.run(args, out, err);
        out.flush();
        err.flush();
        return new CommandResult(
                exitCode,
                new String(outBytes.toByteArray(), StandardCharsets.UTF_8),
                new String(errBytes.toByteArray(), StandardCharsets.UTF_8));
    }

    private static final class CommandResult {
        private final int exitCode;
        private final String out;
        private final String err;

        private CommandResult(int exitCode, String out, String err) {
            this.exitCode = exitCode;
            this.out = out;
            this.err = err;
        }
    }
}
