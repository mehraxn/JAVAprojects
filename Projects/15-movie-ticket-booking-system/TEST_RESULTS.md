# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 / javac 21.0.11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/movieticketbookingsystem/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/movieticketbookingsystem/*.java` |
| Automated tests | PASS | 93 / 93 test cases passed, 181 assertion checks |
| Movie tests | PASS | Validation, default genre, duration rules, snapshot data |
| Seat tests | PASS | Availability, reserve/release rules, snapshot decoupling |
| Showtime tests | PASS | Seat map, null/duplicate seats, ticket price, full detection, snapshot |
| Booking tests | PASS | Validation, ACTIVE/CANCELLED status, unmodifiable seat list, snapshot |
| BookingSystem tests | PASS | Booking, cancellation, availability, deterministic IDs, fixed Clock |
| All-or-nothing booking tests | PASS | Failed bookings reserve nothing and leave count/availability unchanged |
| Defensive snapshot tests | PASS | Unmodifiable results; snapshots cannot mutate internal seat/showtime/booking |
| Main CLI tests | PASS | help/demo/booking/cancellation/full-showtime/availability/validation + invalid command |
| Main demo | PASS | `java -cp out movieticketbookingsystem.Main demo` (exit 0) |
| Booking demo | PASS | `java -cp out movieticketbookingsystem.Main booking-demo` (exit 0) |
| Cancellation demo | PASS | `java -cp out movieticketbookingsystem.Main cancellation-demo` (exit 0) |
| Full-showtime demo | PASS | `java -cp out movieticketbookingsystem.Main full-showtime-demo` (exit 0) |

## Validation commands used

Windows PowerShell (this machine):

```powershell
.\scripts\test.ps1
```

Equivalent POSIX shells (Linux/macOS/Git Bash):

```bash
bash scripts/test.sh
```

`test.ps1` was run end-to-end on Windows (exit 0): strict application compile,
strict test compile, automated test execution (93/93), all six CLI demos, and
cleanup of generated build output. The scripts differ only in the JVM classpath
separator (`;` on Windows, `:` on POSIX shells).

## Known limitations

- In-memory movie ticket booking system only.
- No database.
- No HTTP API.
- No authentication/users.
- No payment provider.
- No real cinema integration.
- No ticket PDF/QR generation.
- No seat-map UI.
- No production booking guarantees.
- Intended as a Java OOP/service-layer learning project.
