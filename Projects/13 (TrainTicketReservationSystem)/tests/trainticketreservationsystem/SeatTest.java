package trainticketreservationsystem;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertFalse;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

final class SeatTest {

    private SeatTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Seat: valid creation stores number and starts available", () -> {
            Seat seat = new Seat(7);
            assertEquals(7, seat.getNumber(), "number stored");
            assertTrue(seat.isAvailable(), "starts available");
            assertFalse(seat.isReserved(), "starts not reserved");
        });

        runner.test("Seat: non-positive number rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Seat(0), "zero");
            assertThrows(IllegalArgumentException.class, () -> new Seat(-1), "negative");
        });

        runner.test("Seat: reserving an available seat works", () -> {
            Seat seat = new Seat(1);
            seat.reserve();
            assertTrue(seat.isReserved(), "now reserved");
            assertFalse(seat.isAvailable(), "no longer available");
        });

        runner.test("Seat: reserving an already reserved seat is rejected", () -> {
            Seat seat = new Seat(1);
            seat.reserve();
            assertThrows(IllegalStateException.class, seat::reserve, "double reserve");
        });

        runner.test("Seat: releasing a reserved seat works", () -> {
            Seat seat = new Seat(1);
            seat.reserve();
            seat.release();
            assertTrue(seat.isAvailable(), "available after release");
        });

        runner.test("Seat: releasing an available seat is rejected", () -> {
            Seat seat = new Seat(1);
            assertThrows(IllegalStateException.class, seat::release, "release when free");
        });
    }
}
