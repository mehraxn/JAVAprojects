package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Garage {
    private final List<ParkingLevel> levels = new ArrayList<>();

    public void addLevel(ParkingLevel level) {
        // TODO: Validate and add a parking level.
        throw new UnsupportedOperationException("TODO: add a level");
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        // TODO: Find a compatible spot and record the arrival time.
        throw new UnsupportedOperationException("TODO: park a vehicle");
    }

    public Vehicle removeVehicle(String licensePlate) {
        // TODO: Locate and release the vehicle.
        throw new UnsupportedOperationException("TODO: remove a vehicle");
    }

    public BigDecimal calculateFee(LocalDateTime arrival, LocalDateTime departure) {
        // TODO: Validate the times and calculate a simple parking fee.
        throw new UnsupportedOperationException("TODO: calculate a fee");
    }
}
