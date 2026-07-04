package hotelroombookingsystem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Hotel hotel = new Hotel();
        hotel.addRoom(new Room("101", "Single", new BigDecimal("80.00")));
        hotel.addRoom(new Room("201", "Double", new BigDecimal("125.00")));

        LocalDate checkIn = LocalDate.of(2026, 8, 10);
        LocalDate checkOut = LocalDate.of(2026, 8, 13);
        Booking booking = hotel.bookRoom(
                "201", new Guest("G001", "Nora"), checkIn, checkOut);

        System.out.println("Booking " + booking.getId() + " total: "
                + booking.calculateTotalPrice());
        System.out.println("Available rooms during stay: "
                + hotel.findAvailableRooms(checkIn, checkOut).size());
        System.out.printf("Occupancy on check-in date: %.1f%%%n",
                hotel.calculateOccupancy(checkIn));

        hotel.cancelBooking(booking.getId());
        System.out.println("Available rooms after cancellation: "
                + hotel.findAvailableRooms(checkIn, checkOut).size());
    }
}
