package parkinggaragesystem;

import java.util.Locale;
import java.util.Objects;

/**
 * An immutable vehicle: a normalized license plate plus a {@link VehicleType}.
 *
 * <p>License plates are trimmed and upper-cased at construction, so
 * {@code " b-ab 123 "} and {@code "B-AB 123"} refer to the same vehicle.
 * Equality and hashing use the normalized plate, which is how {@link Garage}
 * detects duplicate active vehicles.
 */
public final class Vehicle {
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String licensePlate, VehicleType type) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        this.licensePlate = licensePlate.trim().toUpperCase(Locale.ROOT);
        this.type = type;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    /** Immutable, read-only view of this vehicle. */
    public VehicleSnapshot toSnapshot() {
        return new VehicleSnapshot(licensePlate, type);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Vehicle)) {
            return false;
        }
        Vehicle that = (Vehicle) other;
        return licensePlate.equals(that.licensePlate) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(licensePlate, type);
    }

    @Override
    public String toString() {
        return licensePlate + " (" + type + ")";
    }
}
