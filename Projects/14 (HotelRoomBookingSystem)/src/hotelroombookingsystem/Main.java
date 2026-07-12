package hotelroombookingsystem;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args == null || out == null || err == null) throw new IllegalArgumentException("Arguments and streams must not be null");
        String command = args.length == 0 ? "help" : args[0];
        try {
            switch (command) {
                case "help": help(out); break;
                case "demo": demo(out); break;
                case "availability-demo": availabilityDemo(out); break;
                case "overlap-demo": overlapDemo(out); break;
                case "cancellation-demo": cancellationDemo(out); break;
                case "occupancy-demo": occupancyDemo(out); break;
                case "validation-demo": validationDemo(out); break;
                default: err.println("Unknown command: " + command); err.println("Run with 'help' to list commands."); return 2;
            }
            return 0;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            err.println("Command failed: " + exception.getMessage()); return 1;
        }
    }

    private static void help(PrintStream out) {
        out.println("Hotel Room Booking System commands:");
        out.println("  help, demo, availability-demo, overlap-demo");
        out.println("  cancellation-demo, occupancy-demo, validation-demo");
    }

    private static Hotel hotel() {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room("101", "Single", new BigDecimal("80.00"), 1));
        hotel.addRoom(new Room("201", "Double", new BigDecimal("125.00"), 2));
        return hotel;
    }

    private static void demo(PrintStream out) {
        Hotel hotel = hotel(); LocalDate in = LocalDate.of(2026, 8, 10); LocalDate checkout = in.plusDays(3);
        BookingSnapshot booking = hotel.bookRoom("201", new Guest("G1", "Nora"), in, checkout, 2);
        out.println("Booking " + booking.getId() + " total: " + money(booking.getTotalPrice()));
        out.println("Available rooms during stay: " + hotel.findAvailableRooms(in, checkout).size());
        out.println("Occupancy: " + percentage(hotel.calculateOccupancy(in)));
        hotel.cancelBooking(booking.getId());
        out.println("Available after cancellation: " + hotel.findAvailableRooms(in, checkout).size());
    }

    private static void availabilityDemo(PrintStream out) {
        Hotel hotel = hotel(); LocalDate in = LocalDate.of(2026, 8, 10); LocalDate checkout = in.plusDays(2);
        out.println("Before booking: " + hotel.findAvailableRooms(in, checkout).size());
        BookingSnapshot booking = hotel.bookRoom("101", new Guest("G1", "Nora"), in, checkout);
        out.println("After booking: " + hotel.findAvailableRooms(in, checkout).size());
        hotel.cancelBooking(booking.getId()); out.println("After cancellation: " + hotel.findAvailableRooms(in, checkout).size());
    }

    private static void overlapDemo(PrintStream out) {
        Hotel hotel = hotel(); Guest guest = new Guest("G1", "Nora");
        hotel.bookRoom("201", guest, LocalDate.of(2026,8,10), LocalDate.of(2026,8,13));
        expectFailure(() -> hotel.bookRoom("201", new Guest("G2","Omar"), LocalDate.of(2026,8,12), LocalDate.of(2026,8,15)), out);
        hotel.bookRoom("201", new Guest("G3","Lea"), LocalDate.of(2026,8,13), LocalDate.of(2026,8,15));
        out.println("Adjacent booking allowed: checkout is exclusive.");
    }

    private static void cancellationDemo(PrintStream out) {
        Hotel hotel = hotel(); LocalDate in = LocalDate.of(2026,8,10); LocalDate checkout = in.plusDays(2);
        BookingSnapshot booking = hotel.bookRoom("101", new Guest("G1","Nora"), in, checkout);
        out.println("Initial status: " + booking.getStatus()); hotel.cancelBooking(booking.getId());
        out.println("Cancelled status: " + hotel.findBookingById(booking.getId()).getStatus());
        out.println("Room available: " + hotel.findAvailableRooms(in, checkout).size());
        expectFailure(() -> hotel.cancelBooking(booking.getId()), out); expectFailure(() -> hotel.cancelBooking("missing"), out);
    }

    private static void occupancyDemo(PrintStream out) {
        Hotel hotel = hotel(); LocalDate in = LocalDate.of(2026,8,10); LocalDate checkout = in.plusDays(2);
        BookingSnapshot booking = hotel.bookRoom("101", new Guest("G1","Nora"), in, checkout);
        out.println("Booked date: " + percentage(hotel.calculateOccupancy(in)));
        out.println("Checkout date: " + percentage(hotel.calculateOccupancy(checkout)));
        hotel.cancelBooking(booking.getId()); out.println("After cancellation: " + percentage(hotel.calculateOccupancy(in)));
    }

    private static void validationDemo(PrintStream out) {
        expectFailure(() -> new Room(" ","Single",new BigDecimal("1"),1), out);
        expectFailure(() -> new Room("1","Single",BigDecimal.ZERO,1), out);
        expectFailure(() -> new Room("1","Single",new BigDecimal("-1"),1), out);
        Hotel hotel = hotel(); Guest guest = new Guest("G1","Nora");
        expectFailure(() -> hotel.bookRoom("101",guest,LocalDate.of(2026,8,10),LocalDate.of(2026,8,10)), out);
        hotel.bookRoom("101",guest,LocalDate.of(2026,8,10),LocalDate.of(2026,8,12));
        expectFailure(() -> hotel.bookRoom("101",new Guest("G2","Lea"),LocalDate.of(2026,8,11),LocalDate.of(2026,8,13)), out);
        expectFailure(() -> hotel.bookRoom("missing",guest,LocalDate.of(2026,8,10),LocalDate.of(2026,8,11)), out);
        expectFailure(() -> hotel.bookRoom("101",guest,LocalDate.of(2026,9,1),LocalDate.of(2026,9,2),2), out);
    }

    private static String money(BigDecimal value) { return value.setScale(2, RoundingMode.HALF_UP).toPlainString(); }
    private static String percentage(double value) { return String.format(java.util.Locale.ROOT, "%.1f%%", value); }
    private static void expectFailure(Runnable action, PrintStream out) {
        try { action.run(); out.println("Unexpected success"); }
        catch (IllegalArgumentException | IllegalStateException expected) { out.println("Expected rejection: " + expected.getMessage()); }
    }
}
