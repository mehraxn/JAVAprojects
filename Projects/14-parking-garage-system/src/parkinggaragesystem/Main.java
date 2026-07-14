package parkinggaragesystem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command-line entry point and demo driver.
 *
 * <p>All work happens in {@link #run(String[], PrintStream, PrintStream)}, which
 * returns an exit code and never calls {@link System#exit}. Only
 * {@link #main(String[])} exits the JVM, so the CLI can be tested in-process.
 */
public final class Main {

    // Fixed reference time so demo output is stable and reproducible.
    private static final LocalDateTime ENTRY = LocalDateTime.of(2026, 7, 4, 10, 0);

    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    /**
     * Runs one CLI command.
     *
     * @return {@code 0} for a recognised command (including {@code validation-demo},
     *         whose failures are intentional), non-zero for an unknown command.
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = (args == null || args.length == 0) ? "help" : args[0];
        switch (command) {
            case "help":
            case "--help":
            case "-h":
                printHelp(out);
                return 0;
            case "demo":
                runDemo(out);
                return 0;
            case "parking-demo":
                runParkingDemo(out);
                return 0;
            case "exit-demo":
                runExitDemo(out);
                return 0;
            case "fee-demo":
                runFeeDemo(out);
                return 0;
            case "full-garage-demo":
                runFullGarageDemo(out);
                return 0;
            case "report-demo":
                runReportDemo(out);
                return 0;
            case "validation-demo":
                runValidationDemo(out);
                return 0;
            default:
                err.println("Unknown command: " + command);
                err.println("Run 'help' to see available commands.");
                return 2;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println("Parking Garage System");
        out.println();
        out.println("Usage: java -cp out parkinggaragesystem.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help              Show this help text.");
        out.println("  demo              End-to-end park/exit walkthrough.");
        out.println("  parking-demo      Compatible spot assignment and rejection rules.");
        out.println("  exit-demo         Exit, receipt, exact spot release, double-exit rejection.");
        out.println("  fee-demo          Started-hour billing boundaries and vehicle-type rates.");
        out.println("  full-garage-demo  Fill compatible spots, then reject an extra vehicle.");
        out.println("  report-demo       Active parkings, receipt history, revenue, occupancy.");
        out.println("  validation-demo   Intentional validation failures, handled cleanly.");
    }

    // ------------------------------------------------------------------- demos

    /** Sample garage: level 0 with motorcycle/car/truck spots, level 1 with cars. */
    private static Garage sampleGarage() {
        Garage garage = new Garage();
        List<ParkingSpot> level0 = new ArrayList<>();
        level0.add(new ParkingSpot("M-01", VehicleType.MOTORCYCLE));
        level0.add(new ParkingSpot("C-01", VehicleType.CAR));
        level0.add(new ParkingSpot("C-02", VehicleType.CAR));
        level0.add(new ParkingSpot("T-01", VehicleType.TRUCK));
        garage.addLevel(new ParkingLevel(0, level0));

        List<ParkingSpot> level1 = new ArrayList<>();
        level1.add(new ParkingSpot("C-03", VehicleType.CAR));
        level1.add(new ParkingSpot("C-04", VehicleType.CAR));
        garage.addLevel(new ParkingLevel(1, level1));
        return garage;
    }

    private static void runDemo(PrintStream out) {
        out.println("== Parking Garage Demo ==");
        Garage garage = sampleGarage();
        out.println("Total spots: " + garage.getTotalSpotCount()
                + ", available: " + garage.countAvailableSpots());

        out.println();
        out.println("Parking car B-AB 123 at " + ENTRY + " ...");
        ActiveParkingSnapshot active = garage.parkVehicle(new Vehicle("B-AB 123", VehicleType.CAR), ENTRY);
        out.println("  Assigned spot: " + active.getSpotId() + " on level " + active.getLevelNumber());
        out.println("  Available car spots now: " + garage.countAvailableSpots(VehicleType.CAR));

        out.println();
        out.println("Exiting after 1h30m ...");
        ParkingReceiptSnapshot receipt = garage.exitVehicle("b-ab 123", ENTRY.plusHours(1).plusMinutes(30));
        out.println("  Receipt " + receipt.getReceiptId() + ": " + receipt.getBilledHours()
                + "h x " + formatMoney(receipt.getHourlyRate()) + " = " + formatMoney(receipt.getFee()));
        out.println("  Spot released: " + receipt.getSpotId());
        out.println("  Available spots after exit: " + garage.countAvailableSpots());
    }

    private static void runParkingDemo(PrintStream out) {
        out.println("== Parking Demo ==");
        Garage garage = sampleGarage();

        ActiveParkingSnapshot moto = garage.parkVehicle(new Vehicle("M-1", VehicleType.MOTORCYCLE), ENTRY);
        out.println("Motorcycle M-1 -> spot " + moto.getSpotId());
        ActiveParkingSnapshot car = garage.parkVehicle(new Vehicle("C-1", VehicleType.CAR), ENTRY);
        out.println("Car C-1        -> spot " + car.getSpotId() + " (only car-compatible spots used)");
        ActiveParkingSnapshot truck = garage.parkVehicle(new Vehicle("T-1", VehicleType.TRUCK), ENTRY);
        out.println("Truck T-1      -> spot " + truck.getSpotId());

        out.println();
        expectFailure(out, "duplicate vehicle entry",
                () -> garage.parkVehicle(new Vehicle("c-1", VehicleType.CAR), ENTRY));

        out.println();
        out.println("A truck can only use TRUCK spots; with T-01 taken, another truck is rejected:");
        expectFailure(out, "no compatible spot for second truck",
                () -> garage.parkVehicle(new Vehicle("T-2", VehicleType.TRUCK), ENTRY));
    }

    private static void runExitDemo(PrintStream out) {
        out.println("== Exit Demo ==");
        Garage garage = sampleGarage();

        garage.parkVehicle(new Vehicle("C-1", VehicleType.CAR), ENTRY);
        out.println("Parked C-1. Available: " + garage.countAvailableSpots());
        ParkingReceiptSnapshot receipt = garage.exitVehicle("C-1", ENTRY.plusMinutes(45));
        out.println("Exited C-1 -> receipt " + receipt.getReceiptId()
                + ", released spot " + receipt.getSpotId()
                + ", fee " + formatMoney(receipt.getFee()) + " (" + receipt.getBilledHours() + "h)");
        out.println("Available after exit: " + garage.countAvailableSpots());

        out.println();
        expectFailure(out, "second exit of same vehicle",
                () -> garage.exitVehicle("C-1", ENTRY.plusHours(2)));
    }

    private static void runFeeDemo(PrintStream out) {
        out.println("== Fee Demo (started-hour billing) ==");
        out.println("Car rate " + formatMoney(Garage.hourlyRate(VehicleType.CAR)) + "/h:");
        printFee(out, "  0 minutes ", 0);
        printFee(out, " 60 minutes ", 60);
        printFee(out, " 61 minutes ", 61);
        printFee(out, " 90 minutes ", 90);

        out.println();
        out.println("Vehicle-type rates:");
        for (VehicleType type : VehicleType.values()) {
            out.println("  " + pad(type.name(), 12) + " " + formatMoney(Garage.hourlyRate(type)) + "/h");
        }
    }

    private static void runFullGarageDemo(PrintStream out) {
        out.println("== Full Garage Demo ==");
        Garage garage = sampleGarage();

        out.println("Filling all 4 car spots...");
        garage.parkVehicle(new Vehicle("C-1", VehicleType.CAR), ENTRY);
        garage.parkVehicle(new Vehicle("C-2", VehicleType.CAR), ENTRY);
        garage.parkVehicle(new Vehicle("C-3", VehicleType.CAR), ENTRY);
        garage.parkVehicle(new Vehicle("C-4", VehicleType.CAR), ENTRY);
        out.println("Available car spots: " + garage.countAvailableSpots(VehicleType.CAR));

        out.println();
        int activeBefore = garage.getParkedVehicleCount();
        expectFailure(out, "extra car into full car area",
                () -> garage.parkVehicle(new Vehicle("C-5", VehicleType.CAR), ENTRY));
        out.println("  Parked vehicle count unchanged: " + activeBefore
                + " -> " + garage.getParkedVehicleCount());
    }

    private static void runReportDemo(PrintStream out) {
        out.println("== Report Demo ==");
        Garage garage = sampleGarage();
        garage.parkVehicle(new Vehicle("M-1", VehicleType.MOTORCYCLE), ENTRY);
        garage.parkVehicle(new Vehicle("C-1", VehicleType.CAR), ENTRY);
        garage.parkVehicle(new Vehicle("T-1", VehicleType.TRUCK), ENTRY);
        garage.exitVehicle("M-1", ENTRY.plusMinutes(30));   // 1h motorcycle -> 3.00
        garage.exitVehicle("C-1", ENTRY.plusMinutes(90));   // 2h car -> 10.00

        out.println("Active parkings:");
        for (ActiveParkingSnapshot active : garage.listActiveParkings()) {
            out.println("  " + active.getLicensePlate() + " (" + active.getVehicleType()
                    + ") spot " + active.getSpotId());
        }

        out.println();
        out.println("Receipt history:");
        for (ParkingReceiptSnapshot receipt : garage.listReceipts()) {
            out.println("  " + receipt.getReceiptId() + " " + receipt.getLicensePlate()
                    + " " + receipt.getBilledHours() + "h " + formatMoney(receipt.getFee()));
        }

        out.println();
        out.println("Total revenue: " + formatMoney(garage.calculateTotalRevenue()));
        out.println("Revenue by vehicle type:");
        for (Map.Entry<VehicleType, BigDecimal> entry : garage.calculateRevenueByVehicleType().entrySet()) {
            out.println("  " + pad(entry.getKey().name(), 12) + " " + formatMoney(entry.getValue()));
        }
        out.printf("Occupancy: %.1f%%%n", garage.calculateCurrentOccupancyPercentage());
    }

    private static void runValidationDemo(PrintStream out) {
        out.println("== Validation Demo (failures below are intentional) ==");
        Garage garage = sampleGarage();

        expectFailure(out, "blank license plate",
                () -> new Vehicle("  ", VehicleType.CAR));
        expectFailure(out, "null vehicle type",
                () -> new Vehicle("X-1", null));
        expectFailure(out, "duplicate level number",
                () -> garage.addLevel(new ParkingLevel(0,
                        java.util.Arrays.asList(new ParkingSpot("Z-9", VehicleType.CAR)))));
        expectFailure(out, "duplicate spot ID across garage",
                () -> garage.addLevel(new ParkingLevel(2,
                        java.util.Arrays.asList(new ParkingSpot("C-01", VehicleType.CAR)))));
        expectFailure(out, "incompatible vehicle for spot",
                () -> new ParkingSpot("X", VehicleType.MOTORCYCLE)
                        .assignVehicle(new Vehicle("CAR-1", VehicleType.CAR)));
        expectFailure(out, "duplicate vehicle entry", () -> {
            garage.parkVehicle(new Vehicle("DUP-1", VehicleType.CAR), ENTRY);
            garage.parkVehicle(new Vehicle("dup-1", VehicleType.CAR), ENTRY);
        });
        expectFailure(out, "unknown vehicle exit",
                () -> garage.exitVehicle("NOPE-9", ENTRY.plusHours(1)));
        expectFailure(out, "exit before entry", () -> {
            garage.parkVehicle(new Vehicle("EARLY-1", VehicleType.CAR), ENTRY);
            garage.exitVehicle("EARLY-1", ENTRY.minusHours(1));
        });

        out.println();
        out.println("All validation failures were handled cleanly.");
    }

    // ----------------------------------------------------------------- helpers

    private static void printFee(PrintStream out, String label, long minutes) {
        BigDecimal fee = Garage.calculateFee(VehicleType.CAR, ENTRY, ENTRY.plusMinutes(minutes));
        long hours = Garage.calculateBilledHours(ENTRY, ENTRY.plusMinutes(minutes));
        out.println(label + "-> billed " + hours + "h = " + formatMoney(fee));
    }

    private static String formatMoney(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String pad(String text, int width) {
        StringBuilder builder = new StringBuilder(text);
        while (builder.length() < width) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private static void expectFailure(PrintStream out, String label, Runnable action) {
        try {
            action.run();
            out.println("  [" + label + "] ERROR: expected a failure but none occurred");
        } catch (IllegalArgumentException | IllegalStateException expected) {
            out.println("  [" + label + "] rejected: " + expected.getMessage());
        }
    }
}
