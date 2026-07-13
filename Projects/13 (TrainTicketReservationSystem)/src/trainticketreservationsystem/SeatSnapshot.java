package trainticketreservationsystem;

/**
 * Immutable, read-only view of a {@link Seat}. Exposes the seat number and
 * whether it was available at snapshot time, with no way to mutate the live seat.
 */
public final class SeatSnapshot {
    private final int number;
    private final boolean available;

    SeatSnapshot(Seat seat) {
        this.number = seat.getNumber();
        this.available = seat.isAvailable();
    }

    public int getNumber() { return number; }
    public boolean isAvailable() { return available; }
    public boolean isReserved() { return !available; }

    @Override
    public String toString() {
        return "Seat " + number + (available ? " (available)" : " (reserved)");
    }
}
