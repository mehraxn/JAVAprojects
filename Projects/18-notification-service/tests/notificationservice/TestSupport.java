package notificationservice;

/**
 * Tiny dependency-free test helper: named tests, assertion counting, and a
 * process exit code CI can trust. No JUnit, Maven, or Gradle required.
 */
final class TestSupport {
    private static int testsPassed = 0;
    private static int testsFailed = 0;
    private static int checksRun = 0;

    private TestSupport() {
    }

    interface TestBody {
        void run() throws Exception;
    }

    interface ThrowingAction {
        void run() throws Exception;
    }

    static void test(String name, TestBody body) {
        try {
            body.run();
            testsPassed++;
            System.out.println("PASS " + name);
        } catch (AssertionError failure) {
            testsFailed++;
            System.out.println("FAIL " + name + " - " + failure.getMessage());
        } catch (Exception exception) {
            testsFailed++;
            System.out.println("FAIL " + name + " - unexpected " + exception);
        }
    }

    static void assertEquals(Object expected, Object actual, String message) {
        checksRun++;
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " (expected '" + expected
                    + "' but got '" + actual + "')");
        }
    }

    static void assertTrue(boolean condition, String message) {
        checksRun++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    static void assertThrows(Class<? extends Throwable> expected, ThrowingAction action,
            String message) {
        checksRun++;
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expected.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(message + " (expected " + expected.getSimpleName()
                    + " but got " + thrown.getClass().getSimpleName() + ")");
        }
        throw new AssertionError(message + " (expected " + expected.getSimpleName()
                + " but nothing was thrown)");
    }

    static int getTestsPassed() {
        return testsPassed;
    }

    static int getTestsFailed() {
        return testsFailed;
    }

    static int getChecksRun() {
        return checksRun;
    }
}
