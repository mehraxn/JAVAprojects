package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable record of a completed parking session and its computed fee.
 *
 * <p>Everything is fixed at construction and validated eagerly: exit time cannot
 * be before entry time, and the fee cannot be negative. Money uses
 * {@link BigDecimal}. Outside callers receive a {@link ParkingReceiptSnapshot}.
 */
public final class ParkingReceipt {
    private final String receiptId;
    private final String licensePlate;
    private final VehicleType vehicleType;
    private final String spotId;
    private final int levelNumber;
    private final LocalDateTime entryTime;
    private final LocalDateTime exitTime;
    private final long billedHours;
    private final BigDecimal hourlyRate;
    private final BigDecimal fee;

    public ParkingReceipt(String receiptId, String licensePlate, VehicleType vehicleType,
                          String spotId, int levelNumber, LocalDateTime entryTime,
                          LocalDateTime exitTime, long billedHours, BigDecimal hourlyRate,
                          BigDecimal fee) {
        this.receiptId = requireText(receiptId, "Receipt ID");
        this.licensePlate = requireText(licensePlate, "License plate");
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        this.spotId = requireText(spotId, "Spot ID");
        this.levelNumber = levelNumber;
        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time must not be null");
        }
        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time must not be null");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time must not be before entry time");
        }
        if (billedHours < 0) {
            throw new IllegalArgumentException("Billed hours must not be negative");
        }
        if (hourlyRate == null) {
            throw new IllegalArgumentException("Hourly rate must not be null");
        }
        if (fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parking fee must not be negative");
        }
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.billedHours = billedHours;
        this.hourlyRate = hourlyRate;
        this.fee = fee;
    }

    public String getReceiptId() { return receiptId; }
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getVehicleType() { return vehicleType; }
    public String getSpotId() { return spotId; }
    public int getLevelNumber() { return levelNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getBilledHours() { return billedHours; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public BigDecimal getFee() { return fee; }

    /** Immutable, read-only view of this receipt. */
    public ParkingReceiptSnapshot toSnapshot() {
        return new ParkingReceiptSnapshot(receiptId, licensePlate, vehicleType, spotId,
                levelNumber, entryTime, exitTime, billedHours, hourlyRate, fee);
    }

    @Override
    public String toString() {
        return receiptId + " " + licensePlate + " (" + vehicleType + ") spot " + spotId
                + " L" + levelNumber + " " + billedHours + "h fee " + fee.toPlainString();
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
