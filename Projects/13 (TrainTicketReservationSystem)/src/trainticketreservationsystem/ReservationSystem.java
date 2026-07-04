package trainticketreservationsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationSystem {
    private final Map<String, Train> trains = new HashMap<>();
    private final Map<String, Reservation> reservations = new HashMap<>();

    public void addTrain(Train train) {
        // TODO: Validate and store a unique train.
        throw new UnsupportedOperationException("TODO: add a train");
    }

    public List<Train> searchByRoute(String origin, String destination) {
        // TODO: Find trains matching the requested route.
        throw new UnsupportedOperationException("TODO: search trains");
    }

    public Reservation reserveSeat(String trainId, String passengerName) {
        // TODO: Reserve an available seat and create a reservation.
        throw new UnsupportedOperationException("TODO: reserve a train seat");
    }

    public void cancelReservation(String reservationId) {
        // TODO: Release the seat and remove the reservation.
        throw new UnsupportedOperationException("TODO: cancel a reservation");
    }
}
