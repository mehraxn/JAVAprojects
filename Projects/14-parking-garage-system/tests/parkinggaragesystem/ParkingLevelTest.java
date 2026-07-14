package parkinggaragesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertFalse;
import static parkinggaragesystem.TestSupport.assertThrows;
import static parkinggaragesystem.TestSupport.assertTrue;

final class ParkingLevelTest {

    private ParkingLevelTest() {
    }

    private static List<ParkingSpot> spots() {
        List<ParkingSpot> list = new ArrayList<>();
        list.add(new ParkingSpot("M-01", VehicleType.MOTORCYCLE));
        list.add(new ParkingSpot("C-01", VehicleType.CAR));
        list.add(new ParkingSpot("C-02", VehicleType.CAR));
        return list;
    }

    static void register(TestRunner runner) {
        runner.test("Level: valid creation stores number and spots", () -> {
            ParkingLevel level = new ParkingLevel(2, spots());
            assertEquals(2, level.getNumber(), "number stored");
            assertEquals(3, level.getTotalSpotCount(), "spots stored");
        });

        runner.test("Level: negative number rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingLevel(-1, spots()), "negative number"));

        runner.test("Level: null spot list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingLevel(0, null), "null spots"));

        runner.test("Level: empty spot list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingLevel(0, new ArrayList<>()), "empty spots"));

        runner.test("Level: null spot in list rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingLevel(0, Arrays.asList(
                                new ParkingSpot("C-01", VehicleType.CAR), null)), "null spot"));

        runner.test("Level: duplicate spot ID rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingLevel(0, Arrays.asList(
                                new ParkingSpot("C-01", VehicleType.CAR),
                                new ParkingSpot("C-01", VehicleType.CAR))), "duplicate id"));

        runner.test("Level: find compatible available spot works", () -> {
            ParkingLevel level = new ParkingLevel(0, spots());
            ParkingSpot found = level.findAvailableSpot(VehicleType.CAR);
            assertTrue(found != null && found.supports(VehicleType.CAR), "found car spot");
            assertEquals("C-01", found.getId(), "first compatible spot");
        });

        runner.test("Level: count available spots (total and by type)", () -> {
            ParkingLevel level = new ParkingLevel(0, spots());
            assertEquals(3, level.getAvailableSpotCount(), "3 total available");
            assertEquals(2, level.getAvailableSpotCount(VehicleType.CAR), "2 car available");
            assertEquals(1, level.getAvailableSpotCount(VehicleType.MOTORCYCLE), "1 moto available");
            assertEquals(0, level.getAvailableSpotCount(VehicleType.TRUCK), "0 truck available");
        });

        runner.test("Level: full detection works", () -> {
            ParkingLevel level = new ParkingLevel(0,
                    Arrays.asList(new ParkingSpot("C-01", VehicleType.CAR)));
            assertFalse(level.isFull(), "not full initially");
            level.findAvailableSpot(VehicleType.CAR).assignVehicle(new Vehicle("A-1", VehicleType.CAR));
            assertTrue(level.isFull(), "full after occupying only spot");
            assertEquals(1, level.getOccupiedSpotCount(), "1 occupied");
        });

        runner.test("Level: getSpotSnapshots is unmodifiable and safe", () -> {
            ParkingLevel level = new ParkingLevel(0, spots());
            List<ParkingSpotSnapshot> snapshots = level.getSpotSnapshots();
            assertEquals(3, snapshots.size(), "three snapshots");
            assertThrows(UnsupportedOperationException.class,
                    () -> snapshots.clear(), "unmodifiable");
        });

        runner.test("Level: snapshot contains expected data", () -> {
            ParkingLevelSnapshot snapshot = new ParkingLevel(4, spots()).toSnapshot();
            assertEquals(4, snapshot.getLevelNumber(), "snapshot number");
            assertEquals(3, snapshot.getTotalSpots(), "snapshot total");
            assertEquals(3, snapshot.getAvailableSpots(), "snapshot available");
            assertEquals(3, snapshot.getSpots().size(), "snapshot spot list");
        });
    }
}
