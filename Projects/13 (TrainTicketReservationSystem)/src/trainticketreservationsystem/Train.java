package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Train {
    private final String id;
    private final Route route;
    private final List<Seat> seats = new ArrayList<>();

    public Train(String id, Route route) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Train ID must not be blank");
        }
        if (route == null) {
            throw new IllegalArgumentException("Route must not be null");
        }
        this.id = id.trim();
        this.route = route;
    }

    public String getId() { return id; }
    public Route getRoute() { return route; }

    public void addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("Seat must not be null");
        }
        for (Seat existingSeat : seats) {
            if (existingSeat.getNumber() == seat.getNumber()) {
                throw new IllegalArgumentException("Seat number already exists: " + seat.getNumber());
            }
        }
        seats.add(seat);
    }

    public Seat findSeat(int seatNumber) {
        for (Seat seat : seats) {
            if (seat.getNumber() == seatNumber) {
                return seat;
            }
        }
        throw new IllegalArgumentException("Unknown seat number " + seatNumber + " on train " + id);
    }

    public Seat findAvailableSeat() {
        for (Seat seat : seats) {
            if (!seat.isReserved()) {
                return seat;
            }
        }
        return null;
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seats) {
            if (!seat.isReserved()) {
                availableSeats.add(seat);
            }
        }
        return Collections.unmodifiableList(availableSeats);
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(new ArrayList<>(seats));
    }

    public int getAvailableSeatCount() {
        return getAvailableSeats().size();
    }
}
