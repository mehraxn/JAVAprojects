package csvanalyticsengine;

/**
 * Dependency-free test runner. Compile with the application classes on the
 * classpath, then run:
 *
 *   java -cp "out:test-out" csvanalyticsengine.TestRunner   (Linux/macOS)
 *   java -cp "out;test-out" csvanalyticsengine.TestRunner   (Windows)
 *
 * Exits 0 when every test passes, 1 otherwise.
 */
public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        section("CsvReader tests", CsvReaderTest::run);
        section("CsvWriter tests", CsvWriterTest::run);
        section("DataSet tests", DataSetTest::run);
        section("DataRow tests", DataRowTest::run);
        section("NumericStatistics tests", NumericStatisticsTest::run);
        section("AnalyticsService tests", AnalyticsServiceTest::run);
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
