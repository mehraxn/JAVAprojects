package hotelroombookingsystem;

import java.math.BigDecimal;

public final class Room {
    private final String number;
    private final String type;
    private final BigDecimal nightlyRate;
    private final int capacity;

    public Room(String number, String type, BigDecimal nightlyRate) {
        this(number, type, nightlyRate, 1);
    }

    public Room(String number, String type, BigDecimal nightlyRate, int capacity) {
        this.number = requireText(number, "Room number");
        this.type = requireText(type, "Room type");
        if (nightlyRate == null || nightlyRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Nightly rate must be greater than zero");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Room capacity must be greater than zero");
        }
        this.nightlyRate = nightlyRate;
        this.capacity = capacity;
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public int getCapacity() { return capacity; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
