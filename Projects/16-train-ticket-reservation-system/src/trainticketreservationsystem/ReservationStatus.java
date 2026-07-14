package trainticketreservationsystem;

/** Lifecycle state of a {@link Reservation}. */
public enum ReservationStatus {
    /** The reservation holds its seat. */
    ACTIVE,
    /** The reservation was cancelled; it stays in history but holds no seat. */
    CANCELLED
}
