package parkinggaragesystem;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Garage garage = new Garage();
        ParkingLevel groundFloor = new ParkingLevel(0);
        groundFloor.addSpot(new ParkingSpot("M-01", VehicleType.MOTORCYCLE));
        groundFloor.addSpot(new ParkingSpot("C-01", VehicleType.CAR));
        groundFloor.addSpot(new ParkingSpot("C-02", VehicleType.CAR));
        groundFloor.addSpot(new ParkingSpot("T-01", VehicleType.TRUCK));
        garage.addLevel(groundFloor);

        LocalDateTime arrival = LocalDateTime.of(2026, 7, 4, 10, 0);
        ParkingSpot assignedSpot = garage.parkVehicle(
                new Vehicle("B-AB 123", VehicleType.CAR), arrival);
        System.out.println("Assigned spot: " + assignedSpot.getId());
        System.out.println("Available car spots: "
                + garage.getAvailableSpotCount(VehicleType.CAR));

        ParkingReceipt receipt = garage.exitVehicle(
                "B-AB 123", arrival.plusHours(1).plusMinutes(30));
        System.out.println("Exit receipt: " + receipt);
        System.out.println("Available spots after exit: " + garage.getAvailableSpotCount());
    }
}
