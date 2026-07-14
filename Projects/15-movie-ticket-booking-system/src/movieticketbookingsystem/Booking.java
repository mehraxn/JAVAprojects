package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * One booking record: who booked which seats of which showtime, for how much,
 * and when.
 *
 * <p>Everything except {@link #getStatus() status} is immutable. A booking starts
 * {@link BookingStatus#ACTIVE} and can be {@link #cancel() cancelled} exactly
 * once; a cancelled booking stays in history but holds no seats. The seat-ID list
 * is stored unmodifiable, so callers can never change it.
 */
public final class Booking {
    private final String bookingId;
    private final String showtimeId;
    private final String customerName;
    private final List<String> seatIds;
    private final BigDecimal totalPrice;
    private final LocalDateTime bookedAt;
    private BookingStatus status;

    public Booking(String bookingId, String showtimeId, String customerName,
                   List<String> seatIds, BigDecimal totalPrice, LocalDateTime bookedAt) {
        this.bookingId = requireText(bookingId, "Booking ID");
        this.showtimeId = requireText(showtimeId, "Showtime ID");
        this.customerName = requireText(customerName, "Customer name");
        if (seatIds == null) {
            throw new IllegalArgumentException("Seat IDs must not be null");
        }
        if (seatIds.isEmpty()) {
            throw new IllegalArgumentException("Seat IDs must not be empty");
        }
        List<String> validated = new ArrayList<>();
        Set<String> unique = new LinkedHashSet<>();
        for (String seatId : seatIds) {
            String trimmed = requireText(seatId, "Seat ID");
            if (!unique.add(trimmed)) {
                throw new IllegalArgumentException("Duplicate seat ID in booking: " + trimmed);
            }
            validated.add(trimmed);
        }
        if (totalPrice == null) {
            throw new IllegalArgumentException("Total price must not be null");
        }
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total price must not be negative");
        }
        if (bookedAt == null) {
            throw new IllegalArgumentException("Booked-at timestamp must not be null");
        }
        this.seatIds = Collections.unmodifiableList(validated);
        this.totalPrice = totalPrice;
        this.bookedAt = bookedAt;
        this.status = BookingStatus.ACTIVE;
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

    /** Marks this booking cancelled; rejects a second cancellation. */
    void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking already cancelled: " + bookingId);
        }
        status = BookingStatus.CANCELLED;
    }

    /** Immutable, read-only view of this booking at the current status. */
    public BookingSnapshot toSnapshot() {
        return new BookingSnapshot(bookingId, showtimeId, customerName, seatIds,
                totalPrice, bookedAt, status);
    }

    @Override
    public String toString() {
        return bookingId + " " + customerName + " " + seatIds + " " + totalPrice + " " + status;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
