package movieticketbookingsystem;

/**
 * Lifecycle state of a {@link Booking}.
 *
 * <p>A booking starts {@link #ACTIVE} and can move to {@link #CANCELLED} exactly
 * once. Cancelled bookings stay in history but no longer hold any seats.
 */
public enum BookingStatus {
    ACTIVE,
    CANCELLED
}
