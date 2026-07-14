# Hotel Room Booking System

## Overview

This educational, in-memory Java application manages hotel rooms, guests, bookings, availability, cancellation, and occupancy. It focuses on date-range correctness, precise pricing, and service-layer state consistency.

## Features

- Register uniquely numbered rooms with capacity and nightly rate.
- Create bookings with generated identifiers and guest-capacity checks.
- Find rooms available for an inclusive check-in and exclusive check-out range.
- Prevent overlapping active bookings while allowing adjacent stays.
- Calculate nights and total price with `BigDecimal`.
- Cancel bookings without losing booking history.
- Report occupancy and expose immutable room/booking snapshots.
- CLI demonstrations for availability, overlap, cancellation, occupancy, and validation.

## What This Project Demonstrates

- OOP domain models for rooms, guests, and bookings.
- Service-layer validation and state transitions.
- `LocalDate` range logic and overlap detection.
- `BigDecimal` money calculations.
- Defensive snapshots and deterministic automated tests.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or external dependencies.
- Dependency-free custom tests.
- Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/hotelroombookingsystem/     # Domain, hotel service, snapshots, CLI
├── tests/hotelroombookingsystem/   # Dependency-free tests and runner
├── scripts/                         # test.sh and test.ps1
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```bash
javac -Xlint:all -Werror -d out src/hotelroombookingsystem/*.java
java -cp out hotelroombookingsystem.Main demo
```

Other commands include `help`, `availability-demo`, `overlap-demo`, `cancellation-demo`, `occupancy-demo`, and `validation-demo`.

## Testing

Run the cross-platform test script from this folder:

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- In-memory educational model with no database or file persistence.
- No HTTP API, authentication, payment provider, frontend, housekeeping workflow, or channel-manager integration.
- Not intended as a production booking platform.

## Resume Value

Built a Java hotel-booking system with date-overlap protection, capacity-aware reservations, exact price calculation, cancellation history, occupancy reporting, defensive snapshots, and automated tests.
