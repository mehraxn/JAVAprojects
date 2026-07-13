package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Immutable, read-only view of a {@link Booking}. The seat-ID list is
 * unmodifiable, so holding a snapshot cannot change any booking or seat state.
 */
public final class BookingSnapshot {
    private final String bookingId;
    private final String showtimeId;
    private final String customerName;
    private final List<String> seatIds;
    private final BigDecimal totalPrice;
    private final LocalDateTime bookedAt;
    private final BookingStatus status;

    BookingSnapshot(String bookingId, String showtimeId, String customerName,
                    List<String> seatIds, BigDecimal totalPrice,
                    LocalDateTime bookedAt, BookingStatus status) {
        this.bookingId = bookingId;
        this.showtimeId = showtimeId;
        this.customerName = customerName;
        this.seatIds = Collections.unmodifiableList(new java.util.ArrayList<>(seatIds));
        this.totalPrice = totalPrice;
        this.bookedAt = bookedAt;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public String getShowtimeId() { return showtimeId; }
    public String getCustomerName() { return customerName; }
    public List<String> getSeatIds() { return seatIds; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public BookingStatus getStatus() { return status; }

    public boolean isActive() { return status == BookingStatus.ACTIVE; }
    public boolean isCancelled() { return status == BookingStatus.CANCELLED; }

    @Override
    public String toString() {
        return bookingId + " " + customerName + " " + seatIds + " " + totalPrice + " " + status;
    }
}
