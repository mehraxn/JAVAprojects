package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static movieticketbookingsystem.TestSupport.assertBigDecimalEquals;
import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertFalse;
import static movieticketbookingsystem.TestSupport.assertThrows;
import static movieticketbookingsystem.TestSupport.assertTrue;

final class BookingSystemTest {

    private BookingSystemTest() {
    }

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T10:00:00Z"), ZoneId.of("UTC"));
    private static final LocalDateTime FIXED_TIME = LocalDateTime.now(FIXED_CLOCK);
    private static final LocalDateTime WHEN = LocalDateTime.of(2026, 7, 4, 19, 30);

    private static List<Seat> seats(String... ids) {
        List<Seat> list = new ArrayList<>();
        for (String id : ids) {
            list.add(new Seat(id));
        }
        return list;
    }

    private static Showtime showtime(String price, String... seatIds) {
        Movie movie = new Movie("M001", "The Java Journey", "Adventure", 120);
        return new Showtime("S001", movie, WHEN, new BigDecimal(price), seats(seatIds));
    }

    private static BookingSystem sample() {
        BookingSystem system = new BookingSystem(FIXED_CLOCK);
        system.addShowtime(showtime("12.00", "A1", "A2", "A3", "B1", "B2", "B3"));
        return system;
    }

    private static List<String> availableSeatIds(BookingSystem system) {
        List<String> ids = new ArrayList<>();
        for (SeatSnapshot seat : system.getAvailableSeats("S001")) {
            ids.add(seat.getSeatId());
        }
        return ids;
    }

    static void register(TestRunner runner) {
        registerManagement(runner);
        registerBooking(runner);
        registerAllOrNothing(runner);
        registerCancellation(runner);
        registerAvailability(runner);
    }

    private static void registerManagement(TestRunner runner) {
        runner.test("System: add showtime then list works", () -> {
            BookingSystem system = sample();
            assertEquals(1, system.listShowtimes().size(), "one showtime");
            assertEquals("S001", system.listShowtimes().get(0).getShowtimeId(), "id present");
        });

        runner.test("System: null showtime rejected", () -> {
            BookingSystem system = new BookingSystem(FIXED_CLOCK);
            assertThrows(IllegalArgumentException.class,
                    () -> system.addShowtime(null), "null showtime");
        });

        runner.test("System: duplicate showtime ID rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.addShowtime(showtime("9.00", "C1")), "duplicate id");
        });

