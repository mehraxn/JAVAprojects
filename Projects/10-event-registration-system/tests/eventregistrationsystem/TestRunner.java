package eventregistrationsystem;

public final class TestRunner {
    private TestRunner() { }
    public static void main(String[] args) {
        int failures = 0;
        failures += run("AttendeeTest", AttendeeTest::run); failures += run("RegistrationTest", RegistrationTest::run);
        failures += run("EventTest", EventTest::run); failures += run("EventRegistrationSystemTest", EventRegistrationSystemTest::run);
        failures += run("MainTest", MainTest::run);
        System.out.println("Checks: " + Assertions.getChecks() + ", failures: " + failures);
        if (failures != 0) System.exit(1);
    }
    private static int run(String name, Runnable test) {
        try { test.run(); System.out.println("PASS " + name); return 0; }
        catch (AssertionError | RuntimeException failure) { System.err.println("FAIL " + name + ": " + failure.getMessage()); return 1; }
    }
}
