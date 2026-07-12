# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 LTS |
| Strict application compile | PASS | `javac -Xlint:all -Werror` |
| Strict test compile | PASS | `javac -Xlint:all -Werror` |
| Automated tests | PASS | 82 checks, 0 failures |
| Attendee tests | PASS | Required fields and educational email rules |
| Event tests | PASS | Capacity, duplicates, registration, and cancellation |
| Registration tests | PASS | IDs, event ID, attendee, and timestamp |
| Service-layer tests | PASS | Event workflows, fixed clock, cancellation, and searches |
| Snapshot/defensive data tests | PASS | Query lists are unmodifiable and input events are copied |
| Main CLI tests | PASS | help/demo/registration/capacity/cancellation/search/validation and invalid command |
| Main demo | PASS | `Main demo` |
| Capacity demo | PASS | Capacity boundary and unchanged failed-registration state |
| Cancellation demo | PASS | Attendee/registration-ID cancellation and capacity restoration |
| Search demo | PASS | Name, date, category, case handling, and ordering |
| PowerShell validation script | PASS | Run with execution-policy bypass because local script execution is disabled |
| Bash validation script | NOT RUN | Windows `bash` resolved to WSL, but no WSL distribution is installed |

## Known limitations

- In-memory event registration system only.
- No database or HTTP API.
- No authentication/users.
- No payment/ticketing provider.
- No email notifications or calendar integration.
- No waitlist.
- Intended as a Java OOP/service-layer learning project.
