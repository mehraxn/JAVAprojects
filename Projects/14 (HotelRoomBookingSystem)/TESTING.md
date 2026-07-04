# Testing Hotel Room Booking System

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add several rooms with unique numbers and verify `listRooms()`.
2. Search dates with no bookings and verify every room is available.
3. Book a room for three nights and verify total price is three times the nightly rate.
4. Search the booked range and verify the booked room is excluded.
5. Try an overlapping range starting before, during, or inside an existing stay; expect `IllegalStateException`.
6. Book a new stay beginning exactly on the previous check-out date; verify it succeeds.
7. Cancel a booking and verify the room becomes available for that range.
8. Cancel an unknown booking; expect `IllegalArgumentException`.
9. Use equal check-in/check-out dates or check-out before check-in; expect `IllegalArgumentException`.
10. Use null dates, guest, or room data; expect `IllegalArgumentException`.
11. Add a duplicate room number or negative nightly rate; expect `IllegalArgumentException`.
12. Calculate occupancy with no rooms; expect `0.0`.
13. Verify occupancy includes check-in dates but excludes check-out dates.
14. Try modifying returned room, booking, or availability lists; expect `UnsupportedOperationException`.
