package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single scheduled train service: a route plus its numbered seats.
 *
 * <p>Each {@code Train} instance represents one scheduled service. Recurring
 * schedules and travel dates are not modelled; reserved seats stay reserved on
 * that service until the reservation is cancelled.
 *
 * <p>Seats can be supplied at construction or added afterwards. Seat numbers must
 * be unique. Live {@code Seat} instances are only reachable within this package;
 * {@link ReservationSystem} exposes {@link SeatSnapshot}/{@link TrainSnapshot}
 * copies to outside callers.
 */
public final class Train {
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

    /** Convenience constructor that validates and stores an initial seat list. */
    public Train(String id, Route route, List<Seat> seats) {
        this(id, route);
        if (seats == null) {
            throw new IllegalArgumentException("Seat list must not be null");
        }
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("Seat list must not be empty");
        }
        for (Seat seat : seats) {
            addSeat(seat);
        }
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

    /** Returns the live seat with the given number, or throws if unknown. */
    Seat findSeat(int seatNumber) {
        for (Seat seat : seats) {
            if (seat.getNumber() == seatNumber) {
                return seat;
            }
        }
        throw new IllegalArgumentException("Unknown seat number " + seatNumber + " on train " + id);
    }

    /** Returns the first available live seat in seat order, or {@code null}. */
    Seat findFirstAvailableSeat() {
        for (Seat seat : seats) {
            if (!seat.isReserved()) {
                return seat;
            }
        }
        return null;
    }

    /** Live seats, package-private and read-only, in insertion order. */
    List<Seat> seats() {
        return Collections.unmodifiableList(seats);
    }

    public int getTotalSeatCount() {
        return seats.size();
    }

    public int getAvailableSeatCount() {
        int count = 0;
        for (Seat seat : seats) {
            if (!seat.isReserved()) {
                count++;
            }
        }
        return count;
    }

    public boolean isFull() {
        return getAvailableSeatCount() == 0;
    }

    @Override
    public String toString() {
        return "Train " + id + " on " + route
                + " [" + getAvailableSeatCount() + "/" + getTotalSeatCount() + " free]";
    }
}
