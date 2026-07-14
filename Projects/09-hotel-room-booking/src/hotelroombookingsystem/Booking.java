package hotelroombookingsystem;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class Booking {
    private final String id;
    private final Room room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final int guestCount;
    private BookingStatus status;

    public Booking(String id, Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        this(id, room, guest, checkIn, checkOut, 1);
    }

    public Booking(String id, Room room, Guest guest, LocalDate checkIn,
            LocalDate checkOut, int guestCount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Booking ID must not be blank");
        }
        if (room == null || guest == null) {
            throw new IllegalArgumentException("Room and guest must not be null");
        }
        validateDateRange(checkIn, checkOut);
        if (guestCount <= 0) {
            throw new IllegalArgumentException("Guest count must be greater than zero");
        }
        if (guestCount > room.getCapacity()) {
            throw new IllegalArgumentException("Guest count exceeds room capacity");
        }
        this.id = id.trim();
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestCount = guestCount;
        this.status = BookingStatus.ACTIVE;
    }

    public String getId() { return id; }
    public Room getRoom() { return room; }
    public Guest getGuest() { return guest; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public int getGuestCount() { return guestCount; }
    public BookingStatus getStatus() { return status; }

    public boolean overlaps(LocalDate start, LocalDate end) {
        validateDateRange(start, end);
        return status == BookingStatus.ACTIVE
                && start.isBefore(checkOut) && checkIn.isBefore(end);
    }

    public boolean includes(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        return status == BookingStatus.ACTIVE
                && !date.isBefore(checkIn) && date.isBefore(checkOut);
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public BigDecimal calculateTotalPrice() {
        return room.getNightlyRate().multiply(BigDecimal.valueOf(getNumberOfNights()));
    }

    void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled: " + id);
        }
        status = BookingStatus.CANCELLED;
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
