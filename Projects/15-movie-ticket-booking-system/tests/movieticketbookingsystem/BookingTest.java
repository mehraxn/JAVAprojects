package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static movieticketbookingsystem.TestSupport.assertBigDecimalEquals;
import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertThrows;
import static movieticketbookingsystem.TestSupport.assertTrue;

final class BookingTest {

    private BookingTest() {
    }

    private static final LocalDateTime WHEN = LocalDateTime.of(2026, 1, 15, 18, 30);

    private static Booking sample() {
        return new Booking("B0001", "S001", "Alice",
                Arrays.asList("A1", "A2"), new BigDecimal("24.00"), WHEN);
    }

    static void register(TestRunner runner) {
        runner.test("Booking: valid creation stores all fields and starts ACTIVE", () -> {
            Booking booking = sample();
            assertEquals("B0001", booking.getBookingId(), "booking id");
            assertEquals("S001", booking.getShowtimeId(), "showtime id");
            assertEquals("Alice", booking.getCustomerName(), "customer");
            assertEquals(Arrays.asList("A1", "A2"), booking.getSeatIds(), "seat ids");
            assertBigDecimalEquals(new BigDecimal("24.00"), booking.getTotalPrice(), "total");
            assertEquals(WHEN, booking.getBookedAt(), "booked at");
            assertEquals(BookingStatus.ACTIVE, booking.getStatus(), "active");
            assertTrue(booking.isActive(), "isActive");
        });

        runner.test("Booking: null/blank booking ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking(null, "S", "C", Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("  ", "S", "C", Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "blank id");
        });

        runner.test("Booking: null/blank showtime ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", null, "C", Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "null showtime");
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "  ", "C", Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "blank showtime");
        });

        runner.test("Booking: null/blank customer name rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", null, Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "null customer");
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", "  ", Arrays.asList("A1"), BigDecimal.ONE, WHEN),
                    "blank customer");
        });

        runner.test("Booking: null/empty seat list rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", "C", null, BigDecimal.ONE, WHEN), "null seats");
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", "C", new ArrayList<>(), BigDecimal.ONE, WHEN),
                    "empty seats");
        });

        runner.test("Booking: null/blank seat ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", "C", Arrays.asList("A1", null), BigDecimal.ONE, WHEN),
                    "null seat");
            assertThrows(IllegalArgumentException.class,
                    () -> new Booking("B", "S", "C", Arrays.asList("A1", "  "), BigDecimal.ONE, WHEN),
                    "blank seat");
        });

        runner.test("Booking: duplicate seat IDs rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Booking("B", "S", "C", Arrays.asList("A1", "A1"),
                                BigDecimal.ONE, WHEN), "duplicate seats"));

        runner.test("Booking: null total price rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Booking("B", "S", "C", Arrays.asList("A1"), null, WHEN),
                        "null price"));

        runner.test("Booking: negative total price rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Booking("B", "S", "C", Arrays.asList("A1"),
                                new BigDecimal("-0.01"), WHEN), "negative price"));

        runner.test("Booking: zero total price allowed", () -> {
            Booking booking = new Booking("B", "S", "C", Arrays.asList("A1"), BigDecimal.ZERO, WHEN);
            assertBigDecimalEquals(BigDecimal.ZERO, booking.getTotalPrice(), "zero total ok");
        });

        runner.test("Booking: null bookedAt rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Booking("B", "S", "C", Arrays.asList("A1"), BigDecimal.ONE, null),
                        "null bookedAt"));

        runner.test("Booking: cancel changes status to CANCELLED", () -> {
            Booking booking = sample();
            booking.cancel();
            assertEquals(BookingStatus.CANCELLED, booking.getStatus(), "cancelled");
            assertTrue(booking.isCancelled(), "isCancelled");
        });

        runner.test("Booking: cancelling an already cancelled booking rejected", () -> {
            Booking booking = sample();
            booking.cancel();
            assertThrows(IllegalStateException.class, booking::cancel, "double cancel");
        });

        runner.test("Booking: returned seat list is unmodifiable", () -> {
            List<String> seats = sample().getSeatIds();
            assertThrows(UnsupportedOperationException.class,
                    () -> seats.add("Z9"), "unmodifiable seats");
        });

        runner.test("Booking: snapshot contains expected data", () -> {
            BookingSnapshot snapshot = sample().toSnapshot();
            assertEquals("B0001", snapshot.getBookingId(), "snapshot id");
            assertEquals("S001", snapshot.getShowtimeId(), "snapshot showtime");
            assertEquals("Alice", snapshot.getCustomerName(), "snapshot customer");
            assertEquals(Arrays.asList("A1", "A2"), snapshot.getSeatIds(), "snapshot seats");
            assertBigDecimalEquals(new BigDecimal("24.00"), snapshot.getTotalPrice(), "snapshot total");
            assertEquals(WHEN, snapshot.getBookedAt(), "snapshot bookedAt");
            assertEquals(BookingStatus.ACTIVE, snapshot.getStatus(), "snapshot status");
        });

        runner.test("Booking: snapshot seat list is unmodifiable", () -> {
            List<String> seats = sample().toSnapshot().getSeatIds();
            assertThrows(UnsupportedOperationException.class,
                    () -> seats.clear(), "unmodifiable snapshot seats");
        });
    }
}
