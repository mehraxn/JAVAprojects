package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory service layer for a parking garage: levels and spots, vehicle entry
 * and exit, availability, receipts, and revenue reports. This is the only class
 * outside callers use to change state.
 *
 * <p>Every query returns immutable snapshots in unmodifiable lists, so live
 * {@link ParkingSpot}/{@link ParkingLevel}/{@link ParkingReceipt} objects are
 * never leaked and cannot be mutated from outside.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>License plates are normalized (trimmed, upper-cased); a plate can be
 *       actively parked only once.</li>
 *   <li>A vehicle is assigned the first compatible free spot, scanning levels in
 *       insertion order.</li>
 *   <li>Billing uses <strong>started-hour</strong> rules: any started hour counts
 *       as a full hour, with a minimum of one hour.</li>
 *   <li>Exit releases the exact assigned spot, removes the active record, and
 *       stores a receipt in completed history.</li>
 *   <li>Failed operations leave all state unchanged.</li>
 * </ul>
 */
public final class Garage {

    /** Per-vehicle-type hourly rates (money uses {@link BigDecimal}). */
    private static final Map<VehicleType, BigDecimal> HOURLY_RATES = buildRates();

    private final Map<Integer, ParkingLevel> levels = new LinkedHashMap<>();
    private final Map<String, ActiveParking> activeByPlate = new LinkedHashMap<>();
    private final List<ParkingReceipt> receipts = new ArrayList<>();
    private int nextReceiptNumber = 1;

    private static Map<VehicleType, BigDecimal> buildRates() {
        Map<VehicleType, BigDecimal> rates = new EnumMap<>(VehicleType.class);
        rates.put(VehicleType.MOTORCYCLE, new BigDecimal("3.00"));
        rates.put(VehicleType.CAR, new BigDecimal("5.00"));
        rates.put(VehicleType.TRUCK, new BigDecimal("10.00"));
        return Collections.unmodifiableMap(rates);
    }

    /** Hourly rate for a vehicle type. */
    public static BigDecimal hourlyRate(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        return HOURLY_RATES.get(type);
    }

    // ----------------------------------------------------------- garage setup

