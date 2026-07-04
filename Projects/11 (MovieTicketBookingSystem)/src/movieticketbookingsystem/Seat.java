package movieticketbookingsystem;

public class Seat {
    private final int row;
    private final int number;
    private boolean booked;

    public Seat(int row, int number) {
        this.row = row;
        this.number = number;
    }

    public String getLabel() { return row + "-" + number; }
    public boolean isBooked() { return booked; }

    public void reserve() {
        // TODO: Prevent double booking before changing the seat state.
        throw new UnsupportedOperationException("TODO: reserve a seat");
    }

    public void cancelReservation() {
        // TODO: Validate and clear the booking state.
        throw new UnsupportedOperationException("TODO: cancel a seat reservation");
    }
}
