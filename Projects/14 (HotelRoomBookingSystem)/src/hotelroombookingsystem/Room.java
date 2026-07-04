package hotelroombookingsystem;

import java.math.BigDecimal;

public class Room {
    private final String number;
    private final String type;
    private final BigDecimal nightlyRate;

    public Room(String number, String type, BigDecimal nightlyRate) {
        this.number = number;
        this.type = type;
        this.nightlyRate = nightlyRate;
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
}
