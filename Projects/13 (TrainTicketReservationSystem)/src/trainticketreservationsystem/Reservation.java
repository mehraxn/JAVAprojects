package trainticketreservationsystem;

public class Reservation {
    private final String id;
    private final String trainId;
    private final int seatNumber;
    private final String passengerName;

    public Reservation(String id, String trainId, int seatNumber, String passengerName) {
        this.id = id;
        this.trainId = trainId;
        this.seatNumber = seatNumber;
        this.passengerName = passengerName;
    }

    public String getId() { return id; }
    public String getPassengerName() { return passengerName; }
}
