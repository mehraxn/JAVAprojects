package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static parkinggaragesystem.TestSupport.assertBigDecimalEquals;
import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertThrows;

final class ParkingReceiptTest {

    private ParkingReceiptTest() {
    }

    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 7, 4, 10, 0);
    private static final LocalDateTime EXIT = ENTRY.plusHours(2);

    private static ParkingReceipt sample() {
        return new ParkingReceipt("R0001", "B-AB 123", VehicleType.CAR, "C-01", 0,
                ENTRY, EXIT, 2L, new BigDecimal("5.00"), new BigDecimal("10.00"));
    }

    static void register(TestRunner runner) {
        runner.test("Receipt: valid creation stores all fields", () -> {
            ParkingReceipt receipt = sample();
            assertEquals("R0001", receipt.getReceiptId(), "receipt id");
            assertEquals("B-AB 123", receipt.getLicensePlate(), "plate");
            assertEquals(VehicleType.CAR, receipt.getVehicleType(), "type");
            assertEquals("C-01", receipt.getSpotId(), "spot id");
            assertEquals(0, receipt.getLevelNumber(), "level");
            assertEquals(ENTRY, receipt.getEntryTime(), "entry");
            assertEquals(EXIT, receipt.getExitTime(), "exit");
            assertEquals(2L, receipt.getBilledHours(), "billed hours");
            assertBigDecimalEquals(new BigDecimal("5.00"), receipt.getHourlyRate(), "rate");
            assertBigDecimalEquals(new BigDecimal("10.00"), receipt.getFee(), "fee");
        });

        runner.test("Receipt: blank receipt id / plate / spot rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingReceipt("  ", "P", VehicleType.CAR, "S", 0, ENTRY, EXIT,
                            1L, BigDecimal.ONE, BigDecimal.ONE), "blank receipt id");
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingReceipt("R", "  ", VehicleType.CAR, "S", 0, ENTRY, EXIT,
                            1L, BigDecimal.ONE, BigDecimal.ONE), "blank plate");
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingReceipt("R", "P", VehicleType.CAR, "  ", 0, ENTRY, EXIT,
                            1L, BigDecimal.ONE, BigDecimal.ONE), "blank spot");
        });

        runner.test("Receipt: null vehicle type rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingReceipt("R", "P", null, "S", 0, ENTRY, EXIT,
                                1L, BigDecimal.ONE, BigDecimal.ONE), "null type"));

        runner.test("Receipt: null entry/exit time rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingReceipt("R", "P", VehicleType.CAR, "S", 0, null, EXIT,
                            1L, BigDecimal.ONE, BigDecimal.ONE), "null entry");
            assertThrows(IllegalArgumentException.class,
                    () -> new ParkingReceipt("R", "P", VehicleType.CAR, "S", 0, ENTRY, null,
                            1L, BigDecimal.ONE, BigDecimal.ONE), "null exit");
        });

        runner.test("Receipt: exit before entry rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingReceipt("R", "P", VehicleType.CAR, "S", 0,
                                ENTRY, ENTRY.minusHours(1), 1L, BigDecimal.ONE, BigDecimal.ONE),
                        "exit before entry"));

        runner.test("Receipt: null fee rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingReceipt("R", "P", VehicleType.CAR, "S", 0, ENTRY, EXIT,
                                1L, BigDecimal.ONE, null), "null fee"));

        runner.test("Receipt: negative fee rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> new ParkingReceipt("R", "P", VehicleType.CAR, "S", 0, ENTRY, EXIT,
                                1L, BigDecimal.ONE, new BigDecimal("-0.01")), "negative fee"));

        runner.test("Receipt: snapshot contains expected data", () -> {
            ParkingReceiptSnapshot snapshot = sample().toSnapshot();
            assertEquals("R0001", snapshot.getReceiptId(), "snapshot id");
            assertEquals("B-AB 123", snapshot.getLicensePlate(), "snapshot plate");
            assertEquals(VehicleType.CAR, snapshot.getVehicleType(), "snapshot type");
            assertEquals("C-01", snapshot.getSpotId(), "snapshot spot");
            assertEquals(2L, snapshot.getBilledHours(), "snapshot hours");
            assertBigDecimalEquals(new BigDecimal("10.00"), snapshot.getFee(), "snapshot fee");
        });
    }
}
