package jobapplicationtracker;

/**
 * Dependency-free test runner. Compile with the application classes on the
 * classpath, then run:
 *
 *   java -cp "out:test-out" jobapplicationtracker.TestRunner   (Linux/macOS)
 *   java -cp "out;test-out" jobapplicationtracker.TestRunner   (Windows)
 *
 * Exits 0 when every test passes, 1 otherwise.
 */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        System.out.println("== JobApplication tests ==");
        JobApplicationTest.run();
        System.out.println();
        System.out.println("== TrackerService tests ==");
        TrackerServiceTest.run();
        System.out.println();
        System.out.println("== CsvApplicationRepository tests ==");
        CsvApplicationRepositoryTest.run();

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
}
