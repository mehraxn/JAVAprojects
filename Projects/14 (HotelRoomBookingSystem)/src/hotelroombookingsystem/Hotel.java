package hotelroombookingsystem;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hotel {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();

    public void addRoom(Room room) {
        // TODO: Validate and add a room with a unique number.
        throw new UnsupportedOperationException("TODO: add a room");
    }

    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        // TODO: Validate the dates and exclude rooms with overlapping bookings.
        throw new UnsupportedOperationException("TODO: find available rooms");
    }

    public Booking bookRoom(String roomNumber, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        // TODO: Check availability and create a booking.
        throw new UnsupportedOperationException("TODO: book a room");
    }

    public void cancelBooking(String bookingId) {
        // TODO: Remove the requested booking.
        throw new UnsupportedOperationException("TODO: cancel a booking");
    }

    public double calculateOccupancy(LocalDate date) {
        // TODO: Calculate the occupied-room percentage for the date.
        throw new UnsupportedOperationException("TODO: calculate occupancy");
    }
}
