package trainticketreservationsystem;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * One passenger reservation for a specific seat on a specific train.
 *
 * <p>Every field except {@link #status} is fixed at creation. The status begins
 * {@link ReservationStatus#ACTIVE} and can move once to
 * {@link ReservationStatus#CANCELLED} via {@link #cancel()}; a cancelled
 * reservation stays in history but no longer holds its seat. The reservation
 * timestamp is supplied by {@link ReservationSystem} (from its clock) so it can
 * be made deterministic in tests.
 */
public final class Reservation {
    private final String id;
    private final String trainId;
    private final int seatNumber;
    private final String passengerName;
    private final LocalDateTime reservedAt;
    private ReservationStatus status;

    public Reservation(String id, String trainId, int seatNumber,
                       String passengerName, LocalDateTime reservedAt) {
        this.id = requireText(id, "Reservation ID");
        this.trainId = requireText(trainId, "Train ID");
        if (seatNumber <= 0) {
            throw new IllegalArgumentException("Seat number must be greater than zero");
        }
        this.seatNumber = seatNumber;
        this.passengerName = requireText(passengerName, "Passenger name");
        this.reservedAt = Objects.requireNonNull(reservedAt, "Reservation time must not be null");
        this.status = ReservationStatus.ACTIVE;
    }

    public String getId() { return id; }
    public String getTrainId() { return trainId; }
    public int getSeatNumber() { return seatNumber; }
    public String getPassengerName() { return passengerName; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public ReservationStatus getStatus() { return status; }

    public boolean isActive() {
        return status == ReservationStatus.ACTIVE;
    }

    /**
     * Marks this reservation cancelled.
     *
     * @throws IllegalStateException if it was already cancelled
     */
    void cancel() {
        if (status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled: " + id);
        }
        status = ReservationStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return id + ": " + passengerName + " on " + trainId
                + " seat " + seatNumber + " [" + status + "]";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
