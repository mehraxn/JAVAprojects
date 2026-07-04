package parkinggaragesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingLevel {
    private final int number;
    private final List<ParkingSpot> spots = new ArrayList<>();

    public ParkingLevel(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Level number must not be negative");
        }
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void addSpot(ParkingSpot spot) {
        if (spot == null) {
            throw new IllegalArgumentException("Parking spot must not be null");
        }
        for (ParkingSpot existingSpot : spots) {
            if (existingSpot.getId().equals(spot.getId())) {
                throw new IllegalArgumentException("Parking spot ID already exists on level: "
                        + spot.getId());
            }
        }
        spots.add(spot);
    }

    public ParkingSpot findAvailableSpot(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.supports(type)) {
                return spot;
            }
        }
        return null;
    }

    public int getOccupiedSpotCount() {
        return spots.size() - getAvailableSpotCount();
    }

    public int getAvailableSpotCount() {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getAvailableSpotCount(VehicleType type) {
        int count = 0;
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.supports(type)) {
                count++;
            }
        }
        return count;
    }

    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(new ArrayList<>(spots));
    }
}
