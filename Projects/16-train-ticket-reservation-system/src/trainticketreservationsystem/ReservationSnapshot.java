package trainticketreservationsystem;

import java.time.LocalDateTime;

/**
 * Immutable, read-only view of a {@link Reservation}, including its status and
 * timestamp. Holding one cannot change the underlying reservation or seat state.
 */
public final class ReservationSnapshot {
    private final String reservationId;
    private final String trainId;
    private final int seatNumber;
    private final String passengerName;
    private final ReservationStatus status;
    private final LocalDateTime reservedAt;

    ReservationSnapshot(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.trainId = reservation.getTrainId();
        this.seatNumber = reservation.getSeatNumber();
        this.passengerName = reservation.getPassengerName();
        this.status = reservation.getStatus();
        this.reservedAt = reservation.getReservedAt();
    }

    public String getReservationId() { return reservationId; }
    public String getTrainId() { return trainId; }
    public int getSeatNumber() { return seatNumber; }
    public String getPassengerName() { return passengerName; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public boolean isActive() { return status == ReservationStatus.ACTIVE; }

    @Override
    public String toString() {
        return reservationId + ": " + passengerName + " on " + trainId
                + " seat " + seatNumber + " [" + status + "]";
    }
}
