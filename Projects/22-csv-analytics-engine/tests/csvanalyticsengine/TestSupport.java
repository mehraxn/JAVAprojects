package csvanalyticsengine;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tiny dependency-free test helper: named tests, assertion counting, temp-file
 * handling, and a process exit code CI can trust. No JUnit, Maven, or Gradle.
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

    interface TempFileTest {
        void run(Path file) throws Exception;
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

    /** Runs a test body with a temp CSV file that is always deleted afterwards. */
    static void withTempFile(TempFileTest body) throws Exception {
        Path file = Files.createTempFile("csv-analytics-test", ".csv");
        try {
            body.run(file);
        } finally {
            Files.deleteIfExists(file);
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
