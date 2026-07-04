package hotelroombookingsystem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private final String id;
    private final Room room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public Booking(String id, Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID must not be blank");
        }
        if (room == null || guest == null) {
            throw new IllegalArgumentException("Room and guest must not be null");
        }
        validateDateRange(checkIn, checkOut);
        this.id = id.trim();
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getId() { return id; }
    public Room getRoom() { return room; }
    public Guest getGuest() { return guest; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }

    public boolean overlaps(LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return start.isBefore(checkOut) && checkIn.isBefore(end);
    }

    public boolean includes(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        return !date.isBefore(checkIn) && date.isBefore(checkOut);
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public BigDecimal calculateTotalPrice() {
        return room.getNightlyRate().multiply(BigDecimal.valueOf(getNumberOfNights()));
    }

    static void validateDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out must not be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }
}
