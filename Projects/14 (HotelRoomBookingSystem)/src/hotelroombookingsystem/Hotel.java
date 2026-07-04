package hotelroombookingsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private int nextBookingNumber = 1;

    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room must not be null");
        }
        if (rooms.containsKey(room.getNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + room.getNumber());
        }
        rooms.put(room.getNumber(), room);
    }

    public Room getRoom(String roomNumber) {
        String validNumber = requireText(roomNumber, "Room number");
        Room room = rooms.get(validNumber);
        if (room == null) {
            throw new IllegalArgumentException("Unknown room number: " + validNumber);
        }
        return room;
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        Booking.validateDateRange(checkIn, checkOut);
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms.values()) {
            if (isRoomAvailable(room.getNumber(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        availableRooms.sort(Comparator.comparing(Room::getNumber));
        return Collections.unmodifiableList(availableRooms);
    }

    public Booking bookRoom(String roomNumber, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        Room room = getRoom(roomNumber);
        if (guest == null) {
            throw new IllegalArgumentException("Guest must not be null");
        }
        Booking.validateDateRange(checkIn, checkOut);
        if (!isRoomAvailable(room.getNumber(), checkIn, checkOut)) {
            throw new IllegalStateException("Room is unavailable for the requested dates: "
                    + room.getNumber());
        }

        String bookingId = String.format("B%04d", nextBookingNumber++);
        Booking booking = new Booking(bookingId, room, guest, checkIn, checkOut);
        bookings.put(bookingId, booking);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        String validId = requireText(bookingId, "Booking ID");
        if (bookings.remove(validId) == null) {
            throw new IllegalArgumentException("Unknown booking ID: " + validId);
        }
    }

    public double calculateOccupancy(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        if (rooms.isEmpty()) {
            return 0.0;
        }
        int occupiedRooms = 0;
        for (Booking booking : bookings.values()) {
            if (booking.includes(date)) {
                occupiedRooms++;
            }
        }
        return occupiedRooms * 100.0 / rooms.size();
    }

    public List<Room> listRooms() {
        return Collections.unmodifiableList(new ArrayList<>(rooms.values()));
    }

    public List<Booking> listBookings() {
        return Collections.unmodifiableList(new ArrayList<>(bookings.values()));
    }

    private boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        for (Booking booking : bookings.values()) {
            if (booking.getRoom().getNumber().equals(roomNumber)
                    && booking.overlaps(checkIn, checkOut)) {
                return false;
            }
        }
        return true;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
