package movieticketbookingsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingSystem {
    private final Map<String, Showtime> showtimes = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    public void addShowtime(Showtime showtime) {
        // TODO: Validate and store a unique showtime.
        throw new UnsupportedOperationException("TODO: add a showtime");
    }

    public Booking bookSeats(String showtimeId, List<String> seatLabels) {
        // TODO: Validate seats, prevent double booking, and calculate the price.
        throw new UnsupportedOperationException("TODO: book seats");
    }

    public void cancelBooking(String bookingId) {
        // TODO: Release the seats and remove the booking.
        throw new UnsupportedOperationException("TODO: cancel a booking");
    }
}
