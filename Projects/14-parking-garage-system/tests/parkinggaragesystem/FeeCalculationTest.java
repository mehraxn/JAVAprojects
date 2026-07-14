package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static parkinggaragesystem.TestSupport.assertBigDecimalEquals;
import static parkinggaragesystem.TestSupport.assertEquals;
import static parkinggaragesystem.TestSupport.assertThrows;

final class FeeCalculationTest {

    private FeeCalculationTest() {
    }

    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 7, 4, 10, 0);

    private static long hours(long minutes, long seconds) {
        return Garage.calculateBilledHours(ENTRY, ENTRY.plusMinutes(minutes).plusSeconds(seconds));
    }

    static void register(TestRunner runner) {
        runner.test("Fee: 0 minutes bills 1 hour", () ->
                assertEquals(1L, hours(0, 0), "0 min -> 1h"));

        runner.test("Fee: 1 minute bills 1 hour", () ->
                assertEquals(1L, hours(1, 0), "1 min -> 1h"));

        runner.test("Fee: exactly 60 minutes bills 1 hour", () ->
                assertEquals(1L, hours(60, 0), "60 min -> 1h"));

        runner.test("Fee: 61 minutes bills 2 hours", () ->
                assertEquals(2L, hours(61, 0), "61 min -> 2h"));

        runner.test("Fee: 90 minutes bills 2 hours", () ->
                assertEquals(2L, hours(90, 0), "90 min -> 2h"));

        runner.test("Fee: 1 hour 1 second bills 2 hours", () ->
                assertEquals(2L, hours(60, 1), "1h1s -> 2h"));

        runner.test("Fee: exit before entry rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> Garage.calculateBilledHours(ENTRY, ENTRY.minusSeconds(1)), "exit<entry"));

        runner.test("Fee: car 90 minutes = 10.00 (rate 5.00)", () ->
                assertBigDecimalEquals(new BigDecimal("10.00"),
                        Garage.calculateFee(VehicleType.CAR, ENTRY, ENTRY.plusMinutes(90)),
                        "car 2h fee"));

        runner.test("Fee: motorcycle 30 minutes = 3.00 (rate 3.00)", () ->
                assertBigDecimalEquals(new BigDecimal("3.00"),
                        Garage.calculateFee(VehicleType.MOTORCYCLE, ENTRY, ENTRY.plusMinutes(30)),
                        "moto 1h fee"));

        runner.test("Fee: truck 2h1m = 30.00 (rate 10.00, 3h)", () ->
                assertBigDecimalEquals(new BigDecimal("30.00"),
                        Garage.calculateFee(VehicleType.TRUCK, ENTRY, ENTRY.plusMinutes(121)),
                        "truck 3h fee"));

        runner.test("Fee: hourly rates by type", () -> {
            assertBigDecimalEquals(new BigDecimal("3.00"),
                    Garage.hourlyRate(VehicleType.MOTORCYCLE), "moto rate");
            assertBigDecimalEquals(new BigDecimal("5.00"),
                    Garage.hourlyRate(VehicleType.CAR), "car rate");
            assertBigDecimalEquals(new BigDecimal("10.00"),
                    Garage.hourlyRate(VehicleType.TRUCK), "truck rate");
        });

        runner.test("Fee: null vehicle type rate rejected", () ->
                assertThrows(IllegalArgumentException.class,
                        () -> Garage.hourlyRate(null), "null type"));
    }
}
