package studentgrademanager;

public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        int failures = 0;
        failures += runTest("StudentTest", StudentTest::run);
        failures += runTest("GradeBookTest", GradeBookTest::run);
        failures += runTest("ReportTest", ReportTest::run);
        failures += runTest("SnapshotTest", SnapshotTest::run);
        failures += runTest("MainTest", MainTest::run);

        int checks = TestSupport.getCheckCount();
        if (failures == 0) {
            System.out.println("PASS: 5 test classes, " + checks + " assertion checks");
            System.exit(0);
        }

        System.err.println("FAIL: " + failures + " test class(es) failed, " + checks + " assertion checks");
        System.exit(1);
    }

    private static int runTest(String name, Runnable test) {
        int beforeChecks = TestSupport.getCheckCount();
        try {
            test.run();
            int checks = TestSupport.getCheckCount() - beforeChecks;
            System.out.println("PASS " + name + " (" + checks + " checks)");
            return 0;
        } catch (AssertionError ex) {
            int checks = TestSupport.getCheckCount() - beforeChecks;
            System.err.println("FAIL " + name + " after " + checks + " checks: " + ex.getMessage());
            ex.printStackTrace(System.err);
            return 1;
        }
    }
}
