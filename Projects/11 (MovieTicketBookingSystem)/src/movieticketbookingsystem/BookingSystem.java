package movieticketbookingsystem;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * In-memory service layer for showtimes, bookings, cancellations, and seat
 * availability. This is the only class outside callers use to change state.
 *
 * <p>Every query returns immutable snapshots ({@link ShowtimeSnapshot},
 * {@link SeatSnapshot}, {@link BookingSnapshot}) in unmodifiable lists, so live
 * {@link Seat}/{@link Showtime}/{@link Booking} objects are never leaked.
 *
 * <h2>Behaviour notes</h2>
 * <ul>
 *   <li>Booking is <strong>all-or-nothing</strong>: if any requested seat is
 *       unknown, unavailable, or duplicated, nothing is reserved and no booking
 *       is created.</li>
 *   <li>Booking IDs are generated deterministically as {@code B0001},
 *       {@code B0002}, …</li>
 *   <li>Booking timestamps come from an injectable {@link Clock}, so tests can
 *       pin them to a fixed instant.</li>
 *   <li>Cancelling releases exactly the booked seats and marks the booking
 *       {@link BookingStatus#CANCELLED}, keeping it in history.</li>
 *   <li>Failed operations leave all state unchanged.</li>
 * </ul>
 */
public final class BookingSystem {

    private final Clock clock;
    private final Map<String, Showtime> showtimes = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private int nextBookingNumber = 1;

    /** Uses the system clock for booking timestamps. */
    public BookingSystem() {
        this(Clock.systemDefaultZone());
    }

    /** Uses the supplied clock for booking timestamps (deterministic in tests). */
    public BookingSystem(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    // --------------------------------------------------------- showtime management

    public void addShowtime(Showtime showtime) {
        if (showtime == null) {
            throw new IllegalArgumentException("Showtime must not be null");
        }
        if (showtimes.containsKey(showtime.getShowtimeId())) {
            throw new IllegalArgumentException(
                    "Showtime ID already exists: " + showtime.getShowtimeId());
        }
        showtimes.put(showtime.getShowtimeId(), showtime);
    }

    public List<ShowtimeSnapshot> listShowtimes() {
        List<ShowtimeSnapshot> views = new ArrayList<>();
        for (Showtime showtime : showtimes.values()) {
            views.add(showtime.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    /** Snapshot of a showtime, or {@link Optional#empty()} if unknown. */
    public Optional<ShowtimeSnapshot> getShowtime(String showtimeId) {
        Showtime showtime = showtimes.get(requireText(showtimeId, "Showtime ID"));
        return showtime == null ? Optional.empty() : Optional.of(showtime.toSnapshot());
    }

    /** Snapshots of every available seat in a showtime, in seat-map order. */
    public List<SeatSnapshot> getAvailableSeats(String showtimeId) {
        return Collections.unmodifiableList(requireShowtime(showtimeId).getAvailableSeatSnapshots());
    }

    // ------------------------------------------------------------------- booking

    /**
     * Books one or more seats as a single all-or-nothing transaction.
     *
     * @return a snapshot of the created booking
     * @throws IllegalArgumentException if the showtime, customer name, or any seat
     *         is invalid, or a seat ID is duplicated in the request
     * @throws IllegalStateException if any requested seat is already reserved
     */
    public BookingSnapshot bookSeats(String showtimeId, String customerName, List<String> seatIds) {
        Showtime showtime = requireShowtime(showtimeId);
        String customer = requireText(customerName, "Customer name");
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected");
        }

        // Validate everything up front so nothing is reserved unless all checks pass.
        Set<String> requested = new LinkedHashSet<>();
        List<Seat> selected = new ArrayList<>();
        for (String rawSeatId : seatIds) {
            String seatId = requireText(rawSeatId, "Seat ID");
            if (!requested.add(seatId)) {
                throw new IllegalArgumentException("Duplicate seat in request: " + seatId);
            }
            Seat seat = showtime.findSeat(seatId);
            if (seat == null) {
                throw new IllegalArgumentException("Unknown seat: " + seatId);
            }
            if (!seat.isAvailable()) {
                throw new IllegalStateException("Seat already reserved: " + seatId);
            }
            selected.add(seat);
        }

        // All checks passed — commit.
        for (Seat seat : selected) {
            seat.reserve();
        }
        String bookingId = String.format("B%04d", nextBookingNumber++);
        List<String> bookedSeatIds = new ArrayList<>();
        for (Seat seat : selected) {
            bookedSeatIds.add(seat.getSeatId());
        }
        BigDecimal totalPrice = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(selected.size()));
        Booking booking = new Booking(bookingId, showtime.getShowtimeId(), customer,
                bookedSeatIds, totalPrice, LocalDateTime.now(clock));
        bookings.put(bookingId, booking);
        return booking.toSnapshot();
    }

    // -------------------------------------------------------------- cancellation

    /**
     * Cancels an active booking: releases exactly its seats and marks it
     * {@link BookingStatus#CANCELLED}. The booking stays in history.
     *
     * @throws IllegalArgumentException if the booking ID is unknown
     * @throws IllegalStateException if the booking is already cancelled
     */
    public BookingSnapshot cancelBooking(String bookingId) {
        Booking booking = bookings.get(requireText(bookingId, "Booking ID"));
        if (booking == null) {
            throw new IllegalArgumentException("Unknown booking ID: " + bookingId.trim());
        }
        if (booking.isCancelled()) {
            throw new IllegalStateException("Booking already cancelled: " + booking.getBookingId());
        }
        Showtime showtime = showtimes.get(booking.getShowtimeId());
        // Release the exact seats this booking holds.
        for (String seatId : booking.getSeatIds()) {
            Seat seat = showtime.findSeat(seatId);
            seat.release();
        }
        booking.cancel();
        return booking.toSnapshot();
    }

    // --------------------------------------------------------------- booking queries

    public List<BookingSnapshot> listBookings() {
        List<BookingSnapshot> views = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            views.add(booking.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    public Optional<BookingSnapshot> findBookingById(String bookingId) {
        Booking booking = bookings.get(requireText(bookingId, "Booking ID"));
        return booking == null ? Optional.empty() : Optional.of(booking.toSnapshot());
    }

    // ------------------------------------------------------------------ helpers

    private Showtime requireShowtime(String showtimeId) {
        Showtime showtime = showtimes.get(requireText(showtimeId, "Showtime ID"));
        if (showtime == null) {
            throw new IllegalArgumentException("Unknown showtime ID: " + showtimeId.trim());
        }
        return showtime;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
