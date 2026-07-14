package securecicdpipeline;

/**
 * Dependency-free test (same style as the other projects in this repo): a
 * main method with hand-rolled assertions. Run by the pipeline's test stage.
 */
public final class BuildInfoTest {

    public static void main(String[] args) {
        BuildInfo info = new BuildInfo("1.4.0", "abc1234");
        assertEquals("1.4.0", info.version());
        assertEquals("abc1234", info.gitSha());
        assertEquals(
                "ghcr.io/example/app:1.4.0-abc1234",
                info.imageReference("ghcr.io", "example/app"));

        assertThrows(() -> new BuildInfo("1.4", "abc1234"));      // bad semver
        assertThrows(() -> new BuildInfo("1.4.0", "XYZ"));        // bad sha
        assertThrows(() -> new BuildInfo("1.4.0", "abc1234")
                .imageReference("", "repo"));                    // empty registry

        System.out.println("All BuildInfo checks passed.");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected '" + expected + "' but got '" + actual + "'.");
        }
    }

    private static void assertThrows(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected IllegalArgumentException.");
        } catch (IllegalArgumentException expected) {
            // expected validation path
        }
    }
}
