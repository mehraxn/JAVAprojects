package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingReceipt {
    private final Vehicle vehicle;
    private final String spotId;
    private final LocalDateTime arrival;
    private final LocalDateTime departure;
    private final BigDecimal fee;

    public ParkingReceipt(Vehicle vehicle, String spotId, LocalDateTime arrival,
            LocalDateTime departure, BigDecimal fee) {
        this.vehicle = vehicle;
        this.spotId = spotId;
        this.arrival = arrival;
        this.departure = departure;
        this.fee = fee;
    }

    public Vehicle getVehicle() { return vehicle; }
    public String getSpotId() { return spotId; }
    public LocalDateTime getArrival() { return arrival; }
    public LocalDateTime getDeparture() { return departure; }
    public BigDecimal getFee() { return fee; }

    @Override
    public String toString() {
        return vehicle + " | spot " + spotId + " | fee " + fee.toPlainString();
    }
}
