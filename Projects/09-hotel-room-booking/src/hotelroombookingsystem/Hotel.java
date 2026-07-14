package hotelroombookingsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Service layer for room, availability, booking, cancellation, and occupancy workflows. */
public final class Hotel {
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private int nextBookingNumber = 1;

    public void addRoom(Room room) {
        if (room == null) throw new IllegalArgumentException("Room must not be null");
        if (rooms.containsKey(room.getNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + room.getNumber());
        }
        rooms.put(room.getNumber(), new Room(room.getNumber(), room.getType(),
                room.getNightlyRate(), room.getCapacity()));
    }

    public RoomSnapshot getRoom(String roomNumber) {
        return new RoomSnapshot(requireRoom(roomNumber));
    }

    public List<RoomSnapshot> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        Booking.validateDateRange(checkIn, checkOut);
        List<RoomSnapshot> available = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (isRoomAvailable(room.getNumber(), checkIn, checkOut)) available.add(new RoomSnapshot(room));
        }
        available.sort(Comparator.comparing(RoomSnapshot::getNumber));
        return Collections.unmodifiableList(available);
    }

    public BookingSnapshot bookRoom(String roomNumber, Guest guest,
            LocalDate checkIn, LocalDate checkOut) {
        return bookRoom(roomNumber, guest, checkIn, checkOut, 1);
    }

    public BookingSnapshot bookRoom(String roomNumber, Guest guest,
            LocalDate checkIn, LocalDate checkOut, int guestCount) {
        Room room = requireRoom(roomNumber);
        if (guest == null) throw new IllegalArgumentException("Guest must not be null");
        Booking.validateDateRange(checkIn, checkOut);
        if (guestCount <= 0 || guestCount > room.getCapacity()) {
            throw new IllegalArgumentException("Guest count must be positive and within room capacity");
        }
        if (!isRoomAvailable(room.getNumber(), checkIn, checkOut)) {
            throw new IllegalStateException("Room is unavailable for the requested dates: " + room.getNumber());
        }
        String bookingId = String.format("B%04d", nextBookingNumber++);
        Booking booking = new Booking(bookingId, room, guest, checkIn, checkOut, guestCount);
        bookings.put(bookingId, booking);
        return new BookingSnapshot(booking);
    }

    public void cancelBooking(String bookingId) {
        requireBooking(bookingId).cancel();
    }

    public BookingSnapshot findBookingById(String bookingId) {
        return new BookingSnapshot(requireBooking(bookingId));
    }

    public double calculateOccupancy(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date must not be null");
        if (rooms.isEmpty()) return 0.0;
        int occupied = 0;
        for (Room room : rooms.values()) {
            if (!isRoomAvailable(room.getNumber(), date, date.plusDays(1))) occupied++;
        }
        return occupied * 100.0 / rooms.size();
    }

    public List<RoomSnapshot> listRooms() {
        List<RoomSnapshot> snapshots = new ArrayList<>();
        for (Room room : rooms.values()) snapshots.add(new RoomSnapshot(room));
        snapshots.sort(Comparator.comparing(RoomSnapshot::getNumber));
        return Collections.unmodifiableList(snapshots);
    }

    public List<BookingSnapshot> listBookings() {
        List<BookingSnapshot> snapshots = new ArrayList<>();
        for (Booking booking : bookings.values()) snapshots.add(new BookingSnapshot(booking));
        return Collections.unmodifiableList(snapshots);
    }

    private boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Booking booking : bookings.values()) {
            if (booking.getRoom().getNumber().equals(roomNumber) && booking.overlaps(checkIn, checkOut)) return false;
        }
        return true;
    }

    private Room requireRoom(String roomNumber) {
        String valid = requireText(roomNumber, "Room number");
        Room room = rooms.get(valid);
        if (room == null) throw new IllegalArgumentException("Unknown room number: " + valid);
        return room;
    }

    private Booking requireBooking(String bookingId) {
        String valid = requireText(bookingId, "Booking ID");
        Booking booking = bookings.get(valid);
        if (booking == null) throw new IllegalArgumentException("Unknown booking ID: " + valid);
        return booking;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) throw new IllegalArgumentException(fieldName + " must not be blank");
        return value.trim();
    }
}
