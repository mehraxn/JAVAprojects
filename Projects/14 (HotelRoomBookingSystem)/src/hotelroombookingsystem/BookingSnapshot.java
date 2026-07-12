package hotelroombookingsystem;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class BookingSnapshot {
    private final String id;
    private final RoomSnapshot room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final long nights;
    private final BigDecimal totalPrice;
    private final int guestCount;
    private final BookingStatus status;

    BookingSnapshot(Booking booking) {
        this.id = booking.getId();
        this.room = new RoomSnapshot(booking.getRoom());
        Guest source = booking.getGuest();
        this.guest = new Guest(source.getId(), source.getName());
        this.checkIn = booking.getCheckIn();
        this.checkOut = booking.getCheckOut();
        this.nights = booking.getNumberOfNights();
        this.totalPrice = booking.calculateTotalPrice();
        this.guestCount = booking.getGuestCount();
        this.status = booking.getStatus();
    }

    public String getId() { return id; }
    public RoomSnapshot getRoom() { return room; }
    public Guest getGuest() { return guest; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public long getNumberOfNights() { return nights; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public int getGuestCount() { return guestCount; }
    public BookingStatus getStatus() { return status; }
}
