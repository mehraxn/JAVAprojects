package movieticketbookingsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BookingSystem {
    public static final BigDecimal SEAT_PRICE = new BigDecimal("12.00");

    private final Map<String, Movie> movies = new LinkedHashMap<>();
    private final Map<String, Showtime> showtimes = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private int nextBookingNumber = 1;

    public void addMovie(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null");
        }
        if (movies.containsKey(movie.getId())) {
            throw new IllegalArgumentException("Movie ID already exists: " + movie.getId());
        }
        movies.put(movie.getId(), movie);
    }

    public Movie getMovie(String movieId) {
        String validId = requireText(movieId, "Movie ID");
        Movie movie = movies.get(validId);
        if (movie == null) {
            throw new IllegalArgumentException("Unknown movie ID: " + validId);
        }
        return movie;
    }

    public void addShowtime(Showtime showtime) {
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime must not be null");
        }
        Movie registeredMovie = getMovie(showtime.getMovie().getId());
        if (registeredMovie != showtime.getMovie()) {
            throw new IllegalArgumentException(
                    "Showtime must reference the registered Movie instance");
        }
        if (showtimes.containsKey(showtime.getId())) {
            throw new IllegalArgumentException("Showtime ID already exists: " + showtime.getId());
        }
        if (showtime.getSeatMap().isEmpty()) {
            throw new IllegalArgumentException("Showtime must contain at least one seat");
        }
        showtimes.put(showtime.getId(), showtime);
    }

    public Showtime getShowtime(String showtimeId) {
        String validId = requireText(showtimeId, "Showtime ID");
        Showtime showtime = showtimes.get(validId);
        if (showtime == null) {
            throw new IllegalArgumentException("Unknown showtime ID: " + validId);
        }
        return showtime;
    }

    public Booking bookSeats(String showtimeId, List<String> seatLabels) {
        Showtime showtime = getShowtime(showtimeId);
        if (seatLabels == null || seatLabels.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        Set<String> uniqueLabels = new LinkedHashSet<>();
        List<Seat> selectedSeats = new ArrayList<>();
        for (String label : seatLabels) {
            String validLabel = requireText(label, "Seat label");
            String normalizedLabel = validLabel.toUpperCase(Locale.ROOT);
            if (!uniqueLabels.add(normalizedLabel)) {
                throw new IllegalArgumentException("Duplicate seat in booking: " + validLabel);
            }
            Seat seat = showtime.findSeat(validLabel);
            if (seat.isBooked()) {
                throw new IllegalStateException("Seat is already booked: " + seat.getLabel());
            }
            selectedSeats.add(seat);
        }

        for (Seat seat : selectedSeats) {
            seat.reserve();
        }
        String bookingId = String.format("B%04d", nextBookingNumber++);
        List<String> storedLabels = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            storedLabels.add(seat.getLabel());
        }
        BigDecimal totalPrice = SEAT_PRICE.multiply(BigDecimal.valueOf(selectedSeats.size()));
        Booking booking = new Booking(bookingId, showtime.getId(), storedLabels, totalPrice);
        bookings.put(bookingId, booking);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        String validId = requireText(bookingId, "Booking ID");
        Booking booking = bookings.get(validId);
        if (booking == null) {
            throw new IllegalArgumentException("Unknown booking ID: " + validId);
        }
        Showtime showtime = getShowtime(booking.getShowtimeId());
        List<Seat> bookedSeats = new ArrayList<>();
        for (String label : booking.getSeatLabels()) {
            Seat seat = showtime.findSeat(label);
            if (!seat.isBooked()) {
                throw new IllegalStateException("Booking state is inconsistent for seat " + label);
            }
            bookedSeats.add(seat);
        }
        for (Seat seat : bookedSeats) {
            seat.cancelReservation();
        }
        bookings.remove(validId);
    }

    public List<Seat> getAvailableSeats(String showtimeId) {
        return getShowtime(showtimeId).getAvailableSeats();
    }

    public List<Movie> listMovies() {
        return Collections.unmodifiableList(new ArrayList<>(movies.values()));
    }

    public List<Showtime> listShowtimes() {
        return Collections.unmodifiableList(new ArrayList<>(showtimes.values()));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