    public void addLevel(ParkingLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Parking level must not be null");
        }
        if (levels.containsKey(level.getNumber())) {
            throw new IllegalArgumentException("Parking level already exists: " + level.getNumber());
        }
        for (String spotId : level.spotIds()) {
            for (ParkingLevel existing : levels.values()) {
                if (existing.spotIds().contains(spotId)) {
                    throw new IllegalArgumentException(
                            "Parking spot ID already exists in the garage: " + spotId);
                }
            }
        }
        levels.put(level.getNumber(), level);
    }

    public List<ParkingLevelSnapshot> listLevels() {
        List<ParkingLevelSnapshot> views = new ArrayList<>();
        for (ParkingLevel level : levels.values()) {
            views.add(level.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    // --------------------------------------------------------------- entry

    /**
     * Parks a vehicle at the first compatible free spot.
     *
     * @return a snapshot of the new active parking session
     * @throws IllegalArgumentException if the vehicle or entry time is null
     * @throws IllegalStateException if the vehicle is already parked or no
     *         compatible spot is free
     */
    public ActiveParkingSnapshot parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null");
        }
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time must not be null");
        }
        if (activeByPlate.containsKey(vehicle.getLicensePlate())) {
            throw new IllegalStateException("Vehicle is already parked: " + vehicle.getLicensePlate());
        }

        for (ParkingLevel level : levels.values()) {
            ParkingSpot spot = level.findAvailableSpot(vehicle.getType());
            if (spot != null) {
                spot.assignVehicle(vehicle);
                ActiveParking active = new ActiveParking(vehicle, spot, level.getNumber(), entryTime);
                activeByPlate.put(vehicle.getLicensePlate(), active);
                return active.toSnapshot();
            }
        }
        throw new IllegalStateException("No available spot for vehicle type " + vehicle.getType());
    }

    // ---------------------------------------------------------------- exit

    /**
     * Exits a parked vehicle: releases its spot, records a receipt, and returns it.
     *
     * @throws IllegalArgumentException if the plate is blank, the exit time is
     *         null, the vehicle is not parked, or exit is before entry
     */
    public ParkingReceiptSnapshot exitVehicle(String licensePlate, LocalDateTime exitTime) {
        String plate = normalizePlate(licensePlate);
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time must not be null");
        }
        ActiveParking active = activeByPlate.get(plate);
        if (active == null) {
            throw new IllegalArgumentException("Vehicle is not parked: " + plate);
        }
        if (exitTime.isBefore(active.entryTime)) {
            throw new IllegalArgumentException("Exit time must not be before entry time");
        }

        VehicleType type = active.vehicle.getType();
        long billedHours = calculateBilledHours(active.entryTime, exitTime);
        BigDecimal rate = hourlyRate(type);
        BigDecimal fee = rate.multiply(BigDecimal.valueOf(billedHours));
        String receiptId = String.format("R%04d", nextReceiptNumber++);
        ParkingReceipt receipt = new ParkingReceipt(receiptId, active.vehicle.getLicensePlate(),
                type, active.spot.getId(), active.levelNumber, active.entryTime, exitTime,
                billedHours, rate, fee);

        active.spot.releaseVehicle();
        activeByPlate.remove(plate);
        receipts.add(receipt);
        return receipt.toSnapshot();
    }

    // ------------------------------------------------------------- fee policy

    /** Started-hour billing: any started hour is a full hour, minimum one hour. */
    public static long calculateBilledHours(LocalDateTime entry, LocalDateTime exit) {
        if (entry == null || exit == null) {
            throw new IllegalArgumentException("Entry and exit times must not be null");
        }
        long seconds = Duration.between(entry, exit).getSeconds();
        if (seconds < 0) {
            throw new IllegalArgumentException("Exit time must not be before entry time");
        }
        return Math.max(1L, (seconds + 3599L) / 3600L);
    }

    /** Fee for a vehicle type over a parking interval, using started-hour billing. */
    public static BigDecimal calculateFee(VehicleType type, LocalDateTime entry, LocalDateTime exit) {
        long billedHours = calculateBilledHours(entry, exit);
        return hourlyRate(type).multiply(BigDecimal.valueOf(billedHours));
    }

    // --------------------------------------------------------------- queries

    public Optional<ActiveParkingSnapshot> findVehicle(String licensePlate) {
        ActiveParking active = activeByPlate.get(normalizePlate(licensePlate));
        return active == null ? Optional.empty() : Optional.of(active.toSnapshot());
    }

    public boolean isVehicleParked(String licensePlate) {
        return activeByPlate.containsKey(normalizePlate(licensePlate));
    }

    public List<ActiveParkingSnapshot> listActiveParkings() {
        List<ActiveParkingSnapshot> views = new ArrayList<>();
        for (ActiveParking active : activeByPlate.values()) {
            views.add(active.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    public List<ParkingReceiptSnapshot> listReceipts() {
        List<ParkingReceiptSnapshot> views = new ArrayList<>();
        for (ParkingReceipt receipt : receipts) {
            views.add(receipt.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    // --------------------------------------------------------------- reports

    public int getTotalSpotCount() {
        int count = 0;
        for (ParkingLevel level : levels.values()) {
            count += level.getTotalSpotCount();
        }
        return count;
    }

    public int countAvailableSpots() {
        int count = 0;
        for (ParkingLevel level : levels.values()) {
            count += level.getAvailableSpotCount();
        }
        return count;
    }

    public int countAvailableSpots(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        int count = 0;
        for (ParkingLevel level : levels.values()) {
            count += level.getAvailableSpotCount(type);
        }
        return count;
    }

    public int countOccupiedSpots() {
        return getTotalSpotCount() - countAvailableSpots();
    }

    public int getParkedVehicleCount() {
        return activeByPlate.size();
    }

    /** Occupancy as a percentage in [0, 100]; 0 for an empty garage. */
    public double calculateCurrentOccupancyPercentage() {
        int total = getTotalSpotCount();
        if (total == 0) {
            return 0.0;
        }
        return (countOccupiedSpots() * 100.0) / total;
    }

    public BigDecimal calculateTotalRevenue() {
        BigDecimal total = BigDecimal.ZERO;
        for (ParkingReceipt receipt : receipts) {
            total = total.add(receipt.getFee());
        }
        return total;
    }

    /** Completed-receipt revenue grouped by vehicle type (only non-empty types). */
    public Map<VehicleType, BigDecimal> calculateRevenueByVehicleType() {
        Map<VehicleType, BigDecimal> totals = new EnumMap<>(VehicleType.class);
        for (ParkingReceipt receipt : receipts) {
            BigDecimal current = totals.getOrDefault(receipt.getVehicleType(), BigDecimal.ZERO);
            totals.put(receipt.getVehicleType(), current.add(receipt.getFee()));
        }
        return Collections.unmodifiableMap(totals);
    }

    // ------------------------------------------------------------------ helpers

    private static String normalizePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate must not be blank");
        }
        return licensePlate.trim().toUpperCase(Locale.ROOT);
    }

    /** Internal active-parking record; never exposed directly to callers. */
    private static final class ActiveParking {
        private final Vehicle vehicle;
        private final ParkingSpot spot;
        private final int levelNumber;
        private final LocalDateTime entryTime;

        ActiveParking(Vehicle vehicle, ParkingSpot spot, int levelNumber, LocalDateTime entryTime) {
            this.vehicle = vehicle;
            this.spot = spot;
            this.levelNumber = levelNumber;
            this.entryTime = entryTime;
        }

        ActiveParkingSnapshot toSnapshot() {
            return new ActiveParkingSnapshot(vehicle.getLicensePlate(), vehicle.getType(),
                    spot.getId(), levelNumber, entryTime);
        }
    }
}
