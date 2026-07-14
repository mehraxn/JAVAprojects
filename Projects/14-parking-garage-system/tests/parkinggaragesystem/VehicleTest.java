package parkinggaragesystem;

import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertNotEquals;
import static parkinggaragesystem.TestSupport.assertThrows;
import static parkinggaragesystem.TestSupport.assertTrue;

final class VehicleTest {

    private VehicleTest() {
    }

    static void register(TestRunner runner) {
        runner.test("Vehicle: valid creation stores normalized plate and type", () -> {
            Vehicle vehicle = new Vehicle(" b-ab 123 ", VehicleType.CAR);
            assertEquals("B-AB 123", vehicle.getLicensePlate(), "plate normalized");
            assertEquals(VehicleType.CAR, vehicle.getType(), "type stored");
        });

        runner.test("Vehicle: lowercase plate normalized to uppercase", () ->
                assertEquals("XYZ-9", new Vehicle("xyz-9", VehicleType.TRUCK).getLicensePlate(),
                        "uppercased"));

        runner.test("Vehicle: null license plate rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Vehicle(null, VehicleType.CAR), "null plate"));

        runner.test("Vehicle: blank license plate rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Vehicle("  ", VehicleType.CAR), "blank plate"));

        runner.test("Vehicle: null vehicle type rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new Vehicle("A-1", null), "null type"));

        runner.test("Vehicle: equality uses normalized plate", () -> {
            Vehicle a = new Vehicle("b-ab 123", VehicleType.CAR);
            Vehicle b = new Vehicle("B-AB 123", VehicleType.CAR);
            assertEquals(a, b, "same normalized plate equals");
            assertEquals(a.hashCode(), b.hashCode(), "same hashCode");
            assertNotEquals(a, new Vehicle("B-AB 123", VehicleType.TRUCK), "type differs");
        });

        runner.test("Vehicle: VehicleType has expected values", () -> {
            assertEquals(3, VehicleType.values().length, "three types");
            assertTrue(VehicleType.valueOf("CAR") == VehicleType.CAR, "CAR present");
        });

        runner.test("Vehicle: snapshot contains expected data", () -> {
            VehicleSnapshot snapshot = new Vehicle("A-1", VehicleType.MOTORCYCLE).toSnapshot();
            assertEquals("A-1", snapshot.getLicensePlate(), "snapshot plate");
            assertEquals(VehicleType.MOTORCYCLE, snapshot.getType(), "snapshot type");
        });
    }
}
