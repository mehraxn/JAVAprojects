package studentgrademanager;

final class TestSupport {
    private static int checkCount;

    private TestSupport() {
    }

    static int getCheckCount() {
        return checkCount;
    }

    static void assertEquals(Object expected, Object actual, String message) {
        checkCount++;
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " expected <" + expected + "> but was <" + actual + ">");
        }
    }

    static void assertNotEquals(Object unexpected, Object actual, String message) {
        checkCount++;
        if (unexpected == null ? actual == null : unexpected.equals(actual)) {
            throw new AssertionError(message + " did not expect <" + unexpected + ">");
        }
    }

    static void assertTrue(boolean condition, String message) {
        checkCount++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        checkCount++;
        if (condition) {
            throw new AssertionError(message);
        }
    }

    static void assertNull(Object value, String message) {
        checkCount++;
        if (value != null) {
            throw new AssertionError(message + " expected null but was <" + value + ">");
        }
    }

    static void assertNotNull(Object value, String message) {
        checkCount++;
        if (value == null) {
            throw new AssertionError(message + " expected a non-null value");
        }
    }

    static void assertThrows(Class<? extends Throwable> expectedExceptionClass, Runnable action, String message) {
        checkCount++;
        try {
            action.run();
        } catch (Throwable ex) {
            if (expectedExceptionClass.isInstance(ex)) {
                return;
            }
            throw new AssertionError(message + " expected " + expectedExceptionClass.getSimpleName()
                    + " but was " + ex.getClass().getSimpleName(), ex);
        }
        throw new AssertionError(message + " expected " + expectedExceptionClass.getSimpleName());
    }

    static void assertContains(String text, String expectedSubstring, String message) {
        checkCount++;
        if (text == null || !text.contains(expectedSubstring)) {
            throw new AssertionError(message + " expected text to contain <" + expectedSubstring + "> but was <"
                    + text + ">");
        }
    }

    static void assertDoubleEquals(double expected, double actual, double tolerance, String message) {
        checkCount++;
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + " expected <" + expected + "> but was <" + actual + ">");
        }
    }
}
