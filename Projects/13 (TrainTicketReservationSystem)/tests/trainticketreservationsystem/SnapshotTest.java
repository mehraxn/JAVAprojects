package trainticketreservationsystem;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

/**
 * Proves that public data leaving {@link ReservationSystem} cannot be used to
 * mutate internal state: returned lists are unmodifiable and returned snapshots
 * are decoupled from live domain objects.
 */
final class SnapshotTest {

    private SnapshotTest() {
    }

    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-01T09:30:00Z"), ZoneOffset.UTC);

    private static List<Seat> seats(int count) {
        List<Seat> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Seat(i));
        }
        return list;
    }

    private static ReservationSystem sample() {
        ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
        system.addRoute(new Route("R-BM", "Berlin", "Munich"));
        system.addTrain(new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"), seats(5)));
        return system;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: getAvailableSeats list is unmodifiable", () -> {
            ReservationSystem system = sample();
            List<SeatSnapshot> seats = system.getAvailableSeats("ICE-101");
            assertThrows(UnsupportedOperationException.class,
                    () -> seats.remove(0), "cannot modify seat list");
        });

        runner.test("Snapshot: listTrains list is unmodifiable", () -> {
            ReservationSystem system = sample();
            List<TrainSnapshot> trains = system.listTrains();
            assertThrows(UnsupportedOperationException.class,
                    () -> trains.clear(), "cannot modify train list");
        });

        runner.test("Snapshot: searchByRoute list is unmodifiable", () -> {
            ReservationSystem system = sample();
            List<TrainSnapshot> results = system.searchByRoute("Berlin", "Munich");
            assertThrows(UnsupportedOperationException.class,
                    () -> results.clear(), "cannot modify search results");
        });

        runner.test("Snapshot: listReservations list is unmodifiable", () -> {
            ReservationSystem system = sample();
            system.reserveSeat("ICE-101", 1, "Sofia");
            List<ReservationSnapshot> reservations = system.listReservations();
            assertThrows(UnsupportedOperationException.class,
                    () -> reservations.clear(), "cannot modify reservation list");
        });

        runner.test("Snapshot: TrainSnapshot seat list is unmodifiable", () -> {
            ReservationSystem system = sample();
            List<SeatSnapshot> seats = system.getTrain("ICE-101").getSeats();
            assertThrows(UnsupportedOperationException.class,
                    () -> seats.remove(0), "cannot modify snapshot seats");
        });

        runner.test("Snapshot: seat snapshot is a decoupled copy", () -> {
            ReservationSystem system = sample();
            // Snapshot reflects state at capture time and does not change afterwards.
            List<SeatSnapshot> before = system.getTrain("ICE-101").getSeats();
            SeatSnapshot seatOneBefore = before.get(0);
            assertTrue(seatOneBefore.isAvailable(), "seat 1 initially available");
            system.reserveSeat("ICE-101", 1, "Sofia");
            // The previously captured snapshot is unchanged (decoupled from live seat).
            assertTrue(seatOneBefore.isAvailable(), "old snapshot unchanged after reservation");
            // A fresh snapshot reflects the new state.
            SeatSnapshot seatOneAfter = system.getTrain("ICE-101").getSeats().get(0);
            assertTrue(seatOneAfter.isReserved(), "fresh snapshot reflects reservation");
        });

        runner.test("Snapshot: reservation count reflects only system operations", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 1, "Sofia");
            // Holding the snapshot cannot cancel or alter the reservation.
            assertEquals(1, system.listReservations().size(), "one reservation");
            assertTrue(reservation.isActive(), "still active");
            assertEquals(4, system.getAvailableSeats("ICE-101").size(), "seat still held");
        });
    }
}
