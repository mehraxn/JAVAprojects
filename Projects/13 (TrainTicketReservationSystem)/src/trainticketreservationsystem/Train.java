package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private final String id;
    private final Route route;
    private final List<Seat> seats = new ArrayList<>();

    public Train(String id, Route route) {
        this.id = id;
        this.route = route;
    }

    public String getId() { return id; }
    public Route getRoute() { return route; }

    public Seat findAvailableSeat() {
        // TODO: Return the first seat that is not reserved.
        throw new UnsupportedOperationException("TODO: find an available seat");
    }

    public int getAvailableSeatCount() {
        // TODO: Count seats that are not reserved.
        throw new UnsupportedOperationException("TODO: count available seats");
    }
}
