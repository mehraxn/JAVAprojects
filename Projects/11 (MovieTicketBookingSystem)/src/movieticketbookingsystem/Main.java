package movieticketbookingsystem;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        BookingSystem bookingSystem = new BookingSystem();
        Movie movie = new Movie("M001", "The Java Journey", 120);
        bookingSystem.addMovie(movie);

        Showtime showtime = new Showtime("S001", movie,
                LocalDateTime.of(2026, 7, 4, 19, 30));
        for (int row = 1; row <= 2; row++) {
            for (int number = 1; number <= 3; number++) {
                showtime.addSeat(new Seat(row, number));
            }
        }
        bookingSystem.addShowtime(showtime);

        Booking booking = bookingSystem.bookSeats("S001", Arrays.asList("1-1", "1-2"));
        System.out.println("Created booking " + booking.getId()
                + " for " + booking.getSeatLabels() + ", total " + booking.getTotalPrice());
        System.out.println("Available seats after booking: "
                + bookingSystem.getAvailableSeats("S001").size());

        bookingSystem.cancelBooking(booking.getId());
        System.out.println("Available seats after cancellation: "
                + bookingSystem.getAvailableSeats("S001").size());
    }
}
