package notificationservice;

/**
 * Dependency-free test runner. Compile with the application classes on the
 * classpath, then run:
 *
 *   java -cp "out:test-out" notificationservice.TestRunner   (Linux/macOS)
 *   java -cp "out;test-out" notificationservice.TestRunner   (Windows)
 *
 * Exits 0 when every test passes, 1 otherwise.
 */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        section("Notification tests", NotificationTest::run);
        section("Recipient validation tests", RecipientValidationTest::run);
        section("MockNotificationSender tests", MockNotificationSenderTest::run);
        section("NotificationService tests", NotificationServiceTest::run);
        section("CLI smoke tests", CliSmokeTest::run);

        System.out.println();
        System.out.println("Tests passed: " + TestSupport.getTestsPassed()
                + ", failed: " + TestSupport.getTestsFailed()
                + " (" + TestSupport.getChecksRun() + " checks total)");
        if (TestSupport.getTestsFailed() > 0) {
            System.out.println("RESULT: FAIL");
            System.exit(1);
        }
        System.out.println("RESULT: PASS - all tests passed.");
    }

    private static void section(String title, Runnable tests) {
        System.out.println("== " + title + " ==");
        tests.run();
        System.out.println();
    }
}