        runner.test("System: getShowtime returns snapshot, unknown returns empty", () -> {
            BookingSystem system = sample();
            assertTrue(system.getShowtime("S001").isPresent(), "present");
            assertFalse(system.getShowtime("NOPE").isPresent(), "empty");
        });
    }

    private static void registerBooking(TestRunner runner) {
        runner.test("System: book single seat works", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertEquals(Arrays.asList("A1"), booking.getSeatIds(), "one seat");
            assertEquals(5, system.getAvailableSeats("S001").size(), "5 left");
        });

        runner.test("System: book multiple seats works", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice",
                    Arrays.asList("A1", "A2", "B1"));
            assertEquals(3, booking.getSeatIds().size(), "three seats");
            assertEquals(3, system.getAvailableSeats("S001").size(), "3 left");
        });

        runner.test("System: booking IDs are B0001, B0002 sequentially", () -> {
            BookingSystem system = sample();
            BookingSnapshot first = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            BookingSnapshot second = system.bookSeats("S001", "Bob", Arrays.asList("A2"));
            assertEquals("B0001", first.getBookingId(), "first id");
            assertEquals("B0002", second.getBookingId(), "second id");
        });

        runner.test("System: blank customer name rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "  ", Arrays.asList("A1")), "blank customer");
        });

        runner.test("System: unknown showtime rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("NOPE", "Alice", Arrays.asList("A1")), "unknown showtime");
        });

        runner.test("System: unknown seat rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "Alice", Arrays.asList("Z9")), "unknown seat");
        });

        runner.test("System: already booked seat rejected", () -> {
            BookingSystem system = sample();
            system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertThrows(IllegalStateException.class,
                    () -> system.bookSeats("S001", "Bob", Arrays.asList("A1")), "already booked");
        });

        runner.test("System: duplicate seat in request rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "Alice", Arrays.asList("A1", "A1")),
                    "duplicate request");
        });

        runner.test("System: empty seat request rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "Alice", new ArrayList<>()), "empty request");
        });

        runner.test("System: full showtime booking rejected", () -> {
            BookingSystem system = sample();
            system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2", "A3", "B1", "B2", "B3"));
            assertThrows(IllegalStateException.class,
                    () -> system.bookSeats("S001", "Bob", Arrays.asList("A1")), "full showtime");
        });

        runner.test("System: total price correct for one seat", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertBigDecimalEquals(new BigDecimal("12.00"), booking.getTotalPrice(), "one seat total");
        });

        runner.test("System: total price correct for multiple seats", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice",
                    Arrays.asList("A1", "A2", "A3"));
            assertBigDecimalEquals(new BigDecimal("36.00"), booking.getTotalPrice(), "three seat total");
        });

        runner.test("System: decimal ticket price total works", () -> {
            BookingSystem system = new BookingSystem(FIXED_CLOCK);
            system.addShowtime(showtime("9.99", "A1", "A2"));
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2"));
            assertBigDecimalEquals(new BigDecimal("19.98"), booking.getTotalPrice(), "9.99 x 2");
        });

        runner.test("System: booking uses fixed Clock timestamp", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertEquals(FIXED_TIME, booking.getBookedAt(), "fixed timestamp");
        });

        runner.test("System: bookSeats returns snapshot and records it", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            assertTrue(system.findBookingById(booking.getBookingId()).isPresent(), "recorded");
            assertEquals("Alice",
                    system.findBookingById(booking.getBookingId()).get().getCustomerName(), "name");
        });
    }

    private static void registerAllOrNothing(TestRunner runner) {
        runner.test("System: mixed valid + unknown seat fails with no state change", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "Alice", Arrays.asList("A1", "Z9")), "mixed unknown");
            assertEquals(6, system.getAvailableSeats("S001").size(), "all seats still free");
            assertEquals(0, system.listBookings().size(), "no booking created");
        });

        runner.test("System: mixed valid + already booked seat fails with no state change", () -> {
            BookingSystem system = sample();
            system.bookSeats("S001", "Alice", Arrays.asList("B1"));
            int availableBefore = system.getAvailableSeats("S001").size();
            int bookingsBefore = system.listBookings().size();
            assertThrows(IllegalStateException.class,
                    () -> system.bookSeats("S001", "Bob", Arrays.asList("A1", "B1")), "mixed taken");
            assertEquals(availableBefore, system.getAvailableSeats("S001").size(), "available unchanged");
            assertEquals(bookingsBefore, system.listBookings().size(), "bookings unchanged");
            // The valid seat A1 must NOT have been reserved.
            assertTrue(availableSeatIds(system).contains("A1"), "A1 still free");
        });

        runner.test("System: duplicate seat request fails with no state change", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.bookSeats("S001", "Alice", Arrays.asList("A1", "A1")), "dup request");
            assertEquals(6, system.getAvailableSeats("S001").size(), "nothing reserved");
            assertEquals(0, system.listBookings().size(), "no booking");
        });
    }

    private static void registerCancellation(TestRunner runner) {
        runner.test("System: cancellation releases exact seats", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2"));
            assertEquals(4, system.getAvailableSeats("S001").size(), "4 free after booking");
            system.cancelBooking(booking.getBookingId());
            assertEquals(6, system.getAvailableSeats("S001").size(), "6 free after cancel");
            assertTrue(availableSeatIds(system).contains("A1"), "A1 released");
            assertTrue(availableSeatIds(system).contains("A2"), "A2 released");
        });

        runner.test("System: cancellation of missing booking rejected", () -> {
            BookingSystem system = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> system.cancelBooking("B9999"), "missing booking");
        });

        runner.test("System: cancellation of already cancelled booking rejected", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            system.cancelBooking(booking.getBookingId());
            assertThrows(IllegalStateException.class,
                    () -> system.cancelBooking(booking.getBookingId()), "double cancel");
        });

        runner.test("System: cancelled booking stays in history with CANCELLED status", () -> {
            BookingSystem system = sample();
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            system.cancelBooking(booking.getBookingId());
            assertEquals(1, system.listBookings().size(), "still in history");
            assertEquals(BookingStatus.CANCELLED,
                    system.findBookingById(booking.getBookingId()).get().getStatus(), "cancelled");
        });

        runner.test("System: seats can be booked again after cancellation", () -> {
            BookingSystem system = sample();
            BookingSnapshot first = system.bookSeats("S001", "Alice", Arrays.asList("A1"));
            system.cancelBooking(first.getBookingId());
            BookingSnapshot second = system.bookSeats("S001", "Bob", Arrays.asList("A1"));
            assertEquals("B0002", second.getBookingId(), "new booking id");
            assertEquals(Arrays.asList("A1"), second.getSeatIds(), "A1 rebooked");
        });
    }

    private static void registerAvailability(TestRunner runner) {
        runner.test("System: available seats before/after booking and cancellation", () -> {
            BookingSystem system = sample();
            assertEquals(6, system.getAvailableSeats("S001").size(), "6 before");
            BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2"));
            assertEquals(4, system.getAvailableSeats("S001").size(), "4 after booking");
            system.cancelBooking(booking.getBookingId());
            assertEquals(6, system.getAvailableSeats("S001").size(), "6 after cancel");
        });

        runner.test("System: full showtime detection via snapshot", () -> {
            BookingSystem system = sample();
            system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2", "A3", "B1", "B2", "B3"));
            assertTrue(system.getShowtime("S001").get().isFull(), "is full");
            assertEquals(0, system.getShowtime("S001").get().getAvailableSeatCount(), "0 available");
        });
    }
}
