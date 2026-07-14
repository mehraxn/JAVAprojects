package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Immutable, read-only view of a {@link Showtime}, including a snapshot seat map.
 *
 * <p>The seat list is unmodifiable and every element is itself an immutable
 * {@link SeatSnapshot}, so callers can inspect the seat map but never mutate the
 * live showtime.
 */
public final class ShowtimeSnapshot {
    private final String showtimeId;
    private final MovieSnapshot movie;
    private final LocalDateTime startTime;
    private final BigDecimal ticketPrice;
    private final List<SeatSnapshot> seats;
    private final int totalSeats;
    private final int availableSeatCount;

    ShowtimeSnapshot(String showtimeId, MovieSnapshot movie, LocalDateTime startTime,
                     BigDecimal ticketPrice, List<SeatSnapshot> seats,
                     int totalSeats, int availableSeatCount) {
        this.showtimeId = showtimeId;
        this.movie = movie;
        this.startTime = startTime;
        this.ticketPrice = ticketPrice;
        this.seats = Collections.unmodifiableList(seats);
        this.totalSeats = totalSeats;
        this.availableSeatCount = availableSeatCount;
    }

    public String getShowtimeId() { return showtimeId; }
    public MovieSnapshot getMovie() { return movie; }
    public LocalDateTime getStartTime() { return startTime; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public List<SeatSnapshot> getSeats() { return seats; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeatCount() { return availableSeatCount; }

    public boolean isFull() { return availableSeatCount == 0; }

    @Override
    public String toString() {
        return showtimeId + " " + movie.getTitle() + " @ " + startTime
                + " price " + ticketPrice + " (" + availableSeatCount + "/" + totalSeats + " free)";
    }
}
