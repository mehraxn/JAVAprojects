package urlshortenerbackend;

public final class TestRunner {
    private TestRunner() {
    }

    public static void main(String[] args) {
        Assert t = new Assert();

        runSuite(t, "CodeGeneratorTest", () -> CodeGeneratorTest.run(t));
        runSuite(t, "UrlEntryTest", () -> UrlEntryTest.run(t));
        runSuite(t, "ShortenerServiceTest", () -> ShortenerServiceTest.run(t));
        runSuite(t, "FileUrlStoreTest", () -> FileUrlStoreTest.run(t));
        runSuite(t, "UrlShortenerHttpServerTest", () -> UrlShortenerHttpServerTest.run(t));
        runSuite(t, "MainTest", () -> MainTest.run(t));

        System.out.println();
        System.out.println("Checks passed: " + t.passedCount());
        System.out.println("Checks failed: " + t.failedCount());
        if (t.failedCount() == 0) {
            System.out.println("RESULT: ALL TESTS PASSED");
            System.exit(0);
        } else {
            System.out.println("RESULT: TESTS FAILED");
            for (String failure : t.failures()) {
                System.out.println("  - " + failure);
            }
            System.exit(1);
        }
    }

    private static void runSuite(Assert t, String name, Assert.ThrowingRunnable suite) {
        System.out.println("== " + name + " ==");
        int failuresBefore = t.failedCount();
        int passedBefore = t.passedCount();
        try {
            suite.run();
        } catch (Throwable crash) {
            t.recordCrash(name, crash);
        }
        int passed = t.passedCount() - passedBefore;
        int failed = t.failedCount() - failuresBefore;
        System.out.println("   " + passed + " passed, " + failed + " failed");
    }
}
