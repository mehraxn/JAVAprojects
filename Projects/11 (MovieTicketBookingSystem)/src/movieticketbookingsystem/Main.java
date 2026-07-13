package movieticketbookingsystem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command-line entry point and demo driver.
 *
 * <p>All work happens in {@link #run(String[], PrintStream, PrintStream)}, which
 * returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    // Fixed clock so demo booking timestamps are stable and reproducible.
    private static final Clock DEMO_CLOCK =
            Clock.fixed(Instant.parse("2026-01-15T18:30:00Z"), ZoneId.of("UTC"));

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "booking-demo":
                runBookingDemo(out);
                return 0;
            case "cancellation-demo":
                runCancellationDemo(out);
                return 0;
            case "full-showtime-demo":
                runFullShowtimeDemo(out);
                return 0;
            case "availability-demo":
                runAvailabilityDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Movie Ticket Booking System");
        out.println();
        out.println("Usage: java -cp out movieticketbookingsystem.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help                Show this help text.");
        out.println("  demo                End-to-end booking and cancellation walkthrough.");
        out.println("  booking-demo        Single/multi-seat booking and rejection rules.");
        out.println("  cancellation-demo   Cancel a booking and rebook the released seats.");
        out.println("  full-showtime-demo  Book every seat, then reject a further booking.");
        out.println("  availability-demo   Available seats before/after booking and cancelling.");
        out.println("  validation-demo     Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------- demos

    /** Builds a small sample system with one showtime of 6 seats (A1-A3, B1-B3). */
    private static BookingSystem sampleSystem() {
        BookingSystem system = new BookingSystem(DEMO_CLOCK);
        Movie movie = new Movie("M001", "The Java Journey", "Adventure", 120);
        List<Seat> seats = new ArrayList<>();
        for (String row : new String[] {"A", "B"}) {
            for (int number = 1; number <= 3; number++) {
                seats.add(new Seat(row + number));
            }
        }
        Showtime showtime = new Showtime("S001", movie,
                LocalDateTime.of(2026, 7, 4, 19, 30), new BigDecimal("12.00"), seats);
        system.addShowtime(showtime);
        return system;
    }

    private static void runDemo(PrintStream out) {
        out.println("== Movie Ticket Booking Demo ==");
        BookingSystem system = sampleSystem();
        ShowtimeSnapshot showtime = system.getShowtime("S001").orElseThrow();

        out.println();
        out.println("Movie:    " + showtime.getMovie().getTitle()
                + " (" + showtime.getMovie().getGenre() + ", "
                + showtime.getMovie().getDurationMinutes() + " min)");
        out.println("Showtime: " + showtime.getShowtimeId() + " @ " + showtime.getStartTime());
        out.println("Price:    " + formatMoney(showtime.getTicketPrice()) + " per seat");
        out.println("Seats:    " + showtime.getTotalSeats() + " total, "
                + showtime.getAvailableSeatCount() + " available");

        out.println();
        out.println("Booking seats A1, A2, B1 for Alice...");
        BookingSnapshot booking = system.bookSeats("S001", "Alice", Arrays.asList("A1", "A2", "B1"));
        out.println("  Booking ID:   " + booking.getBookingId());
        out.println("  Seats:        " + booking.getSeatIds());
        out.println("  Total price:  " + formatMoney(booking.getTotalPrice()));
        out.println("  Booked at:    " + booking.getBookedAt());
        out.println("  Status:       " + booking.getStatus());
        out.println("  Available after booking: " + system.getAvailableSeats("S001").size());

        out.println();
        out.println("Cancelling booking " + booking.getBookingId() + "...");
        system.cancelBooking(booking.getBookingId());
        out.println("  Available after cancellation: " + system.getAvailableSeats("S001").size());
        out.println("  Booking status now: "
                + system.findBookingById(booking.getBookingId()).orElseThrow().getStatus());
    }

    private static void runBookingDemo(PrintStream out) {
        out.println("== Booking Demo ==");
        BookingSystem system = sampleSystem();

        BookingSnapshot single = system.bookSeats("S001", "Bob", Arrays.asList("A1"));
        out.println("Single-seat booking " + single.getBookingId()
                + " seats " + single.getSeatIds()
                + " total " + formatMoney(single.getTotalPrice()));

        BookingSnapshot multi = system.bookSeats("S001", "Carol", Arrays.asList("A2", "A3"));
        out.println("Multi-seat booking " + multi.getBookingId()
                + " seats " + multi.getSeatIds()
                + " total " + formatMoney(multi.getTotalPrice()));

        out.println();
        expectFailure(out, "duplicate seat in one request",
                () -> system.bookSeats("S001", "Dave", Arrays.asList("B1", "B1")));
        expectFailure(out, "already-booked seat",
                () -> system.bookSeats("S001", "Dave", Arrays.asList("A1")));

        out.println();
        out.println("All-or-nothing: booking [B2, A1(taken)] should reserve nothing...");
        int availableBefore = system.getAvailableSeats("S001").size();
        expectFailure(out, "mixed valid + taken seat",
                () -> system.bookSeats("S001", "Dave", Arrays.asList("B2", "A1")));
        int availableAfter = system.getAvailableSeats("S001").size();
        out.println("  Available seats unchanged: " + availableBefore + " -> " + availableAfter);
    }

    private static void runCancellationDemo(PrintStream out) {
        out.println("== Cancellation Demo ==");
        BookingSystem system = sampleSystem();

        BookingSnapshot booking = system.bookSeats("S001", "Erin", Arrays.asList("A1", "A2"));
        out.println("Booked " + booking.getBookingId() + " seats " + booking.getSeatIds());
        out.println("Available after booking: " + system.getAvailableSeats("S001").size());

        out.println();
        out.println("Cancelling " + booking.getBookingId() + "...");
        BookingSnapshot cancelled = system.cancelBooking(booking.getBookingId());
        out.println("  Released exactly: " + cancelled.getSeatIds());
        out.println("  Status: " + cancelled.getStatus());
        out.println("  Available after cancellation: " + system.getAvailableSeats("S001").size());

        out.println();
        out.println("Rebooking released seat A1 for Frank...");
        BookingSnapshot rebooked = system.bookSeats("S001", "Frank", Arrays.asList("A1"));
        out.println("  New booking " + rebooked.getBookingId() + " seats " + rebooked.getSeatIds());

        out.println();
        expectFailure(out, "cancel missing booking",
                () -> system.cancelBooking("B9999"));
        expectFailure(out, "cancel already-cancelled booking",
                () -> system.cancelBooking(booking.getBookingId()));

        out.println();
        out.println("Booking history (cancelled bookings are kept):");
        for (BookingSnapshot record : system.listBookings()) {
            out.println("  " + record.getBookingId() + " " + record.getSeatIds()
                    + " " + record.getStatus());
        }
    }

    private static void runFullShowtimeDemo(PrintStream out) {
        out.println("== Full Showtime Demo ==");
        BookingSystem system = sampleSystem();

        out.println("Booking every seat (A1..B3)...");
        system.bookSeats("S001", "Grace", Arrays.asList("A1", "A2", "A3", "B1", "B2", "B3"));
        ShowtimeSnapshot showtime = system.getShowtime("S001").orElseThrow();
        out.println("  Available seats: " + showtime.getAvailableSeatCount()
                + " (full=" + showtime.isFull() + ")");

        out.println();
        int bookingsBefore = system.listBookings().size();
        expectFailure(out, "booking into a full showtime",
                () -> system.bookSeats("S001", "Heidi", Arrays.asList("A1")));
        int bookingsAfter = system.listBookings().size();
        out.println("  Booking count unchanged: " + bookingsBefore + " -> " + bookingsAfter);
    }

    private static void runAvailabilityDemo(PrintStream out) {
        out.println("== Availability Demo ==");
        BookingSystem system = sampleSystem();

        out.println("Available before booking: " + seatIds(system.getAvailableSeats("S001")));
        BookingSnapshot booking = system.bookSeats("S001", "Ivan", Arrays.asList("A1", "B3"));
        out.println("Booked " + booking.getSeatIds());
        out.println("Available after booking:  " + seatIds(system.getAvailableSeats("S001")));
        system.cancelBooking(booking.getBookingId());
        out.println("Available after cancel:   " + seatIds(system.getAvailableSeats("S001")));
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        BookingSystem system = sampleSystem();

        expectFailure(out, "blank movie title",
                () -> new Movie("M9", "  ", "Drama", 100));
        expectFailure(out, "zero movie duration",
                () -> new Movie("M9", "Title", "Drama", 0));
        expectFailure(out, "blank showtime ID",
                () -> new Showtime("  ", new Movie("M9", "T", 90),
                        LocalDateTime.now(), new BigDecimal("10.00"),
                        Arrays.asList(new Seat("A1"))));
        expectFailure(out, "zero ticket price",
                () -> new Showtime("S9", new Movie("M9", "T", 90),
                        LocalDateTime.now(), BigDecimal.ZERO,
                        Arrays.asList(new Seat("A1"))));
        expectFailure(out, "negative ticket price",
                () -> new Showtime("S9", new Movie("M9", "T", 90),
                        LocalDateTime.now(), new BigDecimal("-1.00"),
                        Arrays.asList(new Seat("A1"))));
        expectFailure(out, "duplicate seat ID in showtime",
                () -> new Showtime("S9", new Movie("M9", "T", 90),
                        LocalDateTime.now(), new BigDecimal("10.00"),
                        Arrays.asList(new Seat("A1"), new Seat("A1"))));
        expectFailure(out, "blank customer name",
                () -> system.bookSeats("S001", "  ", Arrays.asList("A1")));
        expectFailure(out, "unknown showtime",
                () -> system.bookSeats("NOPE", "Judy", Arrays.asList("A1")));
        expectFailure(out, "unknown seat",
                () -> system.bookSeats("S001", "Judy", Arrays.asList("Z9")));
        expectFailure(out, "duplicate seat selection",
                () -> system.bookSeats("S001", "Judy", Arrays.asList("A1", "A1")));

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // ----------------------------------------------------------------- helpers

    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String seatIds(List<SeatSnapshot> seats) {
        if (seats.isEmpty()) {
            return "(none)";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(seats.get(i).getSeatId());
        }
        return builder.toString();
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
