package eventregistrationsystem;

final class Assertions {
    private static int checks;
    private Assertions() { }
    static int getChecks() { return checks; }
    static void assertEquals(Object expected, Object actual, String message) { checks++; if (!java.util.Objects.equals(expected, actual)) fail(message); }
    static void assertNotEquals(Object unexpected, Object actual, String message) { checks++; if (java.util.Objects.equals(unexpected, actual)) fail(message); }
    static void assertTrue(boolean condition, String message) { checks++; if (!condition) fail(message); }
    static void assertFalse(boolean condition, String message) { assertTrue(!condition, message); }
    static void assertNull(Object value, String message) { checks++; if (value != null) fail(message); }
    static void assertNotNull(Object value, String message) { checks++; if (value == null) fail(message); }
    static void assertContains(String text, String expected, String message) { checks++; if (text == null || !text.contains(expected)) fail(message); }
    static void assertThrows(Class<? extends Throwable> type, Runnable action, String message) {
        checks++;
        try { action.run(); } catch (Throwable thrown) { if (type.isInstance(thrown)) return; throw new AssertionError(message + " (wrong exception)", thrown); }
        fail(message + " (no exception)");
    }
    private static void fail(String message) { throw new AssertionError(message); }
}
