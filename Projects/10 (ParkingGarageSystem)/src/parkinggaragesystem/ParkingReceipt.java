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
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null");
        }
        if (spotId == null || spotId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking spot ID must not be blank");
        }
        if (arrival == null || departure == null) {
            throw new IllegalArgumentException("Arrival and departure times must not be null");
        }
        if (departure.isBefore(arrival)) {
            throw new IllegalArgumentException("Departure must not be before arrival");
        }
        if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parking fee must not be negative");
        }
        this.vehicle = vehicle;
        this.spotId = spotId.trim();
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
