package trainticketreservationsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReservationSystem {
    private final Map<String, Route> routes = new LinkedHashMap<>();
    private final Map<String, Train> trains = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();
    private int nextReservationNumber = 1;

    public void addRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("Route must not be null");
        }
        if (routes.containsKey(route.getId())) {
            throw new IllegalArgumentException("Route ID already exists: " + route.getId());
        }
        routes.put(route.getId(), route);
    }

    public Route getRoute(String routeId) {
        String validId = requireText(routeId, "Route ID");
        Route route = routes.get(validId);
        if (route == null) {
            throw new IllegalArgumentException("Unknown route ID: " + validId);
        }
        return route;
    }

    public void addTrain(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("Train must not be null");
        }
        getRoute(train.getRoute().getId());
        if (trains.containsKey(train.getId())) {
            throw new IllegalArgumentException("Train ID already exists: " + train.getId());
        }
        if (train.getSeats().isEmpty()) {
            throw new IllegalArgumentException("Train must contain at least one seat");
        }
        trains.put(train.getId(), train);
    }

    public Train getTrain(String trainId) {
        String validId = requireText(trainId, "Train ID");
        Train train = trains.get(validId);
        if (train == null) {
            throw new IllegalArgumentException("Unknown train ID: " + validId);
        }
        return train;
    }

    public List<Train> searchByRoute(String origin, String destination) {
        List<Train> matchingTrains = new ArrayList<>();
        for (Train train : trains.values()) {
            if (train.getRoute().matches(origin, destination)) {
                matchingTrains.add(train);
            }
        }
        return Collections.unmodifiableList(matchingTrains);
    }

    public Reservation reserveSeat(String trainId, String passengerName) {
        Train train = getTrain(trainId);
        Seat seat = train.findAvailableSeat();
        if (seat == null) {
            throw new IllegalStateException("No seats are available on train " + train.getId());
        }
        return reserveSeat(trainId, seat.getNumber(), passengerName);
    }

    public Reservation reserveSeat(String trainId, int seatNumber, String passengerName) {
        String validPassengerName = requireText(passengerName, "Passenger name");
        Train train = getTrain(trainId);
        Seat seat = train.findSeat(seatNumber);
        if (seat.isReserved()) {
            throw new IllegalStateException("Seat is already reserved: " + seatNumber);
        }

        seat.reserve();
        String reservationId = String.format("R%04d", nextReservationNumber++);
        Reservation reservation = new Reservation(
                reservationId, train.getId(), seat.getNumber(), validPassengerName);
        reservations.put(reservationId, reservation);
        return reservation;
    }

    public void cancelReservation(String reservationId) {
        String validId = requireText(reservationId, "Reservation ID");
        Reservation reservation = reservations.get(validId);
        if (reservation == null) {
            throw new IllegalArgumentException("Unknown reservation ID: " + validId);
        }
        Seat seat = getTrain(reservation.getTrainId()).findSeat(reservation.getSeatNumber());
        if (!seat.isReserved()) {
            throw new IllegalStateException("Reservation state is inconsistent for seat "
                    + reservation.getSeatNumber());
        }
        seat.release();
        reservations.remove(validId);
    }

    public List<Seat> getAvailableSeats(String trainId) {
        return getTrain(trainId).getAvailableSeats();
    }

    public List<Reservation> listReservationsForTrain(String trainId) {
        Train train = getTrain(trainId);
        List<Reservation> trainReservations = new ArrayList<>();
        for (Reservation reservation : reservations.values()) {
            if (reservation.getTrainId().equals(train.getId())) {
                trainReservations.add(reservation);
            }
        }
        return Collections.unmodifiableList(trainReservations);
    }

    public List<Route> listRoutes() {
        return Collections.unmodifiableList(new ArrayList<>(routes.values()));
    }

    public List<Train> listTrains() {
        return Collections.unmodifiableList(new ArrayList<>(trains.values()));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
