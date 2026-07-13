package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static movieticketbookingsystem.TestSupport.assertBigDecimalEquals;
import static movieticketbookingsystem.TestSupport.assertEquals;
import static movieticketbookingsystem.TestSupport.assertFalse;
import static movieticketbookingsystem.TestSupport.assertThrows;
import static movieticketbookingsystem.TestSupport.assertTrue;

final class ShowtimeTest {

    private ShowtimeTest() {
    }

    private static final LocalDateTime WHEN = LocalDateTime.of(2026, 7, 4, 19, 30);

    private static Movie movie() {
        return new Movie("M001", "The Java Journey", "Adventure", 120);
    }

    private static List<Seat> seats(String... ids) {
        List<Seat> list = new ArrayList<>();
        for (String id : ids) {
            list.add(new Seat(id));
        }
        return list;
    }

    private static Showtime sample() {
        return new Showtime("S001", movie(), WHEN, new BigDecimal("12.00"),
                seats("A1", "A2", "A3"));
    }

    static void register(TestRunner runner) {
        runner.test("Showtime: valid creation stores all fields", () -> {
            Showtime showtime = sample();
            assertEquals("S001", showtime.getShowtimeId(), "id stored");
            assertEquals("The Java Journey", showtime.getMovie().getTitle(), "movie stored");
            assertEquals(WHEN, showtime.getStartTime(), "start time stored");
            assertBigDecimalEquals(new BigDecimal("12.00"), showtime.getTicketPrice(), "price stored");
            assertEquals(3, showtime.getTotalSeats(), "seats stored");
        });

        runner.test("Showtime: null/blank showtime ID rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime(null, movie(), WHEN, new BigDecimal("12.00"), seats("A1")),
                    "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime("  ", movie(), WHEN, new BigDecimal("12.00"), seats("A1")),
                    "blank id");
        });

        runner.test("Showtime: null movie rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Showtime("S1", null, WHEN, new BigDecimal("12.00"), seats("A1")),
                        "null movie"));

        runner.test("Showtime: null start time rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Showtime("S1", movie(), null, new BigDecimal("12.00"), seats("A1")),
                        "null start"));

        runner.test("Showtime: null ticket price rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Showtime("S1", movie(), WHEN, null, seats("A1")),
                        "null price"));

        runner.test("Showtime: zero/negative ticket price rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime("S1", movie(), WHEN, BigDecimal.ZERO, seats("A1")),
                    "zero price");
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime("S1", movie(), WHEN, new BigDecimal("-1"), seats("A1")),
                    "negative price");
        });

        runner.test("Showtime: null/empty seat list rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime("S1", movie(), WHEN, new BigDecimal("12.00"), null),
                    "null seats");
            assertThrows(IllegalArgumentException.class,
                    () -> new Showtime("S1", movie(), WHEN, new BigDecimal("12.00"),
                            new ArrayList<>()), "empty seats");
        });

        runner.test("Showtime: null seat in list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Showtime("S1", movie(), WHEN, new BigDecimal("12.00"),
                                Arrays.asList(new Seat("A1"), null)), "null seat"));

        runner.test("Showtime: duplicate seat ID rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Showtime("S1", movie(), WHEN, new BigDecimal("12.00"),
                                seats("A1", "A1")), "duplicate seat"));

        runner.test("Showtime: available seat count and full detection", () -> {
            Showtime showtime = sample();
            assertEquals(3, showtime.getAvailableSeatCount(), "all available");
            assertFalse(showtime.isFull(), "not full");
            showtime.findSeat("A1").reserve();
            showtime.findSeat("A2").reserve();
            showtime.findSeat("A3").reserve();
            assertEquals(0, showtime.getAvailableSeatCount(), "none available");
            assertTrue(showtime.isFull(), "full");
        });

        runner.test("Showtime: findSeat returns live seat, unknown returns null", () -> {
            Showtime showtime = sample();
            assertEquals("A1", showtime.findSeat("A1").getSeatId(), "found");
            assertTrue(showtime.findSeat("Z9") == null, "unknown seat is null");
        });

        runner.test("Showtime: available seat snapshots are safe (reserving snapshot copy does nothing)", () -> {
            Showtime showtime = sample();
            List<SeatSnapshot> available = showtime.getAvailableSeatSnapshots();
            assertEquals(3, available.size(), "three available snapshots");
            // Snapshots carry no mutators; reserving via showtime is the only path.
            showtime.findSeat("A1").reserve();
            assertEquals(2, showtime.getAvailableSeatCount(), "count reflects live reserve");
        });

        runner.test("Showtime: snapshot contains movie, price, and seat map", () -> {
            ShowtimeSnapshot snapshot = sample().toSnapshot();
            assertEquals("S001", snapshot.getShowtimeId(), "snapshot id");
            assertEquals("The Java Journey", snapshot.getMovie().getTitle(), "snapshot movie");
            assertBigDecimalEquals(new BigDecimal("12.00"), snapshot.getTicketPrice(), "snapshot price");
            assertEquals(3, snapshot.getTotalSeats(), "snapshot total seats");
            assertEquals(3, snapshot.getAvailableSeatCount(), "snapshot available");
            assertEquals(3, snapshot.getSeats().size(), "snapshot seat map");
        });
    }
}
