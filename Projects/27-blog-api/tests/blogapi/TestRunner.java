package blogapi;
public final class TestRunner {
    private TestRunner() { }
    public static void main(String[] args) {
        String[] names = {"DomainModelTest", "BlogServiceTest", "BlogJsonTest", "BlogHttpServerTest", "MainTest"};
        Runnable[] suites = {DomainModelTest::run, BlogServiceTest::run, BlogJsonTest::run,
            BlogHttpServerTest::run, MainTest::run};
        int failures = 0;
        for (int i = 0; i < suites.length; i++) {
            int before = Assertions.count();
            try { suites[i].run(); System.out.println("PASS " + names[i] + " (" + (Assertions.count() - before) + " checks)"); }
            catch (RuntimeException | AssertionError error) { failures++; System.err.println("FAIL " + names[i] + ": " + error.getMessage()); }
        }
        System.out.println("Checks: " + Assertions.count() + ", suites passed: " + (suites.length - failures) + ", suites failed: " + failures);
        if (failures != 0) System.exit(1);
    }
}
