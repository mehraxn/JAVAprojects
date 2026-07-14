package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One screening of a {@link Movie}: a seat map plus a ticket price.
 *
 * <p>Identity, movie, start time, ticket price, and the set of seats are fixed at
 * construction. Only seat availability changes, and only through
 * {@link BookingSystem}. Seat IDs must be unique within a showtime.
 *
 * <p>Money uses {@link BigDecimal}; the ticket price must be strictly greater
 * than zero. Outside callers receive an immutable {@link ShowtimeSnapshot}.
 */
public final class Showtime {
    private final String showtimeId;
    private final Movie movie;
    private final LocalDateTime startTime;
    private final BigDecimal ticketPrice;
    // Keyed by seat ID; preserves insertion order for a stable seat map.
    private final Map<String, Seat> seatsById = new LinkedHashMap<>();

    public Showtime(String showtimeId, Movie movie, LocalDateTime startTime,
                    BigDecimal ticketPrice, List<Seat> seats) {
        this.showtimeId = requireText(showtimeId, "Showtime ID");
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("Start time must not be null");
        }
        if (ticketPrice == null) {
            throw new IllegalArgumentException("Ticket price must not be null");
        }
        if (ticketPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ticket price must be greater than zero");
        }
        if (seats == null) {
            throw new IllegalArgumentException("Seat list must not be null");
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("Seat list must not be empty");
        }
        this.movie = movie;
        this.startTime = startTime;
        this.ticketPrice = ticketPrice;
        for (Seat seat : seats) {
            if (seat == null) {
                throw new IllegalArgumentException("Seat list must not contain null");
            }
            if (seatsById.containsKey(seat.getSeatId())) {
                throw new IllegalArgumentException("Duplicate seat ID: " + seat.getSeatId());
            }
            seatsById.put(seat.getSeatId(), seat);
        }
    }

    public String getShowtimeId() { return showtimeId; }
    public Movie getMovie() { return movie; }
    public LocalDateTime getStartTime() { return startTime; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public int getTotalSeats() { return seatsById.size(); }

    /** Live seat by ID, or {@code null} if this showtime has no such seat. */
    Seat findSeat(String seatId) {
        return seatsById.get(requireText(seatId, "Seat ID"));
    }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seatsById.values()) {
            if (seat.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public boolean isFull() {
        return getAvailableSeatCount() == 0;
    }

    /** Snapshots of every currently available seat, in seat-map order. */
    public List<SeatSnapshot> getAvailableSeatSnapshots() {
        List<SeatSnapshot> available = new ArrayList<>();
        for (Seat seat : seatsById.values()) {
            if (seat.isAvailable()) {
                available.add(seat.toSnapshot());
            }
        }
        return available;
    }

    /** Snapshots of every seat, in seat-map order. */
    public List<SeatSnapshot> getSeatSnapshots() {
        List<SeatSnapshot> all = new ArrayList<>();
        for (Seat seat : seatsById.values()) {
            all.add(seat.toSnapshot());
        }
        return all;
    }

    /** Immutable, read-only view of this showtime including its seat map. */
    public ShowtimeSnapshot toSnapshot() {
        return new ShowtimeSnapshot(showtimeId, movie.toSnapshot(), startTime, ticketPrice,
                getSeatSnapshots(), getTotalSeats(), getAvailableSeatCount());
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
