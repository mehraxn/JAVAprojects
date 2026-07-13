# Test Results

Date: 2026-07-13

Environment: Windows 11, OpenJDK 21.0.11 (Microsoft build). Validation was run
via `scripts/test.ps1` (Windows). `scripts/test.sh` targets Linux/macOS/Git Bash
and uses the `:` classpath separator, so it was not run on this Windows machine.

## Local validation

| Check | Result | Notes |
|---|---|---|
| Java version | PASS | OpenJDK 21.0.11 (`java -version`) |
| Strict application compile | PASS | `javac -Xlint:all -Werror` on `src/**` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out` on `tests/**` |
| Automated tests | PASS | 79/79 cases, 141 assertion checks |
| Route tests | PASS | Validation, matching, value equality |
| Seat tests | PASS | Availability and reserve/release behavior |
| Train tests | PASS | Seats, duplicates, availability, unmodifiable views |
| Reservation tests | PASS | Record fields, timestamp, ACTIVE/CANCELLED status |
| ReservationStatus tests | PASS | Enum values |
| ReservationSystem tests | PASS | Reservation/cancellation/search workflows, injected clock |
| Defensive snapshot tests | PASS | Public data cannot mutate internal state |
| Main CLI tests | PASS | help/demo/reservation/cancellation/search/full-train/validation + invalid command |
| Main demo | PASS | `Main demo` command |
| Reservation demo | PASS | Specific + automatic reservation, double-booking rejection |
| Cancellation demo | PASS | Seat released after cancellation, re-reserved, clean double/missing cancel |
| Search demo | PASS | Case-insensitive, direction-sensitive |
| Full train demo | PASS | Full-train rejection leaves state unchanged |
| Validation demo | PASS | Intentional validation failures handled cleanly (exit 0) |
| `scripts/test.ps1` | PASS | Full pipeline, cleans up `out/` and `test-out/` |
| `scripts/test.sh` | NOT RUN | Linux/macOS/Git Bash script (`:` separator); not run on Windows |

## Known limitations

- In-memory train reservation system only.
- No database.
- No HTTP API.
- No authentication/users.
- No payment provider.
- No real railway integration.
- No ticket PDF generation.
- Each `Train` instance represents one scheduled train service; travel dates and
  recurring schedules are not modeled.
- Intended as a Java OOP/service-layer learning project.
