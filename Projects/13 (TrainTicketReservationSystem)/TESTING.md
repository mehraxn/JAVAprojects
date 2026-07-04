# Testing Train Ticket Reservation System

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add a route and a train with several unique seats; verify both system lists.
2. Reserve a specific seat and verify it is removed from the available-seat list.
3. Reserve without specifying a seat and verify the first available seat is chosen.
4. Try reserving an already reserved seat; expect `IllegalStateException`.
5. Include one seat only once and verify one active reservation is created.
6. Cancel a reservation and verify its exact seat becomes available again.
7. Cancel an unknown reservation; expect `IllegalArgumentException`.
8. Fill every seat and request another automatic reservation; expect `IllegalStateException`.
9. Search using different letter case for origin and destination; verify matching trains are returned.
10. Search the reverse route or a different destination; verify no match.
11. Add duplicate route, train, or seat IDs/numbers; expect `IllegalArgumentException`.
12. Add a train for an unregistered route or a train with no seats; expect `IllegalArgumentException`.
13. Try a blank passenger name, unknown train, unknown seat, or non-positive seat number; expect `IllegalArgumentException`.
14. Create a route with the same origin and destination; expect `IllegalArgumentException`.
15. Try modifying returned route, train, seat, or reservation lists; expect `UnsupportedOperationException`.
