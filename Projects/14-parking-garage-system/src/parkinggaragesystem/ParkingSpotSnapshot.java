package parkinggaragesystem;

/**
 * Immutable, read-only view of a {@link ParkingSpot}. Holding one cannot change
 * occupancy inside the garage.
 */
public final class ParkingSpotSnapshot {
    private final String spotId;
    private final VehicleType supportedType;
    private final boolean available;
    private final String parkedLicensePlate;

    ParkingSpotSnapshot(String spotId, VehicleType supportedType,
                        boolean available, String parkedLicensePlate) {
        this.spotId = spotId;
        this.supportedType = supportedType;
        this.available = available;
        this.parkedLicensePlate = parkedLicensePlate;
    }

    public String getSpotId() { return spotId; }
    public VehicleType getSupportedType() { return supportedType; }
    public boolean isAvailable() { return available; }

    /** License plate parked here, or {@code null} if the spot is free. */
    public String getParkedLicensePlate() { return parkedLicensePlate; }

    @Override
    public String toString() {
        return spotId + " [" + supportedType + "] "
                + (available ? "free" : "occupied by " + parkedLicensePlate);
    }
}
