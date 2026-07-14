# Train Ticket Reservation System

## Overview

An educational, dependency-free Java project that models a train ticket
reservation service. It focuses on clean object-oriented design, a service
layer with real business rules, defensive data exposure, and dependency-free
automated tests — not on a database, web API, or real railway integration.

## What This Project Demonstrates

- **Route** domain model (origin/destination, case-insensitive matching)
- **Seat** domain model (numbered seat with availability state)
- **Train** domain model (a scheduled service = route + unique seats)
- **Reservation** records (immutable details + `ACTIVE`/`CANCELLED` status)
- **ReservationSystem** service layer that owns all state changes
- specific seat reservation
- automatic first-available seat reservation
- double-booking prevention
- full-train rejection
- cancellation and seat release
- route search
- defensive snapshots/copies so internal state cannot be mutated from outside
- deterministic reservation timestamps via an injectable `Clock`
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Create/register routes and reject duplicate route IDs and duplicate station pairs.
- Create/register trains that reference a registered route by value.
- Manage unique, positive-numbered seats.
- Reserve a specific seat or the first available seat.
- Reject double bookings and bookings on a full train.
- Cancel a reservation and release its exact seat.
- Search trains by origin/destination.
- List routes, trains, available seats, and reservation history — all as read-only snapshots.

## Main classes

| Class | Responsibility |
|---|---|
| `Route` | Immutable route: ID, origin, destination; case-insensitive `matches`. |
| `Seat` | Numbered seat; `reserve()`/`release()` state transitions. |
| `Train` | One scheduled service: a route plus unique seats. |
| `Reservation` | One passenger reservation; `ACTIVE` → `CANCELLED`. |
| `ReservationStatus` | Enum: `ACTIVE`, `CANCELLED`. |
| `ReservationSystem` | Service layer owning routes, trains, seats, reservations, and search. |
| `RouteSnapshot` / `SeatSnapshot` / `TrainSnapshot` / `ReservationSnapshot` | Immutable read-only views returned to callers. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **Route search is case-insensitive** (`berlin` matches `Berlin`).
- **Route search is direction-sensitive**: `Berlin → Munich` does **not** match
  `Munich → Berlin`. Reverse matching is intentionally not supported.
- **Route association is value-based**: a train may reference any `Route` whose
  ID and stations match a registered route (an identical fresh `Route` object is
  accepted; object identity is not required).
- **Cancellation releases the exact recorded seat** and marks the reservation
  `CANCELLED`. Cancelled reservations remain in history and no longer block the
  seat; the seat can be reserved again.
- **Public methods return snapshots/copies.** Live `Seat`/`Train`/`Reservation`
  objects are never leaked, so callers cannot mutate internal state. Returned
  lists are unmodifiable.
- **Reservation timestamps** come from an injectable `Clock`
  (`new ReservationSystem(clock)`), which makes tests deterministic.
- **Each `Train` instance represents one scheduled train service.** Travel dates
  and recurring schedules are not modelled; reserved seats stay reserved on that
  service until cancelled.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external dependencies.
- `java.time` and `BigDecimal` for schedules and pricing.
- Dependency-free tests plus Bash and PowerShell validation scripts.

## Architecture / Design

`ReservationSystem` is the service layer over route, train, seat, and reservation models. It validates an entire reservation request before changing seat state and returns immutable snapshots so callers cannot bypass the service workflow.

## Project Structure

```text
.
├── src/trainticketreservationsystem/     # Domain, service, snapshots, CLI
├── tests/trainticketreservationsystem/   # Custom tests and runner
├── scripts/                              # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

Compile:

~~~
javac -Xlint:all -Werror -d out src/trainticketreservationsystem/*.java
~~~

Run the CLI commands:

~~~
java -cp out trainticketreservationsystem.Main help
java -cp out trainticketreservationsystem.Main demo
java -cp out trainticketreservationsystem.Main reservation-demo
java -cp out trainticketreservationsystem.Main cancellation-demo
java -cp out trainticketreservationsystem.Main search-demo
java -cp out trainticketreservationsystem.Main full-train-demo
java -cp out trainticketreservationsystem.Main validation-demo
~~~

## Testing

The project ships with a dependency-free test suite (custom assertion helper and
runner — no JUnit, Maven, or Gradle). See [TESTING.md](TESTING.md) for exact
commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded run.

## Known Limitations

This is a learning project. It intentionally has:

- no database (in-memory only)
- no HTTP API
- no login/authentication
- no payment provider
- no real railway integration
- no ticket PDF generation
- no production deployment
- no travel dates / recurring schedules (each `Train` is one service)

## Resume Value

Built a dependency-free Java train-reservation system with route and schedule modeling, atomic seat booking, cancellation, exact pricing, defensive snapshots, CLI workflows, and automated tests.
