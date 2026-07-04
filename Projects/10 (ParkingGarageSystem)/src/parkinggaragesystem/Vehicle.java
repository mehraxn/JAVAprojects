package parkinggaragesystem;

import java.util.Locale;

public class Vehicle {
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

    @Override
    public String toString() {
        return licensePlate + " (" + type + ")";
    }
}
