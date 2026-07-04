package hotelroombookingsystem;

import java.math.BigDecimal;

public class Room {
    private final String number;
    private final String type;
    private final BigDecimal nightlyRate;

    public Room(String number, String type, BigDecimal nightlyRate) {
        this.number = requireText(number, "Room number");
        this.type = requireText(type, "Room type");
        if (nightlyRate == null || nightlyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Nightly rate must not be negative");
        }
        this.nightlyRate = nightlyRate;
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public BigDecimal getNightlyRate() { return nightlyRate; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
