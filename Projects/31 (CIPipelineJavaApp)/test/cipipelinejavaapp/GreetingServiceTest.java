package cipipelinejavaapp;

public class GreetingServiceTest {
    public static void main(String[] args) {
        GreetingService service = new GreetingService();

        assertEquals("Hello, Java!", service.createGreeting("Java"));
        assertEquals("Hello, CI!", service.createGreeting("  CI  "));
        assertThrowsForInvalidName(service, null);
        assertThrowsForInvalidName(service, "   ");
        assertThrowsForInvalidName(service, repeat('a', 81));

        System.out.println("All GreetingService checks passed.");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + actual + "'.");
        }
    }

    private static void assertThrowsForInvalidName(GreetingService service, String name) {
        try {
            service.createGreeting(name);
            throw new AssertionError("Expected invalid name to be rejected.");
        } catch (IllegalArgumentException expected) {
            // Expected validation path.
        }
    }

    private static String repeat(char character, int count) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < count; index++) {
            result.append(character);
        }
        return result.toString();
    }
}
