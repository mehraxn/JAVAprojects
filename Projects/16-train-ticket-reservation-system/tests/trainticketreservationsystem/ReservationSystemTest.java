package trainticketreservationsystem;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static trainticketreservationsystem.TestSupport.assertEquals;
import static trainticketreservationsystem.TestSupport.assertFalse;
import static trainticketreservationsystem.TestSupport.assertNotNull;
import static trainticketreservationsystem.TestSupport.assertThrows;
import static trainticketreservationsystem.TestSupport.assertTrue;

final class ReservationSystemTest {

    private ReservationSystemTest() {
    }

    // A fixed clock so reservation timestamps are deterministic.
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-08-01T09:30:00Z"), ZoneOffset.UTC);

    private static List<Seat> seats(int count) {
        List<Seat> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(new Seat(i));
        }
        return list;
    }

    /** Fresh system with route R-BM and train ICE-101 (5 seats). */
    private static ReservationSystem sample() {
        ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
        system.addRoute(new Route("R-BM", "Berlin", "Munich"));
        system.addTrain(new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"), seats(5)));
        return system;
    }

    static void register(TestRunner runner) {
        registerManagement(runner);
        registerReservations(runner);
        registerSearchAndClock(runner);
    }

    private static void registerManagement(TestRunner runner) {
        runner.test("System: addRoute then listRoutes works", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            system.addRoute(new Route("R1", "Berlin", "Munich"));
            assertEquals(1, system.listRoutes().size(), "one route");
            assertEquals("R1", system.listRoutes().get(0).getId(), "route id");
        });

        runner.test("System: duplicate route ID rejected", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            system.addRoute(new Route("R1", "Berlin", "Munich"));
            assertThrows(IllegalArgumentException.class,
                    () -> system.addRoute(new Route("R1", "Paris", "Lyon")), "dup id");
        });

        runner.test("System: duplicate route pair rejected", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            system.addRoute(new Route("R1", "Berlin", "Munich"));
            assertThrows(IllegalArgumentException.class,
                    () -> system.addRoute(new Route("R2", "berlin", "munich")), "dup pair");
        });

        runner.test("System: addTrain works with value-matched route object", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            system.addRoute(new Route("R-BM", "Berlin", "Munich"));
            // A fresh Route object with the same data must be accepted.
            system.addTrain(new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"), seats(3)));
            assertEquals(1, system.listTrains().size(), "one train");
        });

        runner.test("System: train with unregistered route rejected", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            assertThrows(IllegalArgumentException.class,
                    () -> system.addTrain(new Train("ICE-1", new Route("R-X", "A", "B"), seats(2))),
                    "unregistered route");
        });

        runner.test("System: train whose route ID matches but stations differ rejected", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            system.addRoute(new Route("R-BM", "Berlin", "Munich"));
            assertThrows(IllegalArgumentException.class,
                    () -> system.addTrain(new Train("ICE-1", new Route("R-BM", "Berlin", "Cologne"), seats(2))),
                    "mismatched stations");
        });

        runner.test("System: duplicate train ID rejected", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.addTrain(new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"), seats(2))),
                    "dup train");
        });

        runner.test("System: getTrain returns snapshot with seat details", () -> {
            ReservationSystem system = sample();
            TrainSnapshot train = system.getTrain("ICE-101");
            assertEquals("ICE-101", train.getTrainId(), "train id");
            assertEquals(5, train.getTotalSeats(), "total seats");
            assertEquals(5, train.getAvailableSeats(), "available seats");
        });

        runner.test("System: unknown train rejected", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.getTrain("NOPE"), "unknown train");
        });
    }

    private static void registerReservations(TestRunner runner) {
        runner.test("System: reserve specific seat works and generates ID", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 3, "Sofia");
            assertEquals("R0001", reservation.getReservationId(), "generated id");
            assertEquals(3, reservation.getSeatNumber(), "seat 3");
            assertEquals("Sofia", reservation.getPassengerName(), "passenger");
            assertTrue(reservation.isActive(), "active");
            assertEquals(4, system.getAvailableSeats("ICE-101").size(), "one seat used");
        });

        runner.test("System: reserve first available seat is deterministic", () -> {
            ReservationSystem system = sample();
            system.reserveSeat("ICE-101", 1, "A");
            ReservationSnapshot auto = system.reserveFirstAvailableSeat("ICE-101", "B");
            assertEquals(2, auto.getSeatNumber(), "picks seat 2");
        });

        runner.test("System: passenger name required", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.reserveSeat("ICE-101", 1, "  "), "blank name");
        });

        runner.test("System: reserve unknown train rejected", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.reserveFirstAvailableSeat("NOPE", "Sofia"), "unknown train");
        });

        runner.test("System: reserve unknown seat rejected", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.reserveSeat("ICE-101", 99, "Sofia"), "unknown seat");
        });

        runner.test("System: reserve already reserved seat rejected", () -> {
            ReservationSystem system = sample();
            system.reserveSeat("ICE-101", 1, "Sofia");
            assertThrows(IllegalStateException.class,
                    () -> system.reserveSeat("ICE-101", 1, "Marco"), "double booking");
        });

        runner.test("System: full train reservation rejected", () -> {
            ReservationSystem system = sample();
            for (int i = 0; i < 5; i++) {
                system.reserveFirstAvailableSeat("ICE-101", "P" + i);
            }
            assertThrows(IllegalStateException.class,
                    () -> system.reserveFirstAvailableSeat("ICE-101", "Late"), "full train");
        });

        runner.test("System: failed reservation leaves seat state unchanged", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.reserveSeat("ICE-101", 99, "Sofia"), "bad seat");
            assertEquals(5, system.getAvailableSeats("ICE-101").size(), "still 5 free");
            assertEquals(0, system.listReservations().size(), "no reservations recorded");
        });

        runner.test("System: cancellation releases the exact seat", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 4, "Sofia");
            system.cancelReservation(reservation.getReservationId());
            assertEquals(5, system.getAvailableSeats("ICE-101").size(), "seat released");
            assertTrue(seatAvailable(system, "ICE-101", 4), "seat 4 free again");
        });

        runner.test("System: cancelled reservation stays in history as CANCELLED", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 4, "Sofia");
            system.cancelReservation(reservation.getReservationId());
            ReservationSnapshot found = system.findReservationById(reservation.getReservationId());
            assertEquals(ReservationStatus.CANCELLED, found.getStatus(), "cancelled status");
            assertEquals(1, system.listReservations().size(), "still in history");
        });

        runner.test("System: seat can be reserved again after cancellation", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot first = system.reserveSeat("ICE-101", 4, "Sofia");
            system.cancelReservation(first.getReservationId());
            ReservationSnapshot second = system.reserveSeat("ICE-101", 4, "Marco");
            assertEquals("R0002", second.getReservationId(), "new id");
            assertEquals(4, second.getSeatNumber(), "same seat reused");
        });

        runner.test("System: cancel missing reservation rejected", () -> {
            ReservationSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.cancelReservation("R9999"), "missing");
        });

        runner.test("System: cancel already-cancelled reservation rejected", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 4, "Sofia");
            system.cancelReservation(reservation.getReservationId());
            assertThrows(IllegalStateException.class,
                    () -> system.cancelReservation(reservation.getReservationId()), "double cancel");
        });

        runner.test("System: listReservationsForTrain filters by train", () -> {
            ReservationSystem system = sample();
            system.addTrain(new Train("ICE-102", new Route("R-BM", "Berlin", "Munich"), seats(2)));
            system.reserveSeat("ICE-101", 1, "Sofia");
            system.reserveSeat("ICE-102", 1, "Marco");
            assertEquals(1, system.listReservationsForTrain("ICE-101").size(), "one for 101");
            assertEquals(2, system.listReservations().size(), "two overall");
        });
    }

    private static void registerSearchAndClock(TestRunner runner) {
        runner.test("System: search by route is case-insensitive", () -> {
            ReservationSystem system = sample();
            assertEquals(1, system.searchByRoute("berlin", "MUNICH").size(), "match found");
        });

        runner.test("System: reverse route does not match (direction-sensitive)", () -> {
            ReservationSystem system = sample();
            assertTrue(system.searchByRoute("Munich", "Berlin").isEmpty(), "no reverse match");
        });

        runner.test("System: search result order is deterministic (insertion order)", () -> {
            ReservationSystem system = sample();
            system.addTrain(new Train("ICE-102", new Route("R-BM", "Berlin", "Munich"), seats(2)));
            List<TrainSnapshot> results = system.searchByRoute("Berlin", "Munich");
            assertEquals(2, results.size(), "two trains");
            assertEquals("ICE-101", results.get(0).getTrainId(), "first is ICE-101");
            assertEquals("ICE-102", results.get(1).getTrainId(), "second is ICE-102");
        });

        runner.test("System: reservation uses injected clock for timestamp", () -> {
            ReservationSystem system = sample();
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 1, "Sofia");
            LocalDateTime expected = LocalDateTime.ofInstant(
                    Instant.parse("2026-08-01T09:30:00Z"), ZoneId.from(ZoneOffset.UTC));
            assertEquals(expected, reservation.getReservedAt(), "deterministic timestamp");
        });

        runner.test("System: default constructor uses a working system clock", () -> {
            ReservationSystem system = new ReservationSystem();
            system.addRoute(new Route("R-BM", "Berlin", "Munich"));
            system.addTrain(new Train("ICE-101", new Route("R-BM", "Berlin", "Munich"), seats(2)));
            ReservationSnapshot reservation = system.reserveSeat("ICE-101", 1, "Sofia");
            assertNotNull(reservation.getReservedAt(), "timestamp present");
        });

        runner.test("System: empty system returns empty unmodifiable lists", () -> {
            ReservationSystem system = new ReservationSystem(FIXED_CLOCK);
            assertTrue(system.listRoutes().isEmpty(), "no routes");
            assertTrue(system.listTrains().isEmpty(), "no trains");
            assertTrue(system.listReservations().isEmpty(), "no reservations");
        });
    }

    private static boolean seatAvailable(ReservationSystem system, String trainId, int seatNumber) {
        for (SeatSnapshot seat : system.getAvailableSeats(trainId)) {
            if (seat.getNumber() == seatNumber) {
                return true;
            }
        }
        return false;
    }
}
