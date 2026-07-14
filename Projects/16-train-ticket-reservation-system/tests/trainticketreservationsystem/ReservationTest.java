package trainticketreservationsystem;

import java.time.LocalDateTime;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

final class ReservationTest {

    private ReservationTest() {
    }

    private static final LocalDateTime WHEN = LocalDateTime.of(2026, 8, 1, 9, 30);

    static void register(TestRunner runner) {
        runner.test("Reservation: valid creation stores all fields and starts ACTIVE", () -> {
            Reservation reservation = new Reservation("R0001", "ICE-1", 3, "Sofia", WHEN);
            assertEquals("R0001", reservation.getId(), "id stored");
            assertEquals("ICE-1", reservation.getTrainId(), "train id stored");
            assertEquals(3, reservation.getSeatNumber(), "seat number stored");
            assertEquals("Sofia", reservation.getPassengerName(), "passenger stored");
            assertEquals(WHEN, reservation.getReservedAt(), "timestamp stored");
            assertEquals(ReservationStatus.ACTIVE, reservation.getStatus(), "starts ACTIVE");
            assertTrue(reservation.isActive(), "isActive true");
        });

        runner.test("Reservation: null/blank id rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Reservation(null, "ICE-1", 1, "Sofia", WHEN), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Reservation("  ", "ICE-1", 1, "Sofia", WHEN), "blank id");
        });

        runner.test("Reservation: null/blank train id rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Reservation("R1", "  ", 1, "Sofia", WHEN), "blank train id"));

        runner.test("Reservation: non-positive seat rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Reservation("R1", "ICE-1", 0, "Sofia", WHEN), "seat 0"));

        runner.test("Reservation: null/blank passenger rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Reservation("R1", "ICE-1", 1, null, WHEN), "null passenger");
            assertThrows(IllegalArgumentException.class,
                    () -> new Reservation("R1", "ICE-1", 1, "   ", WHEN), "blank passenger");
        });

        runner.test("Reservation: null timestamp rejected", () ->
                assertThrows(NullPointerException.class,
                        () -> new Reservation("R1", "ICE-1", 1, "Sofia", null), "null time"));

        runner.test("Reservation: cancel moves status to CANCELLED", () -> {
            Reservation reservation = new Reservation("R1", "ICE-1", 1, "Sofia", WHEN);
            reservation.cancel();
            assertEquals(ReservationStatus.CANCELLED, reservation.getStatus(), "cancelled");
        });

        runner.test("Reservation: double cancel rejected", () -> {
            Reservation reservation = new Reservation("R1", "ICE-1", 1, "Sofia", WHEN);
            reservation.cancel();
            assertThrows(IllegalStateException.class, reservation::cancel, "double cancel");
        });
    }
}
