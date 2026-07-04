# Train Ticket Reservation System

An in-memory Java application for routes, trains, seats, and passenger reservations.

## Implemented features

- Add routes with unique IDs and different origin/destination stations.
- Add trains with unique IDs, registered routes, and uniquely numbered seats.
- Reserve a requested seat or automatically choose the first available seat.
- Prevent duplicate reservation of the same seat.
- Cancel reservations and release their seats.
- Show available seats and active reservations for a train.
- Search trips by exact origin and destination, case-insensitively.

## Structure

- `Route` represents an origin/destination pair.
- `Seat` owns its reservation state.
- `Train` groups a route and numbered seats.
- `Reservation` is an immutable passenger reservation.
- `ReservationSystem` manages routes, trains, and reservations.
- `Main` demonstrates route search, reservation, and cancellation.

Source files are under `src/trainticketreservationsystem` and use only standard Java.

## Run

```powershell
javac -d out src\trainticketreservationsystem\*.java
java -cp out trainticketreservationsystem.Main
```

See `TESTING.md` for manual test cases.
