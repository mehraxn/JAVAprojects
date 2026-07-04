package movieticketbookingsystem;

public class Seat {
    private final int row;
    private final int number;
    private boolean booked;

    public Seat(int row, int number) {
        if (row <= 0 || number <= 0) {
            throw new IllegalArgumentException("Seat row and number must be greater than zero");
        }
        this.row = row;
        this.number = number;
    }

    public int getRow() { return row; }
    public int getNumber() { return number; }
    public String getLabel() { return row + "-" + number; }
    public boolean isBooked() { return booked; }

    public void reserve() {
        if (booked) {
            throw new IllegalStateException("Seat is already booked: " + getLabel());
        }
        booked = true;
    }

    public void cancelReservation() {
        if (!booked) {
            throw new IllegalStateException("Seat is not booked: " + getLabel());
        }
        booked = false;
    }
}
