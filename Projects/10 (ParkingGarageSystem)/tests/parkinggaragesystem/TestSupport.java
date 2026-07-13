package parkinggaragesystem;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Tiny dependency-free assertion helper shared by all test classes.
 *
 * <p>Each assertion increments a global check counter and throws
 * {@link AssertionError} on failure so {@link TestRunner} can report totals.
 */
public final class TestSupport {
    private static int checks;

    private TestSupport() {
    }

    public static int checkCount() {
        return checks;
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        checks++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " -- expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertNotEquals(Object unexpected, Object actual, String message) {
        checks++;
        if (Objects.equals(unexpected, actual)) {
            throw new AssertionError(message + " -- value was unexpectedly <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condition, String message) {
        checks++;
        if (!condition) {
            throw new AssertionError(message + " -- expected true");
        }
    }

    public static void assertFalse(boolean condition, String message) {
        checks++;
        if (condition) {
            throw new AssertionError(message + " -- expected false");
        }
    }

    public static void assertNull(Object value, String message) {
        checks++;
        if (value != null) {
            throw new AssertionError(message + " -- expected null but was <" + value + ">");
        }
    }

    public static void assertNotNull(Object value, String message) {
        checks++;
        if (value == null) {
            throw new AssertionError(message + " -- expected non-null");
        }
    }

    public static void assertThrows(Class<? extends Throwable> expectedType,
                                    Runnable action, String message) {
        checks++;
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return;
            }
            throw new AssertionError(message + " -- expected " + expectedType.getSimpleName()
                    + " but got " + thrown.getClass().getSimpleName() + ": " + thrown.getMessage());
        }
        throw new AssertionError(message + " -- expected " + expectedType.getSimpleName()
                + " but nothing was thrown");
    }

    public static void assertContains(String text, String expectedSubstring, String message) {
        checks++;
        if (text == null || !text.contains(expectedSubstring)) {
            throw new AssertionError(message + " -- expected <" + text
                    + "> to contain <" + expectedSubstring + ">");
        }
    }

    /** Compares two BigDecimals numerically (scale-insensitive: 5.0 equals 5.00). */
    public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual, String message) {
        checks++;
        if (expected == null || actual == null || expected.compareTo(actual) != 0) {
            throw new AssertionError(message + " -- expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void assertDoubleEquals(double expected, double actual, double tolerance, String message) {
        checks++;
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError(message + " -- expected <" + expected
                    + "> but was <" + actual + "> (tolerance " + tolerance + ")");
        }
    }
}
