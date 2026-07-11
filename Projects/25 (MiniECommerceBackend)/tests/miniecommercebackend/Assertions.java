package miniecommercebackend;

import java.math.BigDecimal;

final class Assertions {
    private static int checks;
    private Assertions() { }
    static int count() { return checks; }
    static void assertEquals(Object expected, Object actual, String message) {
        checks++;
        if (expected == null ? actual != null : !expected.equals(actual)) fail(message);
    }
    static void assertNotEquals(Object unexpected, Object actual, String message) {
        checks++;
        if (unexpected == null ? actual == null : unexpected.equals(actual)) fail(message);
    }
    static void assertTrue(boolean condition, String message) { checks++; if (!condition) fail(message); }
    static void assertFalse(boolean condition, String message) { checks++; if (condition) fail(message); }
    static void assertNull(Object value, String message) { checks++; if (value != null) fail(message); }
    static void assertNotNull(Object value, String message) { checks++; if (value == null) fail(message); }
    static void assertThrows(Class<? extends Throwable> type, Runnable action, String message) {
        checks++;
        try { action.run(); } catch (Throwable actual) {
            if (type.isInstance(actual)) return;
            fail(message + " (wrong exception: " + actual.getClass().getSimpleName() + ")");
        }
        fail(message + " (no exception)");
    }
    static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual, String message) {
        checks++;
        if (expected == null || actual == null || expected.compareTo(actual) != 0) fail(message);
    }
    private static void fail(String message) { throw new AssertionError(message); }
}
