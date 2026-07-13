package movieticketbookingsystem;

/**
 * A single seat in a {@link Showtime} and its availability state.
 *
 * <p>Identity ({@code seatId}) is fixed at construction; only availability
 * changes, and only through the package-private {@link #reserve()} and
 * {@link #release()} methods driven by {@link Showtime}/{@link BookingSystem}.
 * Outside callers never receive a live {@code Seat}; they get an immutable
 * {@link SeatSnapshot} instead, so internal state cannot be corrupted.
 */
public final class Seat {
    private final String seatId;
    private boolean available = true;

    public Seat(String seatId) {
        this.seatId = requireText(seatId, "Seat ID");
    }

    public String getSeatId() { return seatId; }
    public boolean isAvailable() { return available; }

    /** Reserves an available seat; rejects a double reservation. */
    void reserve() {
        if (!available) {
            throw new IllegalStateException("Seat is already reserved: " + seatId);
        }
        available = false;
    }

    /** Releases a reserved seat; rejects releasing an available seat. */
    void release() {
        if (available) {
            throw new IllegalStateException("Seat is not reserved: " + seatId);
        }
        available = true;
    }

    /** Immutable, read-only view of this seat. */
    public SeatSnapshot toSnapshot() {
        return new SeatSnapshot(seatId, available);
    }

    @Override
    public String toString() {
        return seatId + (available ? " (available)" : " (reserved)");
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
