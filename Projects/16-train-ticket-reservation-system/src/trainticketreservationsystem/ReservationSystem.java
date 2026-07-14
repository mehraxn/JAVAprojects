package trainticketreservationsystem;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * In-memory service layer that owns routes, trains, seats, and reservations.
 *
 * <p>This is the only class outside callers use to change state. Every query
 * method returns immutable snapshots ({@link RouteSnapshot}, {@link TrainSnapshot},
 * {@link SeatSnapshot}, {@link ReservationSnapshot}) so live domain objects are
 * never leaked and cannot be mutated from outside.
 *
 * <p>Reservation timestamps come from an injectable {@link Clock}, so tests can
 * pin time to a fixed instant.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>Route search is case-insensitive but direction-sensitive.</li>
 *   <li>A train may reference any {@link Route} whose id and stations match a
 *       registered route (value equality, not object identity).</li>
 *   <li>Cancellation releases the exact recorded seat and marks the reservation
 *       {@link ReservationStatus#CANCELLED}; it stays in history.</li>
 * </ul>
 */
public final class ReservationSystem {
    private final Map<String, Route> routes = new LinkedHashMap<>();
    private final Map<String, Train> trains = new LinkedHashMap<>();
    private final Map<String, Reservation> reservations = new LinkedHashMap<>();
    private final Clock clock;
    private int nextReservationNumber = 1;

    /** Creates a system that timestamps reservations from the system clock. */
    public ReservationSystem() {
        this(Clock.systemDefaultZone());
    }

    /** Creates a system that timestamps reservations from the given clock. */
    public ReservationSystem(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    // ---------------------------------------------------------------- routes

    public void addRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("Route must not be null");
        }
        if (routes.containsKey(route.getId())) {
            throw new IllegalArgumentException("Route ID already exists: " + route.getId());
        }
        for (Route existingRoute : routes.values()) {
            if (existingRoute.matches(route.getOrigin(), route.getDestination())) {
                throw new IllegalArgumentException("Route already exists from "
                        + route.getOrigin() + " to " + route.getDestination());
            }
        }
        routes.put(route.getId(), route);
    }

    public RouteSnapshot getRoute(String routeId) {
        return new RouteSnapshot(requireRoute(routeId));
    }

    public List<RouteSnapshot> listRoutes() {
        List<RouteSnapshot> views = new ArrayList<>();
        for (Route route : routes.values()) {
            views.add(new RouteSnapshot(route));
        }
        return Collections.unmodifiableList(views);
    }

    // ---------------------------------------------------------------- trains

    public void addTrain(Train train) {
        if (train == null) {
            throw new IllegalArgumentException("Train must not be null");
        }
        Route registeredRoute = requireRoute(train.getRoute().getId());
        if (!registeredRoute.equals(train.getRoute())) {
            throw new IllegalArgumentException("Train route " + train.getRoute().getId()
                    + " does not match the registered route with that ID");
        }
        if (trains.containsKey(train.getId())) {
            throw new IllegalArgumentException("Train ID already exists: " + train.getId());
        }
        if (train.getTotalSeatCount() == 0) {
            throw new IllegalArgumentException("Train must contain at least one seat");
        }
        trains.put(train.getId(), train);
    }

    public TrainSnapshot getTrain(String trainId) {
        return new TrainSnapshot(requireTrain(trainId));
    }

    public List<TrainSnapshot> listTrains() {
        List<TrainSnapshot> views = new ArrayList<>();
        for (Train train : trains.values()) {
            views.add(new TrainSnapshot(train));
        }
        return Collections.unmodifiableList(views);
    }

    public List<SeatSnapshot> getAvailableSeats(String trainId) {
        Train train = requireTrain(trainId);
        List<SeatSnapshot> views = new ArrayList<>();
        for (Seat seat : train.seats()) {
            if (seat.isAvailable()) {
                views.add(new SeatSnapshot(seat));
            }
        }
        return Collections.unmodifiableList(views);
    }

    public List<TrainSnapshot> searchByRoute(String origin, String destination) {
        String validOrigin = requireText(origin, "Origin");
        String validDestination = requireText(destination, "Destination");
        if (validOrigin.equalsIgnoreCase(validDestination)) {
            throw new IllegalArgumentException("Origin and destination must be different");
        }
        List<TrainSnapshot> matches = new ArrayList<>();
        for (Train train : trains.values()) {
            if (train.getRoute().matches(validOrigin, validDestination)) {
                matches.add(new TrainSnapshot(train));
            }
        }
        return Collections.unmodifiableList(matches);
    }

    // ---------------------------------------------------------- reservations

    /** Reserves the first available seat on the train, in seat order. */
    public ReservationSnapshot reserveFirstAvailableSeat(String trainId, String passengerName) {
        String validPassengerName = requireText(passengerName, "Passenger name");
        Train train = requireTrain(trainId);
        Seat seat = train.findFirstAvailableSeat();
        if (seat == null) {
            throw new IllegalStateException("No seats are available on train " + train.getId());
        }
        return createReservation(train, seat, validPassengerName);
    }

    /** Reserves a specific seat number on the train. */
    public ReservationSnapshot reserveSeat(String trainId, int seatNumber, String passengerName) {
        String validPassengerName = requireText(passengerName, "Passenger name");
        Train train = requireTrain(trainId);
        Seat seat = train.findSeat(seatNumber);
        if (seat.isReserved()) {
            throw new IllegalStateException("Seat is already reserved: " + seatNumber);
        }
        return createReservation(train, seat, validPassengerName);
    }

    private ReservationSnapshot createReservation(Train train, Seat seat, String passengerName) {
        // Validate name is already done by callers; reserve the seat last so a
        // failed reservation leaves seat state unchanged.
        seat.reserve();
        String reservationId = String.format("R%04d", nextReservationNumber++);
        Reservation reservation = new Reservation(
                reservationId, train.getId(), seat.getNumber(),
                passengerName, LocalDateTime.now(clock));
        reservations.put(reservationId, reservation);
        return new ReservationSnapshot(reservation);
    }

    /**
     * Cancels an active reservation and releases its exact seat.
     *
     * @throws IllegalArgumentException if the reservation ID is unknown
     * @throws IllegalStateException if the reservation is already cancelled
     */
    public void cancelReservation(String reservationId) {
        String validId = requireText(reservationId, "Reservation ID");
        Reservation reservation = reservations.get(validId);
        if (reservation == null) {
            throw new IllegalArgumentException("Unknown reservation ID: " + validId);
        }
        if (!reservation.isActive()) {
            throw new IllegalStateException("Reservation is already cancelled: " + validId);
        }
        Seat seat = requireTrain(reservation.getTrainId()).findSeat(reservation.getSeatNumber());
        if (!seat.isReserved()) {
            throw new IllegalStateException("Reservation state is inconsistent for seat "
                    + reservation.getSeatNumber());
        }
        reservation.cancel();
        seat.release();
    }

    public ReservationSnapshot findReservationById(String reservationId) {
        String validId = requireText(reservationId, "Reservation ID");
        Reservation reservation = reservations.get(validId);
        if (reservation == null) {
            throw new IllegalArgumentException("Unknown reservation ID: " + validId);
        }
        return new ReservationSnapshot(reservation);
    }

    /** All reservations (active and cancelled) in creation order. */
    public List<ReservationSnapshot> listReservations() {
        List<ReservationSnapshot> views = new ArrayList<>();
        for (Reservation reservation : reservations.values()) {
            views.add(new ReservationSnapshot(reservation));
        }
        return Collections.unmodifiableList(views);
    }

    /** Reservations for one train (active and cancelled) in creation order. */
    public List<ReservationSnapshot> listReservationsForTrain(String trainId) {
        Train train = requireTrain(trainId);
        List<ReservationSnapshot> views = new ArrayList<>();
        for (Reservation reservation : reservations.values()) {
            if (reservation.getTrainId().equals(train.getId())) {
                views.add(new ReservationSnapshot(reservation));
            }
        }
        return Collections.unmodifiableList(views);
    }

    // --------------------------------------------------------------- helpers

    private Route requireRoute(String routeId) {
        String validId = requireText(routeId, "Route ID");
        Route route = routes.get(validId);
        if (route == null) {
            throw new IllegalArgumentException("Unknown route ID: " + validId);
        }
        return route;
    }

    private Train requireTrain(String trainId) {
        String validId = requireText(trainId, "Train ID");
        Train train = trains.get(validId);
        if (train == null) {
            throw new IllegalArgumentException("Unknown train ID: " + validId);
        }
        return train;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
