package parkinggaragesystem;

import java.time.LocalDateTime;

/**
 * Immutable, read-only view of a currently active parking session (a vehicle
 * still in the garage). Holding one cannot change garage state.
 */
public final class ActiveParkingSnapshot {
    private final String licensePlate;
    private final VehicleType vehicleType;
    private final String spotId;
    private final int levelNumber;
    private final LocalDateTime entryTime;

    ActiveParkingSnapshot(String licensePlate, VehicleType vehicleType,
                          String spotId, int levelNumber, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.levelNumber = levelNumber;
        this.entryTime = entryTime;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getSpotId() { return spotId; }
    public int getLevelNumber() { return levelNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override
    public String toString() {
        return licensePlate + " (" + vehicleType + ") at spot " + spotId
                + " L" + levelNumber + " since " + entryTime;
    }
}
