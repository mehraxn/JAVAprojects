# Event Registration System

## Overview

This educational, in-memory Java application manages events, attendees, registrations, cancellation, capacity, and search. A service layer coordinates the domain models and returns immutable views instead of exposing mutable state.

## Features

- Create events with positive capacities and unique identifiers.
- Register attendees and prevent duplicate registration for the same event.
- Generate registration identifiers and deterministic timestamps.
- Cancel by attendee or registration ID and restore capacity.
- Search events by name, category, or exact date.
- Sort search results by date and then name.
- Return safe event and registration snapshots.
- CLI demonstrations for registration, capacity, cancellation, search, and validation.

## What This Project Demonstrates

- OOP modeling of attendees, events, and registrations.
- Service-layer orchestration and business-rule validation.
- Capacity-limited relationships and cancellation workflows.
- Case-insensitive search, deterministic sorting, and injectable `Clock` usage.
- Defensive copies and dependency-free automated testing.

## Tech Stack

- Java 21 standard library.
- Plain `javac`/`java`; no Maven or external dependencies.
- Dependency-free custom tests.
- Bash and PowerShell validation scripts.

## Project Structure

```text
.
├── src/eventregistrationsystem/     # Models, service, snapshots, CLI
├── tests/eventregistrationsystem/   # Dependency-free tests and runner
├── scripts/                          # test.sh and test.ps1
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```bash
javac -Xlint:all -Werror -d out src/eventregistrationsystem/*.java
java -cp out eventregistrationsystem.Main demo
```

Other commands include `help`, `registration-demo`, `capacity-demo`, `cancellation-demo`, `search-demo`, and `validation-demo`.

## Testing

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

See [TESTING.md](TESTING.md) for the procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Known Limitations

- In-memory educational model with deliberately simple email validation.
- No database, HTTP API, authentication, payment/ticketing integration, email delivery, calendar integration, waitlist, or deployment layer.

## Resume Value

Built a Java event-registration service with capacity enforcement, duplicate prevention, cancellation, deterministic identifiers and timestamps, searchable event data, defensive snapshots, and automated tests.
