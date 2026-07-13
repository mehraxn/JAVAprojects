package parkinggaragesystem;

/** Immutable, read-only view of a {@link Vehicle}. */
public final class VehicleSnapshot {
    private final String licensePlate;
    private final VehicleType type;

    VehicleSnapshot(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }

    @Override
    public String toString() {
        return licensePlate + " (" + type + ")";
    }
}
