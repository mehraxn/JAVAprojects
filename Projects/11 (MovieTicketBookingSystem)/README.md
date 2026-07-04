# Movie Ticket Booking System

An in-memory Java application for movies, showtimes, seats, and bookings.

## Implemented features

- Add movies with unique IDs.
- Add showtimes for registered movies.
- Configure uniquely labelled seats for each showtime.
- Book one or more seats in a single validated operation.
- Prevent duplicate labels within a request and double-booking across bookings.
- Cancel bookings and release their seats.
- Show all available seats for a showtime.
- Calculate a simple total using a fixed `12.00` price per seat.

## Structure

- `Movie` stores movie details.
- `Seat` owns its booked/available state.
- `Showtime` connects a movie, start time, and seat map.
- `Booking` is an immutable booking summary.
- `BookingSystem` manages movies, showtimes, and bookings.
- `Main` demonstrates booking and cancellation.

Source files are under `src/movieticketbookingsystem` and use only standard Java.

## Run

```powershell
javac -d out src\movieticketbookingsystem\*.java
java -cp out movieticketbookingsystem.Main
```

See `TESTING.md` for manual test cases.
