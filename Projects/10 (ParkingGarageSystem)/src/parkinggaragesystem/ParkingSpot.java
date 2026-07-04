package parkinggaragesystem;

public class ParkingSpot {
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

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public boolean supports(VehicleType vehicleType) {
        return supportedType == vehicleType;
    }

    public void assignVehicle(Vehicle vehicle) {
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

    public Vehicle releaseVehicle() {
        if (isAvailable()) {
            throw new IllegalStateException("Parking spot is already empty: " + id);
        }
        Vehicle releasedVehicle = parkedVehicle;
        parkedVehicle = null;
        return releasedVehicle;
    }
}
