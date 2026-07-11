package authenticationsystem;

/**
 * Dependency-free test runner. Compile with the application classes on the
 * classpath, then run:
 *
 *   java -cp "out:test-out" authenticationsystem.TestRunner   (Linux/macOS)
 *   java -cp "out;test-out" authenticationsystem.TestRunner   (Windows)
 *
 * Exits 0 when every test passes, 1 otherwise. PBKDF2 runs with 120,000
 * iterations, so the full suite takes a few seconds on purpose.
 */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        section("PasswordPolicy tests", PasswordPolicyTest::run);
        section("PasswordHasher tests", PasswordHasherTest::run);
        section("User tests", UserTest::run);
        section("Session tests", SessionTest::run);
        section("AuthService tests", AuthServiceTest::run);
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
