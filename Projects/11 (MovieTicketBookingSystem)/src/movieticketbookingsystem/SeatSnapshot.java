package movieticketbookingsystem;

/**
 * Immutable, read-only view of a {@link Seat}. Holding one cannot change seat
 * availability inside a {@link Showtime}.
 */
public final class SeatSnapshot {
    private final String seatId;
    private final boolean available;

    SeatSnapshot(String seatId, boolean available) {
        this.seatId = seatId;
        this.available = available;
    }

    public String getSeatId() { return seatId; }
    public boolean isAvailable() { return available; }

    @Override
    public String toString() {
        return seatId + (available ? " (available)" : " (reserved)");
    }
}
