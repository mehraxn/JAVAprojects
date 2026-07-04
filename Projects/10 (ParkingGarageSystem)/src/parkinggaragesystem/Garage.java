package parkinggaragesystem;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Garage {
    public static final BigDecimal HOURLY_RATE = new BigDecimal("5.00");

    private final List<ParkingLevel> levels = new ArrayList<>();
    private final Map<String, ParkingRecord> activeParking = new LinkedHashMap<>();

    public void addLevel(ParkingLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Parking level must not be null");
        }
        for (ParkingLevel existingLevel : levels) {
            if (existingLevel.getNumber() == level.getNumber()) {
                throw new IllegalArgumentException("Parking level already exists: " + level.getNumber());
            }
            for (ParkingSpot newSpot : level.getSpots()) {
                for (ParkingSpot existingSpot : existingLevel.getSpots()) {
                    if (existingSpot.getId().equals(newSpot.getId())) {
                        throw new IllegalArgumentException(
                                "Parking spot ID already exists in the garage: " + newSpot.getId());
                    }
                }
            }
        }
        levels.add(level);
    }

    public ParkingSpot parkVehicle(Vehicle vehicle) {
        return parkVehicle(vehicle, LocalDateTime.now());
    }

    public ParkingSpot parkVehicle(Vehicle vehicle, LocalDateTime arrival) {
        if (vehicle == null || arrival == null) {
            throw new IllegalArgumentException("Vehicle and arrival time must not be null");
        }
        if (activeParking.containsKey(vehicle.getLicensePlate())) {
            throw new IllegalStateException("Vehicle is already parked: " + vehicle.getLicensePlate());
        }

        ParkingSpot availableSpot = null;
        for (ParkingLevel level : levels) {
            availableSpot = level.findAvailableSpot(vehicle.getType());
            if (availableSpot != null) {
                break;
            }
        }
        if (availableSpot == null) {
            throw new IllegalStateException("No available spot for vehicle type " + vehicle.getType());
        }

        availableSpot.assignVehicle(vehicle);
        activeParking.put(vehicle.getLicensePlate(),
                new ParkingRecord(vehicle, availableSpot, arrival));
        return availableSpot;
    }

    public Vehicle removeVehicle(String licensePlate) {
        return exitVehicle(licensePlate, LocalDateTime.now()).getVehicle();
    }

    public ParkingReceipt exitVehicle(String licensePlate, LocalDateTime departure) {
        String normalizedPlate = normalizePlate(licensePlate);
        if (departure == null) {
            throw new IllegalArgumentException("Departure time must not be null");
        }
        ParkingRecord record = activeParking.get(normalizedPlate);
        if (record == null) {
            throw new IllegalArgumentException("Vehicle is not parked: " + normalizedPlate);
        }

        BigDecimal fee = calculateFee(record.arrival, departure);
        record.spot.releaseVehicle();
        activeParking.remove(normalizedPlate);
        return new ParkingReceipt(record.vehicle, record.spot.getId(), record.arrival, departure, fee);
    }

    public BigDecimal calculateFee(LocalDateTime arrival, LocalDateTime departure) {
        if (arrival == null || departure == null) {
            throw new IllegalArgumentException("Arrival and departure times must not be null");
        }
        long parkedSeconds = Duration.between(arrival, departure).getSeconds();
        if (parkedSeconds < 0) {
            throw new IllegalArgumentException("Departure must not be before arrival");
        }
        long billedHours = Math.max(1L, (parkedSeconds + 3599L) / 3600L);
        return HOURLY_RATE.multiply(BigDecimal.valueOf(billedHours));
    }

    public int getAvailableSpotCount() {
        int count = 0;
        for (ParkingLevel level : levels) {
            count += level.getAvailableSpotCount();
        }
        return count;
    }

    public int getAvailableSpotCount(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        int count = 0;
        for (ParkingLevel level : levels) {
            count += level.getAvailableSpotCount(type);
        }
        return count;
    }

    public int getParkedVehicleCount() {
        return activeParking.size();
    }

    public boolean isVehicleParked(String licensePlate) {
        return activeParking.containsKey(normalizePlate(licensePlate));
    }

    private static String normalizePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("License plate must not be blank");
        }
        return licensePlate.trim().toUpperCase(Locale.ROOT);
    }

    private static class ParkingRecord {
        private final Vehicle vehicle;
        private final ParkingSpot spot;
        private final LocalDateTime arrival;

        private ParkingRecord(Vehicle vehicle, ParkingSpot spot, LocalDateTime arrival) {
            this.vehicle = vehicle;
            this.spot = spot;
            this.arrival = arrival;
        }
    }
}
