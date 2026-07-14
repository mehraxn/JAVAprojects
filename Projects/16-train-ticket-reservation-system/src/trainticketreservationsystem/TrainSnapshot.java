package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable, read-only view of a {@link Train}, including a snapshot of every
 * seat. The seat list is unmodifiable and its elements are {@link SeatSnapshot}
 * copies, so callers cannot reach or mutate live seats.
 */
public final class TrainSnapshot {
    private final String trainId;
    private final RouteSnapshot route;
    private final List<SeatSnapshot> seats;
    private final int totalSeats;
    private final int availableSeats;

    TrainSnapshot(Train train) {
        this.trainId = train.getId();
        this.route = new RouteSnapshot(train.getRoute());
        List<SeatSnapshot> seatViews = new ArrayList<>();
        for (Seat seat : train.seats()) {
            seatViews.add(new SeatSnapshot(seat));
        }
        this.seats = Collections.unmodifiableList(seatViews);
        this.totalSeats = train.getTotalSeatCount();
        this.availableSeats = train.getAvailableSeatCount();
    }

    public String getTrainId() { return trainId; }
    public RouteSnapshot getRoute() { return route; }
    public List<SeatSnapshot> getSeats() { return seats; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public boolean isFull() { return availableSeats == 0; }

    @Override
    public String toString() {
        return "Train " + trainId + " on " + route
                + " [" + availableSeats + "/" + totalSeats + " free]";
    }
}
