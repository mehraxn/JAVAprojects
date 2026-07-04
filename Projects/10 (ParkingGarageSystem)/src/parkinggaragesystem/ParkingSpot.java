package parkinggaragesystem;

public class ParkingSpot {
    private final String id;
    private final VehicleType supportedType;
    private Vehicle parkedVehicle;

    public ParkingSpot(String id, VehicleType supportedType) {
        this.id = id;
        this.supportedType = supportedType;
    }

    public String getId() {
        return id;
    }

    public boolean isAvailable() {
        return parkedVehicle == null;
    }

    public void assignVehicle(Vehicle vehicle) {
        // TODO: Check compatibility and availability before assigning the vehicle.
        throw new UnsupportedOperationException("TODO: assign a vehicle");
    }

    public Vehicle releaseVehicle() {
        // TODO: Clear and return the currently parked vehicle.
        throw new UnsupportedOperationException("TODO: release a vehicle");
    }
}
