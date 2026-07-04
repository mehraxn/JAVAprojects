package movieticketbookingsystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Showtime {
    private final String id;
    private final Movie movie;
    private final LocalDateTime startsAt;
    private final List<Seat> seats = new ArrayList<>();

    public Showtime(String id, Movie movie, LocalDateTime startsAt) {
        this.id = id;
        this.movie = movie;
        this.startsAt = startsAt;
    }

    public String getId() { return id; }

    public Seat findSeat(String label) {
        // TODO: Find a seat by its displayed label.
        throw new UnsupportedOperationException("TODO: find a seat");
    }

    public List<Seat> getSeatMap() {
        // TODO: Return a safe view of the seats.
        throw new UnsupportedOperationException("TODO: return the seat map");
    }
}
