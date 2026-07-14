package parkinggaragesystem;

import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertFalse;
import static parkinggaragesystem.TestSupport.assertNull;
import static parkinggaragesystem.TestSupport.assertThrows;
import static parkinggaragesystem.TestSupport.assertTrue;

final class ParkingSpotTest {

    private ParkingSpotTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Spot: valid creation stores id and supported type, starts available", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            assertEquals("C-01", spot.getId(), "id stored");
            assertEquals(VehicleType.CAR, spot.getSupportedType(), "type stored");
            assertTrue(spot.isAvailable(), "starts available");
        });

        runner.test("Spot: null/blank id rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingSpot(null, VehicleType.CAR), "null id");
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingSpot("  ", VehicleType.CAR), "blank id");
        });

        runner.test("Spot: null supported type rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingSpot("C-01", null), "null type"));

        runner.test("Spot: supports only its own type", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            assertTrue(spot.supports(VehicleType.CAR), "supports car");
            assertFalse(spot.supports(VehicleType.TRUCK), "not truck");
        });

        runner.test("Spot: compatible vehicle can park", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            spot.assignVehicle(new Vehicle("A-1", VehicleType.CAR));
            assertFalse(spot.isAvailable(), "occupied");
            assertEquals("A-1", spot.parkedLicensePlate(), "records plate");
        });

        runner.test("Spot: incompatible vehicle rejected", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            assertThrows(IllegalArgumentException.class,
                    () -> spot.assignVehicle(new Vehicle("A-1", VehicleType.TRUCK)), "incompatible");
        });

        runner.test("Spot: occupied spot rejects another vehicle", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            spot.assignVehicle(new Vehicle("A-1", VehicleType.CAR));
            assertThrows(IllegalStateException.class,
                    () -> spot.assignVehicle(new Vehicle("B-2", VehicleType.CAR)), "occupied");
        });

        runner.test("Spot: release occupied spot works and returns vehicle", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            spot.assignVehicle(new Vehicle("A-1", VehicleType.CAR));
            Vehicle released = spot.releaseVehicle();
            assertEquals("A-1", released.getLicensePlate(), "returned vehicle");
            assertTrue(spot.isAvailable(), "free again");
            assertNull(spot.parkedLicensePlate(), "no plate when free");
        });

        runner.test("Spot: release available spot rejected", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            assertThrows(IllegalStateException.class, spot::releaseVehicle, "release free");
        });

        runner.test("Spot: snapshot contains expected data", () -> {
            ParkingSpot spot = new ParkingSpot("C-01", VehicleType.CAR);
            ParkingSpotSnapshot free = spot.toSnapshot();
            assertEquals("C-01", free.getSpotId(), "snapshot id");
            assertEquals(VehicleType.CAR, free.getSupportedType(), "snapshot type");
            assertTrue(free.isAvailable(), "snapshot available");
            assertNull(free.getParkedLicensePlate(), "snapshot no plate");
            spot.assignVehicle(new Vehicle("A-1", VehicleType.CAR));
            ParkingSpotSnapshot busy = spot.toSnapshot();
            assertFalse(busy.isAvailable(), "snapshot occupied");
            assertEquals("A-1", busy.getParkedLicensePlate(), "snapshot plate");
            // Old snapshot must not change.
            assertTrue(free.isAvailable(), "old snapshot unchanged");
        });
    }
}
