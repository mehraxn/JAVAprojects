package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertThrows;
import static movieticketbookingsystem.TestSupport.assertTrue;

/**
 * Proves that data leaving {@link BookingSystem} is safe: returned lists are
 * unmodifiable, snapshots carry no mutators, and holding a snapshot cannot change
 * internal seat, showtime, or booking state.
 */
final class SnapshotTest {

    private SnapshotTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));

    private static List<Seat> seats(String... ids) {
        List<Seat> list = new ArrayList<>();
        for (String id : ids) {
            list.add(new Seat(id));
        }
        return list;
    }

    private static BookingSystem sample() {
        BookingSystem system = new BookingSystem(FIXED_CLOCK);
        Movie movie = new Movie("M001", "The Java Journey", "Adventure", 120);
        system.addShowtime(new Showtime("S001", movie,
                LocalDateTime.of(2026, 7, 4, 19, 30), new BigDecimal("12.00"),
                seats("A1", "A2", "A3")));
        return system;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: getAvailableSeats result is unmodifiable", () -> {
            List<SeatSnapshot> seats = sample().getAvailableSeats("S001");
            assertThrows(UnsupportedOperationException.class,
                    () -> seats.remove(0), "unmodifiable available seats");
        });

        runner.test("Snapshot: listShowtimes result is unmodifiable", () -> {
            List<ShowtimeSnapshot> showtimes = sample().listShowtimes();
            assertThrows(UnsupportedOperationException.class,
                    () -> showtimes.clear(), "unmodifiable showtimes");
        });

        runner.test("Snapshot: listBookings result is unmodifiable", () -> {
            BookingSystem system = sample();
            system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            List<BookingSnapshot> bookings = system.listBookings();
            assertThrows(UnsupportedOperationException.class,
                    () -> bookings.clear(), "unmodifiable bookings");
        });

        runner.test("Snapshot: ShowtimeSnapshot seat list is unmodifiable", () -> {
            ShowtimeSnapshot showtime = sample().getShowtime("S001").orElseThrow();
            assertThrows(UnsupportedOperationException.class,
                    () -> showtime.getSeats().clear(), "unmodifiable snapshot seat map");
        });

        runner.test("Snapshot: SeatSnapshot cannot mutate internal Seat", () -> {
            BookingSystem system = sample();
            SeatSnapshot seat = system.getAvailableSeats("S001").get(0);
            // SeatSnapshot exposes no reserve/release; availability stays constant
            // and the internal seat is untouched.
            assertTrue(seat.isAvailable(), "snapshot available");
            assertEquals(3, system.getShowtime("S001").get().getAvailableSeatCount(),
                    "internal count intact");
        });

        runner.test("Snapshot: BookingSnapshot seat list mutation does not affect system", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2"));
            try {
                booking.getSeatIds().add("A3");
            } catch (UnsupportedOperationException ignored) {
                // expected — unmodifiable
            }
            assertEquals(2,
                    system.findBookingById(booking.getBookingId()).get().getSeatIds().size(),
                    "internal booking seats unchanged");
        });

        runner.test("Snapshot: ShowtimeSnapshot is decoupled from live showtime", () -> {
            BookingSystem system = sample();
            ShowtimeSnapshot before = system.getShowtime("S001").orElseThrow();
            assertEquals(3, before.getAvailableSeatCount(), "captured 3 available");
            system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            // The previously captured snapshot must not change.
            assertEquals(3, before.getAvailableSeatCount(), "old snapshot unchanged");
            assertEquals(2, system.getShowtime("S001").get().getAvailableSeatCount(),
                    "fresh snapshot updated");
        });

        runner.test("Snapshot: BookingSnapshot status is decoupled from live booking", () -> {
            BookingSystem system = sample();
            BookingSnapshot active = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertEquals(BookingStatus.ACTIVE, active.getStatus(), "captured ACTIVE");
            system.cancelBooking(active.getBookingId());
            assertEquals(BookingStatus.ACTIVE, active.getStatus(), "old snapshot still ACTIVE");
            assertEquals(BookingStatus.CANCELLED,
                    system.findBookingById(active.getBookingId()).get().getStatus(),
                    "fresh snapshot CANCELLED");
        });

        runner.test("Snapshot: reading snapshots leaves internal available count correct", () -> {
            BookingSystem system = sample();
            system.getAvailableSeats("S001");
            system.listShowtimes();
            system.getShowtime("S001");
            assertEquals(3, system.getShowtime("S001").get().getAvailableSeatCount(),
                    "count still 3 after reads");
        });
    }
}
