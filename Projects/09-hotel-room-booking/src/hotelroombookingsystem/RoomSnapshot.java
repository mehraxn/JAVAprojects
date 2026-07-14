package hotelroombookingsystem;

import java.math.BigDecimal;

public final class RoomSnapshot {
    private final String number;
    private final String type;
    private final BigDecimal nightlyRate;
    private final int capacity;

    RoomSnapshot(Room room) {
        this.number = room.getNumber();
        this.type = room.getType();
        this.nightlyRate = room.getNightlyRate();
        this.capacity = room.getCapacity();
    }

    public String getNumber() { return number; }
    public String getType() { return type; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public int getCapacity() { return capacity; }
}
