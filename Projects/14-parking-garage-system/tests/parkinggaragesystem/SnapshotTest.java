package parkinggaragesystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertThrows;
import static parkinggaragesystem.TestSupport.assertTrue;

/**
 * Proves that data leaving {@link Garage} is safe: returned lists are
 * unmodifiable, snapshots carry no mutators, and holding a snapshot cannot change
 * internal spot, level, or receipt state.
 */
final class SnapshotTest {

    private SnapshotTest() {
    }

    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 7, 4, 10, 0);

    private static Garage sample() {
        Garage garage = new Garage();
        garage.addLevel(new ParkingLevel(0, Arrays.asList(
                new ParkingSpot("C-01", VehicleType.CAR),
                new ParkingSpot("C-02", VehicleType.CAR),
                new ParkingSpot("M-01", VehicleType.MOTORCYCLE))));
        return garage;
    }

    static void register(TestRunner runner) {
        runner.test("Snapshot: listLevels result is unmodifiable", () -> {
            List<ParkingLevelSnapshot> levels = sample().listLevels();
            assertThrows(UnsupportedOperationException.class, () -> levels.clear(), "levels");
        });

        runner.test("Snapshot: level snapshot spot list is unmodifiable", () -> {
            ParkingLevelSnapshot level = sample().listLevels().get(0);
            assertThrows(UnsupportedOperationException.class,
                    () -> level.getSpots().clear(), "spot list");
        });

        runner.test("Snapshot: listActiveParkings result is unmodifiable", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            List<ActiveParkingSnapshot> active = garage.listActiveParkings();
            assertThrows(UnsupportedOperationException.class, () -> active.clear(), "active");
        });

        runner.test("Snapshot: listReceipts result is unmodifiable", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("A-1", ENTRY.plusHours(1));
            List<ParkingReceiptSnapshot> receipts = garage.listReceipts();
            assertThrows(UnsupportedOperationException.class, () -> receipts.clear(), "receipts");
        });

        runner.test("Snapshot: spot snapshot has no mutator; internal spot untouched", () -> {
            Garage garage = sample();
            ParkingSpotSnapshot spot = garage.listLevels().get(0).getSpots().get(0);
            // ParkingSpotSnapshot exposes only getters; there is no way to release/assign.
            assertTrue(spot.isAvailable(), "snapshot available");
            assertEquals(3, garage.countAvailableSpots(), "internal availability intact");
        });

        runner.test("Snapshot: level snapshot is decoupled from live garage", () -> {
            Garage garage = sample();
            ParkingLevelSnapshot before = garage.listLevels().get(0);
            assertEquals(3, before.getAvailableSpots(), "captured 3 free");
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            // Old snapshot must not change.
            assertEquals(3, before.getAvailableSpots(), "old snapshot unchanged");
            assertEquals(2, garage.listLevels().get(0).getAvailableSpots(), "fresh snapshot updated");
        });

        runner.test("Snapshot: exit receipt snapshot cannot corrupt receipt history", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("A-1", ENTRY.plusHours(1));
            // Grabbing the list and attempting mutation must not change stored history.
            List<ParkingReceiptSnapshot> receipts = garage.listReceipts();
            try {
                receipts.add(null);
            } catch (UnsupportedOperationException ignored) {
                // expected
            }
            assertEquals(1, garage.listReceipts().size(), "history intact");
        });

        runner.test("Snapshot: reading snapshots leaves internal availability correct", () -> {
            Garage garage = sample();
            garage.listLevels();
            garage.listActiveParkings();
            garage.findVehicle("nobody");
            assertEquals(3, garage.countAvailableSpots(), "still 3 free after reads");
        });

        runner.test("Snapshot: parkVehicle return value exposes no spot mutator", () -> {
            Garage garage = sample();
            ActiveParkingSnapshot active = garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            // ActiveParkingSnapshot has only getters — no way to release the spot.
            assertEquals("A-1", active.getLicensePlate(), "plate readable");
            assertEquals(2, garage.countAvailableSpots(), "one spot taken, no external release");
        });
    }
}
