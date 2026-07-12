# Event Registration System

An educational, in-memory Java event registration system focused on OOP, immutable data views, and service-layer business rules.

## What it demonstrates

- Attendee, Event, and Registration domain models
- An EventRegistrationSystem service layer
- Input validation, capacity enforcement, and duplicate-registration prevention
- Cancellation by attendee or registration ID with capacity restoration
- Case-insensitive event searches by name/category and exact-date filtering
- Search results sorted by date, then name
- Deterministic registration timestamps through an injected `Clock`
- Safe, immutable event and registration snapshots
- Dependency-free automated tests and strict compilation

## Features

Events have generated registration IDs, positive capacities, and unique IDs/definitions. The same attendee may join different events but cannot register twice for one event. Failed registrations leave state unchanged. Public service queries return snapshots/copies so callers cannot mutate internal state.

Email checks are deliberately simple and educational, not production-grade: exactly one `@`, nonblank local/domain parts, and an internally placed dot in the domain.

## Quick start

```text
javac -Xlint:all -Werror -d out src/eventregistrationsystem/*.java
java -cp out eventregistrationsystem.Main help
java -cp out eventregistrationsystem.Main demo
java -cp out eventregistrationsystem.Main registration-demo
java -cp out eventregistrationsystem.Main capacity-demo
java -cp out eventregistrationsystem.Main cancellation-demo
java -cp out eventregistrationsystem.Main search-demo
java -cp out eventregistrationsystem.Main validation-demo
```

See [TESTING.md](TESTING.md) for exact validation commands and [TEST_RESULTS.md](TEST_RESULTS.md) for recorded local results.

## Limitations

This learning project has no database, HTTP API, login/authentication, payment or ticketing integration, email notifications, calendar integration, waitlist, or production deployment.
