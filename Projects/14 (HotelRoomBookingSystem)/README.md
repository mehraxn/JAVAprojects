# Hotel Room Booking System

An educational, in-memory Java hotel room booking system focused on OOP, date ranges, availability, and service-layer business rules.

## What it demonstrates

- Immutable Room and Guest domain models, plus Booking records with status
- Hotel service-layer room, reservation, cancellation, and occupancy workflows
- Positive room capacities and guest-count validation
- Date-range validation and overlap prevention
- Availability searches and adjacent bookings
- `BigDecimal` money calculations to avoid floating-point pricing errors
- Immutable room and booking snapshots
- Dependency-free automated tests and strict compilation

## Date-range and cancellation rules

Check-in is inclusive and check-out is exclusive. A room becomes available on its check-out date, so Aug 10 to Aug 13 and Aug 13 to Aug 15 may coexist. Overlapping stays are rejected without changing booking state.

Cancelled bookings remain in history with `CANCELLED` status. They no longer block availability and do not count toward occupancy.

## Features

Add uniquely numbered rooms, create capacity-aware bookings with generated IDs, calculate exact totals, search available rooms, cancel bookings, retain booking history, and calculate occupancy percentages. Public queries and booking results are snapshots, protecting internal hotel state.

## Quick start

```text
javac -Xlint:all -Werror -d out src/hotelroombookingsystem/*.java
java -cp out hotelroombookingsystem.Main help
java -cp out hotelroombookingsystem.Main demo
java -cp out hotelroombookingsystem.Main availability-demo
java -cp out hotelroombookingsystem.Main overlap-demo
java -cp out hotelroombookingsystem.Main cancellation-demo
java -cp out hotelroombookingsystem.Main occupancy-demo
java -cp out hotelroombookingsystem.Main validation-demo
```

See [TESTING.md](TESTING.md) for commands and [TEST_RESULTS.md](TEST_RESULTS.md) for recorded validation.

## Limitations

No database, HTTP API, login/authentication, payment provider, external hotel/channel-manager integration, housekeeping workflow, deployment, or production booking guarantees are included.
