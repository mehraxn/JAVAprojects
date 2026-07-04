package movieticketbookingsystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Showtime {
    private final String id;
    private final Movie movie;
    private final LocalDateTime startsAt;
    private final List<Seat> seats = new ArrayList<>();

    public Showtime(String id, Movie movie, LocalDateTime startsAt) {
        this.id = requireText(id, "Showtime ID");
        if (movie == null || startsAt == null) {
            throw new IllegalArgumentException("Movie and start time must not be null");
        }
        this.movie = movie;
        this.startsAt = startsAt;
    }

    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public LocalDateTime getStartsAt() { return startsAt; }

    public void addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat must not be null");
        }
        for (Seat existingSeat : seats) {
            if (existingSeat.getLabel().equals(seat.getLabel())) {
                throw new IllegalArgumentException("Seat label already exists: " + seat.getLabel());
            }
        }
        seats.add(seat);
    }

    public Seat findSeat(String label) {
        String validLabel = requireText(label, "Seat label");
        for (Seat seat : seats) {
            if (seat.getLabel().equalsIgnoreCase(validLabel)) {
                return seat;
            }
        }
        throw new IllegalArgumentException("Unknown seat label: " + validLabel);
    }

    public List<Seat> getSeatMap() {
        return Collections.unmodifiableList(new ArrayList<>(seats));
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                availableSeats.add(seat);
            }
        }
        return Collections.unmodifiableList(availableSeats);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
