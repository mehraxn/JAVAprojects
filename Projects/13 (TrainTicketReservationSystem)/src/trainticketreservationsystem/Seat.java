package trainticketreservationsystem;

/**
 * A single numbered seat on a train and its availability state.
 *
 * <p>Seat numbers are positive integers. A seat starts available and toggles
 * between reserved and available through {@link #reserve()} and {@link #release()}.
 * Reserving a reserved seat or releasing an available seat is rejected so that
 * seat state can never drift silently out of sync with reservations.
 *
 * <p>{@code Seat} is mutable by design, so {@link ReservationSystem} never leaks
 * live {@code Seat} instances to callers; it exposes {@link SeatSnapshot} copies
 * instead.
 */
public final class Seat {
    private final int number;
    private boolean reserved;

    public Seat(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Seat number must be greater than zero");
        }
        this.number = number;
    }

    public int getNumber() { return number; }
    public boolean isReserved() { return reserved; }
    public boolean isAvailable() { return !reserved; }

    public void reserve() {
        if (reserved) {
            throw new IllegalStateException("Seat is already reserved: " + number);
        }
        reserved = true;
    }

    public void release() {
        if (!reserved) {
            throw new IllegalStateException("Seat is not reserved: " + number);
        }
        reserved = false;
    }

    @Override
    public String toString() {
        return "Seat " + number + (reserved ? " (reserved)" : " (available)");
    }
}
