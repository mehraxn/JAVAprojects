# Testing Event Registration System

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add multiple events with different names, dates, categories, and capacities.
2. Register participants until capacity is reached and verify available places decrease.
3. Try registering another participant when full; expect `IllegalStateException` and no overbooking.
4. Register the same attendee twice for one event; expect `IllegalArgumentException`.
5. Register the same attendee for two different events; verify both registrations succeed.
6. Cancel a registration and verify capacity becomes available again.
7. Cancel an unknown attendee registration; expect `IllegalArgumentException`.
8. Verify participant and registration lists contain the expected records.
9. Search by partial event name with different letter case.
10. Search by exact event date and verify all events on that date are returned.
11. Search by partial category with different letter case.
12. Verify search results are ordered by date and then name.
13. Add a duplicate event ID, zero capacity, or negative capacity; expect `IllegalArgumentException`.
14. Use blank attendee fields, invalid email, null date, or unknown event ID; expect `IllegalArgumentException`.
15. Try modifying returned event, participant, or registration lists; expect `UnsupportedOperationException`.
