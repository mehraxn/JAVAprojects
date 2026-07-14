package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Immutable, read-only view of a {@link ParkingReceipt}. */
public final class ParkingReceiptSnapshot {
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

    ParkingReceiptSnapshot(String receiptId, String licensePlate, VehicleType vehicleType,
                           String spotId, int levelNumber, LocalDateTime entryTime,
                           LocalDateTime exitTime, long billedHours, BigDecimal hourlyRate,
                           BigDecimal fee) {
        this.receiptId = receiptId;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.levelNumber = levelNumber;
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

    @Override
    public String toString() {
        return receiptId + " " + licensePlate + " (" + vehicleType + ") spot " + spotId
                + " L" + levelNumber + " " + billedHours + "h fee " + fee.toPlainString();
    }
}
