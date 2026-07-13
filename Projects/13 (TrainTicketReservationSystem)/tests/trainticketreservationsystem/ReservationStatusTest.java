package trainticketreservationsystem;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertNotNull;

final class ReservationStatusTest {

    private ReservationStatusTest() {
    }

    static void register(TestRunner runner) {
        runner.test("ReservationStatus: has ACTIVE and CANCELLED values", () -> {
            assertEquals(2, ReservationStatus.values().length, "two states");
            assertNotNull(ReservationStatus.valueOf("ACTIVE"), "ACTIVE present");
            assertNotNull(ReservationStatus.valueOf("CANCELLED"), "CANCELLED present");
        });
    }
}
