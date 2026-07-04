package parkinggaragesystem;

import java.util.ArrayList;
import java.util.List;

public class ParkingLevel {
    private final int number;
    private final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingLevel(int number) {
        this.number = number;
    }

    public void addSpot(ParkingSpot spot) {
        // TODO: Validate and add a uniquely identified parking spot.
        throw new UnsupportedOperationException("TODO: add a parking spot");
    }

    public ParkingSpot findAvailableSpot(VehicleType type) {
        // TODO: Find the first free spot compatible with the vehicle type.
        throw new UnsupportedOperationException("TODO: find a parking spot");
    }

    public int getOccupiedSpotCount() {
        // TODO: Count spots that currently hold a vehicle.
        throw new UnsupportedOperationException("TODO: count occupied spots");
    }
}
