package trainticketreservationsystem;

public class Reservation {
    private final String id;
    private final String trainId;
    private final int seatNumber;
    private final String passengerName;

    public Reservation(String id, String trainId, int seatNumber, String passengerName) {
        this.id = requireText(id, "Reservation ID");
        this.trainId = requireText(trainId, "Train ID");
        if (seatNumber <= 0) {
            throw new IllegalArgumentException("Seat number must be greater than zero");
        }
        this.seatNumber = seatNumber;
        this.passengerName = requireText(passengerName, "Passenger name");
    }

    public String getId() { return id; }
    public String getTrainId() { return trainId; }
    public int getSeatNumber() { return seatNumber; }
    public String getPassengerName() { return passengerName; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
