# Movie Ticket Booking System

## Overview

An educational, dependency-free Java project that models a movie ticket booking
system. It focuses on clean object-oriented design, a service layer with real
business rules, seat maps, all-or-nothing multi-seat booking, cancellation,
`BigDecimal` ticket pricing, defensive data exposure, and dependency-free
automated tests — not on a database, web API, or payment integration.

## What This Project Demonstrates

- **Movie** domain model (ID, title, genre, duration)
- **Showtime** domain model (movie, start time, ticket price, seat map)
- **Seat** domain model (seat ID, availability)
- **Booking** records with `ACTIVE` / `CANCELLED` status
- **BookingSystem** service layer that owns all state changes
- showtime seat map and availability
- single-seat and multi-seat booking
- **all-or-nothing** booking validation
- duplicate-seat request prevention
- already-booked seat prevention
- full-showtime rejection
- cancellation and exact seat release
- booking history retained after cancellation
- inventory of bookings with deterministic IDs (`B0001`, `B0002`, …)
- `BigDecimal` ticket pricing (never `double`)
- deterministic booking timestamps via an injectable `Clock`
- defensive snapshots so internal state cannot be mutated from outside
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Add showtimes and reject duplicate showtime IDs.
- Book one or several seats in a single all-or-nothing transaction.
- Reject duplicate seat requests, unknown seats, and already-booked seats.
- Reject booking into a full showtime.
- Cancel a booking, releasing exactly its seats; rebook released seats.
- Keep cancelled bookings in history with `CANCELLED` status.
- Report available seats before and after booking/cancellation.
- Total price = ticket price × number of seats, using `BigDecimal`.
- CLI demos for every feature area.

## Main classes

| Class | Responsibility |
|---|---|
| `Movie` | Immutable movie: ID, title, genre, duration. |
| `Seat` | One seat and its availability; only `BookingSystem` changes it. |
| `Showtime` | One screening: movie, start time, ticket price, unique-seat map. |
| `Booking` | Immutable booking record; status changes only to `CANCELLED`. |
| `BookingStatus` | Enum: `ACTIVE`, `CANCELLED`. |
| `BookingSystem` | Service layer: showtimes, booking, cancellation, availability. |
| `MovieSnapshot` / `SeatSnapshot` / `ShowtimeSnapshot` / `BookingSnapshot` | Immutable read-only views returned to callers. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **Booking is all-or-nothing.** If any requested seat is unknown, already
  reserved, or duplicated in the request, **no** seats are reserved and **no**
  booking is created — state is left unchanged.
- **Cancellation releases exactly the booked seats** and marks the booking
  `CANCELLED`. Cancelled bookings remain in history; cancelling twice is rejected.
- **Booking IDs are deterministic**: `B0001`, `B0002`, …
- **Booking timestamps** come from an injectable `java.time.Clock`, so they are
  reproducible in tests and demos.
- **Money uses `BigDecimal`** (never `double`); the ticket price must be greater
  than zero, comparisons use `compareTo`, and user-facing output shows 2 decimals.
- **Public methods return immutable snapshots in unmodifiable lists.** Live
  `Seat` / `Showtime` / `Booking` objects are never leaked, so external code
  cannot reserve seats or corrupt bookings without going through `BookingSystem`.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external dependencies.
- `BigDecimal` for ticket pricing and injectable `Clock` for deterministic booking time.
- Dependency-free tests plus Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/movieticketbookingsystem/     # Movies, showtimes, seats, bookings, CLI
├── tests/movieticketbookingsystem/   # Custom automated test suite
├── scripts/                           # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

Compile:

~~~
javac -Xlint:all -Werror -d out src/movieticketbookingsystem/*.java
~~~

Run the CLI commands:

~~~
java -cp out movieticketbookingsystem.Main help
java -cp out movieticketbookingsystem.Main demo
java -cp out movieticketbookingsystem.Main booking-demo
java -cp out movieticketbookingsystem.Main cancellation-demo
java -cp out movieticketbookingsystem.Main full-showtime-demo
java -cp out movieticketbookingsystem.Main availability-demo
java -cp out movieticketbookingsystem.Main validation-demo
~~~

## Testing

The project ships with a dependency-free test suite (custom assertion helper and
runner — no JUnit, Maven, or Gradle). Run everything with:

~~~
bash scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md)
for the latest recorded run.

## Known Limitations

This is a learning project. It intentionally has:

- no database (in-memory only)
- no HTTP API
- no login/authentication
- no payment provider
- no real cinema integration
- no ticket PDF/QR generation
- no seat-map UI
- no production booking guarantees
- no production deployment

## Resume Value

Built a dependency-free Java movie-booking system with atomic multi-seat reservation, cancellation and seat release, exact pricing, deterministic timestamps, defensive snapshots, CLI demonstrations, and automated tests.
