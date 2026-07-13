package movieticketbookingsystem;

import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertFalse;
import static movieticketbookingsystem.TestSupport.assertThrows;
import static movieticketbookingsystem.TestSupport.assertTrue;

final class SeatTest {

    private SeatTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Seat: valid creation stores seat ID and starts available", () -> {
            Seat seat = new Seat("A1");
            assertEquals("A1", seat.getSeatId(), "seat id stored");
            assertTrue(seat.isAvailable(), "starts available");
        });

        runner.test("Seat: seat ID is trimmed", () ->
                assertEquals("A1", new Seat("  A1 ").getSeatId(), "trimmed"));

        runner.test("Seat: null/blank seat ID rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> new Seat(null), "null id");
            assertThrows(IllegalArgumentException.class, () -> new Seat("  "), "blank id");
        });

        runner.test("Seat: reserving an available seat works", () -> {
            Seat seat = new Seat("A1");
            seat.reserve();
            assertFalse(seat.isAvailable(), "now reserved");
        });

        runner.test("Seat: reserving an already reserved seat rejected", () -> {
            Seat seat = new Seat("A1");
            seat.reserve();
            assertThrows(IllegalStateException.class, seat::reserve, "double reserve");
        });

        runner.test("Seat: releasing a reserved seat works", () -> {
            Seat seat = new Seat("A1");
            seat.reserve();
            seat.release();
            assertTrue(seat.isAvailable(), "released");
        });

        runner.test("Seat: releasing an available seat rejected", () -> {
            Seat seat = new Seat("A1");
            assertThrows(IllegalStateException.class, seat::release, "release available");
        });

        runner.test("Seat: snapshot contains expected data", () -> {
            Seat seat = new Seat("A1");
            SeatSnapshot available = seat.toSnapshot();
            assertEquals("A1", available.getSeatId(), "snapshot id");
            assertTrue(available.isAvailable(), "snapshot available");
            seat.reserve();
            assertFalse(seat.toSnapshot().isAvailable(), "snapshot reserved after reserve");
            // Old snapshot must not change.
            assertTrue(available.isAvailable(), "old snapshot unchanged");
        });
    }
}
