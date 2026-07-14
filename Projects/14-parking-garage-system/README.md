# Parking Garage System

## Overview

An educational, dependency-free Java project that models a parking garage. It
focuses on clean object-oriented design, a service layer with real business
rules, compatible spot assignment, vehicle entry/exit workflows, started-hour
`BigDecimal` fee calculation, completed parking history, defensive data exposure,
and dependency-free automated tests — not on a database, web API, or camera/gate
integration.

## What This Project Demonstrates

- **Vehicle** domain model (normalized license plate, vehicle type)
- **VehicleType** enum (`MOTORCYCLE`, `CAR`, `TRUCK`)
- **ParkingSpot** domain model (ID, supported type, occupied/free state)
- **ParkingLevel** domain model (numbered level with a unique-ID spot map)
- **Garage** service layer that owns all state changes
- compatible spot assignment across levels
- active parking tracking
- duplicate active vehicle prevention
- multi-level availability tracking (total and by vehicle type)
- vehicle exit workflow with exact spot release
- **ParkingReceipt** generation and completed history
- started-hour fee calculation with per-type rates
- revenue reporting (total, by vehicle type) and occupancy percentage
- defensive snapshots so internal state cannot be mutated from outside
- command-based CLI demos
- dependency-free automated tests
- strict compilation (`-Xlint:all -Werror`)

## Features

- Add levels and reject duplicate level numbers or duplicate spot IDs.
- Park a vehicle in the first compatible free spot; reject incompatible/full cases.
- Reject duplicate active vehicles (by normalized plate).
- Exit a vehicle: release the exact spot, generate a receipt, keep it in history.
- Report available spots (total and by type), occupancy %, and revenue.
- Per-vehicle-type hourly rates with started-hour billing, using `BigDecimal`.
- CLI demos for every feature area.

## Main classes

| Class | Responsibility |
|---|---|
| `Vehicle` | Immutable vehicle: normalized plate + type; equality by normalized plate. |
| `VehicleType` | Enum: `MOTORCYCLE`, `CAR`, `TRUCK`. |
| `ParkingSpot` | One spot and its occupancy; only `Garage` assigns/releases it. |
| `ParkingLevel` | Numbered level with a unique-ID spot map and availability queries. |
| `ParkingReceipt` | Immutable completed-session record with the computed fee. |
| `Garage` | Service layer: setup, entry, exit, availability, history, reports. |
| `VehicleSnapshot` / `ParkingSpotSnapshot` / `ParkingLevelSnapshot` / `ActiveParkingSnapshot` / `ParkingReceiptSnapshot` | Immutable read-only views returned to callers. |
| `Main` | Command-based CLI / demo driver. |

## Behavior notes

- **License plates are normalized** (trimmed, upper-cased). `" b-ab 123 "` and
  `"B-AB 123"` are the same vehicle; duplicate detection uses the normalized plate.
- **Vehicles only park in compatible spots**; a vehicle is assigned the first
  compatible free spot, scanning levels in insertion order.
- **Duplicate active vehicle entries are rejected**; a plate can be parked once
  at a time.
- **Exit releases the exact assigned spot**, removes the active record, and stores
  a receipt in completed history. Completed receipts do not affect availability.
- **Started-hour billing**: any started hour bills a full hour, with a minimum of
  one hour. `0` minutes still bills `1` hour; `60` minutes bills `1` hour; `61`
  minutes bills `2` hours. Exit before entry is rejected.
- **Per-type rates**: `MOTORCYCLE 3.00`, `CAR 5.00`, `TRUCK 10.00` per hour.
- **Money uses `BigDecimal`** (never `double`); user-facing fees show 2 decimals.
- **Public methods return immutable snapshots in unmodifiable lists.** Live
  `ParkingSpot`/`ParkingLevel`/`ParkingReceipt` objects are never leaked, so
  external code cannot release spots or corrupt state without going through
  `Garage`.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven, Gradle, or external dependencies.
- `BigDecimal` for fee and revenue calculations.
- Dependency-free tests plus Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/parkinggaragesystem/     # Domain, garage service, snapshots, CLI
├── tests/parkinggaragesystem/   # Custom automated test suite
├── scripts/                     # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

Compile:

~~~
javac -Xlint:all -Werror -d out src/parkinggaragesystem/*.java
~~~

Run the CLI commands:

~~~
java -cp out parkinggaragesystem.Main help
java -cp out parkinggaragesystem.Main demo
java -cp out parkinggaragesystem.Main parking-demo
java -cp out parkinggaragesystem.Main exit-demo
java -cp out parkinggaragesystem.Main fee-demo
java -cp out parkinggaragesystem.Main full-garage-demo
java -cp out parkinggaragesystem.Main report-demo
java -cp out parkinggaragesystem.Main validation-demo
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
- no license-plate camera integration
- no real parking barrier/gate integration
- no production parking guarantees
- no production deployment

## Resume Value

Built a dependency-free Java parking-garage system with compatible spot assignment, exact entry/exit tracking, started-hour fee calculation, occupancy and revenue reports, defensive snapshots, and automated tests.
