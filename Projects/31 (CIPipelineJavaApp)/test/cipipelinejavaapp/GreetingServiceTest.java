package cipipelinejavaapp;

/**
 * Dependency-free test runner. Exits 0 and prints a summary when every check
 * passes; prints the failing check and exits 1 otherwise, so CI can use the
 * process exit code directly.
 */
public class GreetingServiceTest {
    private static int checksRun = 0;

    public static void main(String[] args) {
        try {
            GreetingService service = new GreetingService();

            assertEquals("Hello, Java!", service.createGreeting("Java"));
            assertEquals("Hello, CI!", service.createGreeting("  CI  "));

            // Boundary: exactly 80 characters is allowed, 81 is rejected.
            String longestAllowedName = repeat('a', 80);
            assertEquals("Hello, " + longestAllowedName + "!",
                    service.createGreeting(longestAllowedName));
            assertThrowsForInvalidName(service, repeat('a', 81));

            assertThrowsForInvalidName(service, null);
            assertThrowsForInvalidName(service, "");
            assertThrowsForInvalidName(service, "   ");
        } catch (AssertionError failure) {
            System.err.println("TEST FAILED: " + failure.getMessage());
            System.exit(1);
        }
        System.out.println("All tests passed (" + checksRun + " checks).");
    }

    private static void assertEquals(String expected, String actual) {
        checksRun++;
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Expected '" + expected + "' but got '" + actual + "'.");
        }
    }

    private static void assertThrowsForInvalidName(GreetingService service, String name) {
        checksRun++;
        try {
            service.createGreeting(name);
            throw new AssertionError(
                    "Expected invalid name to be rejected: '" + name + "'.");
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
