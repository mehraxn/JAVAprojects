package hotelroombookingsystem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booking {
    private final String id;
    private final Room room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public Booking(String id, Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getId() { return id; }

    public boolean overlaps(LocalDate start, LocalDate end) {
        // TODO: Determine whether the supplied date range overlaps this booking.
        throw new UnsupportedOperationException("TODO: check date overlap");
    }

    public BigDecimal calculateTotalPrice() {
        // TODO: Multiply the number of nights by the room rate.
        throw new UnsupportedOperationException("TODO: calculate booking price");
    }
}
