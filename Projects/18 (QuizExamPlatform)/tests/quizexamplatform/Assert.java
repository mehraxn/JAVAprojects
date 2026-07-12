package quizexamplatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Minimal dependency-free assertion helper. Failures are collected, not thrown. */
public final class Assert {
    private int passed;
    private final List<String> failures = new ArrayList<String>();

    /** Like Runnable, but allowed to throw checked exceptions. */
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    public void assertEquals(Object expected, Object actual, String message) {
        check(Objects.equals(expected, actual),
                message + " -- expected <" + expected + "> but was <" + actual + ">");
    }

    public void assertNotEquals(Object unexpected, Object actual, String message) {
        check(!Objects.equals(unexpected, actual),
                message + " -- did not expect <" + unexpected + ">");
    }

    public void assertTrue(boolean condition, String message) {
        check(condition, message);
    }

    public void assertFalse(boolean condition, String message) {
        check(!condition, message);
    }

    public void assertNull(Object value, String message) {
        check(value == null, message + " -- expected null but was <" + value + ">");
    }

    public void assertNotNull(Object value, String message) {
        check(value != null, message + " -- expected a non-null value");
    }

    public void assertThrows(Class<? extends Throwable> expected,
            ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (Throwable thrown) {
            check(expected.isInstance(thrown), message + " -- expected "
                    + expected.getSimpleName() + " but got "
                    + thrown.getClass().getSimpleName() + ": " + thrown.getMessage());
            return;
        }
        check(false, message + " -- expected " + expected.getSimpleName()
                + " but nothing was thrown");
    }

    public void assertContains(String text, String expectedSubstring, String message) {
        check(text != null && expectedSubstring != null && text.contains(expectedSubstring),
                message + " -- <" + text + "> does not contain <" + expectedSubstring + ">");
    }

    public void recordCrash(String testName, Throwable crash) {
        failures.add(testName + " crashed with " + crash.getClass().getSimpleName()
                + ": " + crash.getMessage());
        System.out.println("  FAIL: " + failures.get(failures.size() - 1));
    }

    private void check(boolean ok, String failureMessage) {
        if (ok) {
            passed++;
        } else {
            failures.add(failureMessage);
            System.out.println("  FAIL: " + failureMessage);
        }
    }

    public int passedCount() {
        return passed;
    }

    public int failedCount() {
        return failures.size();
    }

    public List<String> failures() {
        return new ArrayList<String>(failures);
    }
}
