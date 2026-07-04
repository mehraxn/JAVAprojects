package trainticketreservationsystem;

public class Seat {
    private final int number;
    private boolean reserved;

    public Seat(int number) {
        this.number = number;
    }

    public int getNumber() { return number; }
    public boolean isReserved() { return reserved; }

    public void reserve() {
        // TODO: Prevent double reservation before changing the state.
        throw new UnsupportedOperationException("TODO: reserve a seat");
    }

    public void release() {
        // TODO: Validate and clear the reservation state.
        throw new UnsupportedOperationException("TODO: release a seat");
    }
}
