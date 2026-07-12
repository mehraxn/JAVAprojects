package hospitalqueuemanagement;

public final class TestRunner {
    private TestRunner() { }
    public static void main(String[] args) {
        int failed = 0;
        failed += run("PatientTest", PatientTest::run);
        failed += run("TriageLevelTest", TriageLevelTest::run);
        failed += run("TriageQueueTest", TriageQueueTest::run);
        failed += run("QueueStatisticsTest", QueueStatisticsTest::run);
        failed += run("MainTest", MainTest::run);
        System.out.println("Checks: " + TestSupport.checks() + ", failures: " + failed);
        if (failed != 0) { System.exit(1); }
    }

    private static int run(String name, Runnable test) {
        try { test.run(); System.out.println("PASS " + name); return 0; }
        catch (AssertionError | RuntimeException failure) {
            System.err.println("FAIL " + name + ": " + failure.getMessage());
            return 1;
        }
    }
}
