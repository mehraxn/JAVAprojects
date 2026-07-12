package hospitalqueuemanagement;

final class TestSupport {
    private static int checks;
    private TestSupport() { }
    static int checks() { return checks; }
    static void assertEquals(Object expected, Object actual, String message) { checks++; if (!java.util.Objects.equals(expected, actual)) fail(message); }
    static void assertNotEquals(Object unexpected, Object actual, String message) { checks++; if (java.util.Objects.equals(unexpected, actual)) fail(message); }
    static void assertTrue(boolean value, String message) { checks++; if (!value) fail(message); }
    static void assertFalse(boolean value, String message) { checks++; if (value) fail(message); }
    static void assertNull(Object value, String message) { checks++; if (value != null) fail(message); }
    static void assertNotNull(Object value, String message) { checks++; if (value == null) fail(message); }
    static void assertContains(String text, String part, String message) { checks++; if (text == null || !text.contains(part)) fail(message); }
    static void assertDoubleEquals(double expected, double actual, double tolerance, String message) { checks++; if (Math.abs(expected - actual) > tolerance) fail(message); }
    static void assertThrows(Class<? extends Throwable> type, Runnable action, String message) {
        checks++;
        try { action.run(); } catch (Throwable thrown) { if (type.isInstance(thrown)) return; throw new AssertionError(message, thrown); }
        fail(message);
    }
    private static void fail(String message) { throw new AssertionError(message); }
}
