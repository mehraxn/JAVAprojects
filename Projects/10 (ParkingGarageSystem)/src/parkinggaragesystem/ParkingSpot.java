package parkinggaragesystem;

/**
 * One parking spot: an ID, the single {@link VehicleType} it supports, and its
 * occupied/free state.
 *
 * <p>ID and supported type are fixed at construction; only occupancy changes, and
 * only through the package-private {@link #assignVehicle(Vehicle)} and
 * {@link #releaseVehicle()} methods driven by {@link Garage}. Outside callers
 * never receive a live {@code ParkingSpot}; they get an immutable
 * {@link ParkingSpotSnapshot} instead, so garage state cannot be corrupted.
 */
public final class ParkingSpot {
    private final String id;
    private final VehicleType supportedType;
    private Vehicle parkedVehicle;

    public ParkingSpot(String id, VehicleType supportedType) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Parking spot ID must not be blank");
        }
        if (supportedType == null) {
            throw new IllegalArgumentException("Supported vehicle type must not be null");
        }
        this.id = id.trim();
        this.supportedType = supportedType;
    }

    public String getId() {
        return id;
    }

    public VehicleType getSupportedType() {
        return supportedType;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean supports(VehicleType vehicleType) {
        return supportedType == vehicleType;
    }

    /** License plate of the parked vehicle, or {@code null} if the spot is free. */
    String parkedLicensePlate() {
        return parkedVehicle == null ? null : parkedVehicle.getLicensePlate();
    }

    /** Parks a compatible vehicle in a free spot. */
    void assignVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null");
        }
        if (!isAvailable()) {
            throw new IllegalStateException("Parking spot is occupied: " + id);
        }
        if (!supports(vehicle.getType())) {
            throw new IllegalArgumentException("Spot " + id + " does not support " + vehicle.getType());
        }
        parkedVehicle = vehicle;
    }

    /** Frees an occupied spot and returns the vehicle that was parked. */
    Vehicle releaseVehicle() {
        if (isAvailable()) {
            throw new IllegalStateException("Parking spot is already empty: " + id);
        }
        Vehicle releasedVehicle = parkedVehicle;
        parkedVehicle = null;
        return releasedVehicle;
    }

    /** Immutable, read-only view of this spot at the current occupancy. */
    public ParkingSpotSnapshot toSnapshot() {
        return new ParkingSpotSnapshot(id, supportedType, isAvailable(), parkedLicensePlate());
    }

    @Override
    public String toString() {
        return id + " [" + supportedType + "] " + (isAvailable() ? "free" : "occupied");
    }
}
