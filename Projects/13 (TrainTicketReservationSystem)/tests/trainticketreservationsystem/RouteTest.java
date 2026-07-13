package trainticketreservationsystem;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertFalse;
import static trainticketreservationsystem.TestSupport.assertNotEquals;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

final class RouteTest {

    private RouteTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Route: valid creation stores id/origin/destination", () -> {
            Route route = new Route("R1", "Berlin", "Munich");
            assertEquals("R1", route.getId(), "id stored");
            assertEquals("Berlin", route.getOrigin(), "origin stored");
            assertEquals("Munich", route.getDestination(), "destination stored");
        });

        runner.test("Route: fields are trimmed", () -> {
            Route route = new Route("  R2 ", "  Paris ", " Lyon ");
            assertEquals("R2", route.getId(), "id trimmed");
            assertEquals("Paris", route.getOrigin(), "origin trimmed");
            assertEquals("Lyon", route.getDestination(), "destination trimmed");
        });

        runner.test("Route: null/blank id rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Route(null, "A", "B"), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Route("  ", "A", "B"), "blank id");
        });

        runner.test("Route: null/blank origin rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Route("R", null, "B"), "null origin");
            assertThrows(IllegalArgumentException.class,
                    () -> new Route("R", "   ", "B"), "blank origin");
        });

        runner.test("Route: null/blank destination rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Route("R", "A", null), "null destination");
            assertThrows(IllegalArgumentException.class,
                    () -> new Route("R", "A", "   "), "blank destination");
        });

        runner.test("Route: equal origin/destination rejected ignoring case", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Route("R", "Berlin", "berlin"), "same station"));

        runner.test("Route: matches is case-insensitive and direction-sensitive", () -> {
            Route route = new Route("R", "Berlin", "Munich");
            assertTrue(route.matches("berlin", "MUNICH"), "case-insensitive forward");
            assertFalse(route.matches("Munich", "Berlin"), "reverse does not match");
        });

        runner.test("Route: value equality by id and case-insensitive stations", () -> {
            Route a = new Route("R", "Berlin", "Munich");
            Route b = new Route("R", "berlin", "munich");
            Route c = new Route("R", "Berlin", "Cologne");
            assertEquals(a, b, "same id/stations equal");
            assertEquals(a.hashCode(), b.hashCode(), "equal objects share hashCode");
            assertNotEquals(a, c, "different destination not equal");
        });
    }
}
