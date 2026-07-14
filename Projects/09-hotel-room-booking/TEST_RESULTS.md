# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 LTS |
| Strict application compile | PASS | `javac -Xlint:all -Werror` |
| Strict test compile | PASS | `javac -Xlint:all -Werror` |
| Automated tests | PASS | 90 checks, 0 failures |
| Room tests | PASS | Validation, BigDecimal rate, and capacity |
| Guest tests | PASS | ID/name validation and immutability |
| Booking tests | PASS | Date range, guest count, nights, price, and status |
| Hotel availability tests | PASS | Full overlap matrix, adjacent ranges, and checkout exclusivity |
| Cancellation tests | PASS | History status and restored availability |
| Occupancy tests | PASS | Booked, checkout, and cancelled dates |
| Snapshot/defensive data tests | PASS | Immutable results and copied room input |
| Main CLI tests | PASS | All valid commands plus invalid/default behavior |
| Main demo | PASS | Booking, total, availability, cancellation, occupancy |
| Overlap demo | PASS | Overlap rejection and adjacent booking |
| Occupancy demo | PASS | Booked, checkout, and cancelled percentages |
| PowerShell validation script | PASS | Run with execution-policy bypass because local scripts are disabled |
| Bash validation script | NOT RUN | Windows `bash` resolved to WSL, but no WSL distribution is installed |

## Known limitations

- In-memory hotel booking system only.
- No database or HTTP API.
- No authentication/users.
- No payment provider.
- No external hotel/channel-manager integration.
- No housekeeping workflow.
- No production booking guarantees.
- Intended as a Java OOP/date-range/business-logic learning project.
