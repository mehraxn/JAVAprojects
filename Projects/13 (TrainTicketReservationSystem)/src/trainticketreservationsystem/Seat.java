package trainticketreservationsystem;

public class Seat {
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
}
