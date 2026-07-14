package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static parkinggaragesystem.TestSupport.assertBigDecimalEquals;
import static parkinggaragesystem.TestSupport.assertDoubleEquals;
import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertFalse;
import static parkinggaragesystem.TestSupport.assertThrows;
import static parkinggaragesystem.TestSupport.assertTrue;

final class GarageTest {

    private GarageTest() {
    }

    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 7, 4, 10, 0);

    private static List<ParkingSpot> spots(String... idAndType) {
        // idAndType comes in pairs: "M-01","MOTORCYCLE", ...
        List<ParkingSpot> list = new ArrayList<>();
        for (int i = 0; i < idAndType.length; i += 2) {
            list.add(new ParkingSpot(idAndType[i], VehicleType.valueOf(idAndType[i + 1])));
        }
        return list;
    }

    /** Level 0: M-01, C-01, C-02, T-01. Level 1: C-03, C-04. */
    private static Garage sample() {
        Garage garage = new Garage();
        garage.addLevel(new ParkingLevel(0, spots(
                "M-01", "MOTORCYCLE", "C-01", "CAR", "C-02", "CAR", "T-01", "TRUCK")));
        garage.addLevel(new ParkingLevel(1, spots("C-03", "CAR", "C-04", "CAR")));
        return garage;
    }

    static void register(TestRunner runner) {
        registerSetup(runner);
        registerParking(runner);
        registerExit(runner);
        registerReports(runner);
    }

    private static void registerSetup(TestRunner runner) {
        runner.test("Garage: add level and list levels (snapshots)", () -> {
            Garage garage = sample();
            assertEquals(2, garage.listLevels().size(), "two levels");
            assertEquals(6, garage.getTotalSpotCount(), "six spots total");
        });

        runner.test("Garage: null level rejected", () -> {
            Garage garage = new Garage();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.addLevel(null), "null level");
        });

        runner.test("Garage: duplicate level number rejected", () -> {
            Garage garage = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.addLevel(new ParkingLevel(0, spots("X-1", "CAR"))), "dup level");
        });

        runner.test("Garage: duplicate spot ID across levels rejected", () -> {
            Garage garage = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.addLevel(new ParkingLevel(2, spots("C-01", "CAR"))), "dup spot");
        });

        runner.test("Garage: availability counts (total and by type)", () -> {
            Garage garage = sample();
            assertEquals(6, garage.countAvailableSpots(), "6 available");
            assertEquals(4, garage.countAvailableSpots(VehicleType.CAR), "4 car");
            assertEquals(1, garage.countAvailableSpots(VehicleType.MOTORCYCLE), "1 moto");
            assertEquals(1, garage.countAvailableSpots(VehicleType.TRUCK), "1 truck");
        });
    }

    private static void registerParking(TestRunner runner) {
        runner.test("Garage: park vehicle assigns a compatible spot", () -> {
            Garage garage = sample();
            ActiveParkingSnapshot active = garage.parkVehicle(
                    new Vehicle("A-1", VehicleType.CAR), ENTRY);
            assertEquals(VehicleType.CAR, active.getVehicleType(), "car parked");
            assertTrue(active.getSpotId().startsWith("C-"), "car-compatible spot");
            assertEquals(3, garage.countAvailableSpots(VehicleType.CAR), "one fewer car spot");
        });

        runner.test("Garage: null vehicle rejected", () -> {
            Garage garage = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.parkVehicle(null, ENTRY), "null vehicle");
        });

        runner.test("Garage: null entry time rejected", () -> {
            Garage garage = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), null), "null entry");
        });

        runner.test("Garage: duplicate active vehicle rejected (normalized plate)", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            assertThrows(IllegalStateException.class,
                    () -> garage.parkVehicle(new Vehicle("a-1", VehicleType.CAR), ENTRY), "dup active");
        });

        runner.test("Garage: no compatible spot rejects parking", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("T-1", VehicleType.TRUCK), ENTRY); // only truck spot
            assertThrows(IllegalStateException.class,
                    () -> garage.parkVehicle(new Vehicle("T-2", VehicleType.TRUCK), ENTRY),
                    "no truck spot");
        });

        runner.test("Garage: failed parking leaves availability unchanged", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("T-1", VehicleType.TRUCK), ENTRY);
            int available = garage.countAvailableSpots();
            int parked = garage.getParkedVehicleCount();
            assertThrows(IllegalStateException.class,
                    () -> garage.parkVehicle(new Vehicle("T-2", VehicleType.TRUCK), ENTRY), "fail");
            assertEquals(available, garage.countAvailableSpots(), "availability unchanged");
            assertEquals(parked, garage.getParkedVehicleCount(), "parked count unchanged");
        });

        runner.test("Garage: findVehicle returns active snapshot, unknown empty", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            assertTrue(garage.findVehicle("a-1").isPresent(), "found normalized");
            assertFalse(garage.findVehicle("NOPE").isPresent(), "unknown empty");
        });
    }

    private static void registerExit(TestRunner runner) {
        runner.test("Garage: exit releases exact spot and increases availability", () -> {
            Garage garage = sample();
            ActiveParkingSnapshot active = garage.parkVehicle(
                    new Vehicle("A-1", VehicleType.CAR), ENTRY);
            String spotId = active.getSpotId();
            assertEquals(3, garage.countAvailableSpots(VehicleType.CAR), "3 car free");
            ParkingReceiptSnapshot receipt = garage.exitVehicle("A-1", ENTRY.plusMinutes(30));
            assertEquals(spotId, receipt.getSpotId(), "exact spot released");
            assertEquals(4, garage.countAvailableSpots(VehicleType.CAR), "4 car free again");
        });

        runner.test("Garage: exit generates receipt stored in history", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            ParkingReceiptSnapshot receipt = garage.exitVehicle("A-1", ENTRY.plusMinutes(90));
            assertEquals("R0001", receipt.getReceiptId(), "receipt id");
            assertBigDecimalEquals(new BigDecimal("10.00"), receipt.getFee(), "2h car = 10.00");
            assertEquals(1, garage.listReceipts().size(), "one receipt in history");
        });

        runner.test("Garage: exit unknown vehicle rejected", () -> {
            Garage garage = sample();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.exitVehicle("NOPE", ENTRY.plusHours(1)), "unknown exit");
        });

        runner.test("Garage: exit same vehicle twice rejected", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("A-1", ENTRY.plusHours(1));
            assertThrows(IllegalArgumentException.class,
                    () -> garage.exitVehicle("A-1", ENTRY.plusHours(2)), "double exit");
        });

        runner.test("Garage: exit before entry rejected, state unchanged", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            int parked = garage.getParkedVehicleCount();
            assertThrows(IllegalArgumentException.class,
                    () -> garage.exitVehicle("A-1", ENTRY.minusHours(1)), "exit<entry");
            assertEquals(parked, garage.getParkedVehicleCount(), "still parked");
            assertEquals(0, garage.listReceipts().size(), "no receipt");
        });

        runner.test("Garage: null exit time rejected", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            assertThrows(IllegalArgumentException.class,
                    () -> garage.exitVehicle("A-1", null), "null exit time");
        });
    }

    private static void registerReports(TestRunner runner) {
        runner.test("Garage: active parking list works", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.parkVehicle(new Vehicle("M-1", VehicleType.MOTORCYCLE), ENTRY);
            assertEquals(2, garage.listActiveParkings().size(), "two active");
        });

        runner.test("Garage: total revenue and revenue by type", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("M-1", VehicleType.MOTORCYCLE), ENTRY);
            garage.parkVehicle(new Vehicle("C-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("M-1", ENTRY.plusMinutes(30)); // 1h moto = 3.00
            garage.exitVehicle("C-1", ENTRY.plusMinutes(90)); // 2h car  = 10.00
            assertBigDecimalEquals(new BigDecimal("13.00"), garage.calculateTotalRevenue(), "total");
            Map<VehicleType, BigDecimal> byType = garage.calculateRevenueByVehicleType();
            assertBigDecimalEquals(new BigDecimal("3.00"), byType.get(VehicleType.MOTORCYCLE), "moto");
            assertBigDecimalEquals(new BigDecimal("10.00"), byType.get(VehicleType.CAR), "car");
        });

        runner.test("Garage: occupancy percentage works", () -> {
            Garage garage = sample(); // 6 spots
            assertDoubleEquals(0.0, garage.calculateCurrentOccupancyPercentage(), 0.0001, "empty 0%");
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.parkVehicle(new Vehicle("A-2", VehicleType.CAR), ENTRY);
            garage.parkVehicle(new Vehicle("A-3", VehicleType.CAR), ENTRY);
            assertDoubleEquals(50.0, garage.calculateCurrentOccupancyPercentage(), 0.0001, "3/6 = 50%");
        });

        runner.test("Garage: completed receipts do not affect availability", () -> {
            Garage garage = sample();
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("A-1", ENTRY.plusHours(1));
            assertEquals(6, garage.countAvailableSpots(), "all free after exit");
            assertEquals(1, garage.listReceipts().size(), "receipt kept");
        });

        runner.test("Garage: empty garage occupancy is 0", () -> {
            Garage garage = new Garage();
            assertDoubleEquals(0.0, garage.calculateCurrentOccupancyPercentage(), 0.0001, "no spots");
        });

        runner.test("Garage: full-garage behavior across levels", () -> {
            Garage garage = new Garage();
            garage.addLevel(new ParkingLevel(0, Arrays.asList(new ParkingSpot("C-01", VehicleType.CAR))));
            garage.parkVehicle(new Vehicle("A-1", VehicleType.CAR), ENTRY);
            assertThrows(IllegalStateException.class,
                    () -> garage.parkVehicle(new Vehicle("A-2", VehicleType.CAR), ENTRY), "full");
        });
    }
}
