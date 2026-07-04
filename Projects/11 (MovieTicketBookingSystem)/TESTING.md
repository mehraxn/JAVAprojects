# Testing Movie Ticket Booking System

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add a movie and a showtime with several seats; verify both appear in their system lists.
2. Book one seat and verify it disappears from the available-seat list.
3. Book several distinct seats together and verify the total equals `12.00` per seat.
4. Try booking an already booked seat; expect `IllegalStateException` and no other seat changes.
5. Include the same seat label twice in one request; expect `IllegalArgumentException`.
6. Include one valid and one unknown seat; expect `IllegalArgumentException` and verify the valid seat remains available.
7. Cancel a booking and verify all its seats become available again.
8. Cancel an unknown booking; expect `IllegalArgumentException`.
9. Add duplicate movie, showtime, or seat IDs/labels; expect `IllegalArgumentException`.
10. Add a showtime for an unregistered movie or with no seats; expect `IllegalArgumentException`.
11. Try invalid movie duration or non-positive seat coordinates; expect `IllegalArgumentException`.
12. Verify the seat-label list returned by a booking cannot be modified.
