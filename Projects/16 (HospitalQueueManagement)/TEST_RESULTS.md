# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 LTS |
| Strict application compile | PASS | `javac -Xlint:all -Werror` |
| Strict test compile | PASS | `javac -Xlint:all -Werror` |
| Automated tests | PASS | 93 checks; 0 failures |
| Patient tests | PASS | Validation, copies, final class, and timestamps |
| TriageLevel tests | PASS | Priority ordering and display name |
| Queue ordering tests | PASS | Priority, arrival, and ID tie-breaks |
| Status lifecycle tests | PASS | Waiting, treatment, requeue, discharge, and terminal rules |
| Defensive snapshot tests | PASS | Public data cannot mutate internal state |
| Queue statistics tests | PASS | Counts and deterministic wait-time calculations |
| Main CLI tests | PASS | All commands and invalid/no-command behavior |
| Main demo | PASS | Run by PowerShell validation script |
| Emergency demo | PASS | Run by PowerShell validation script |
| Statistics demo | PASS | Run by PowerShell validation script |
| PowerShell test script | PASS | Full compile, tests, demos, and cleanup |
| Bash test script | NOT RUN | Host `bash` routes to WSL; no WSL distribution is installed |

## Known limitations

- In-memory hospital queue only.
- No database.
- No HTTP API.
- No authentication/users.
- No real hospital integration.
- No appointment scheduling.
- No medical records system.
- No production clinical safety guarantees.
- Intended as a Java priority-queue/OOP learning project.
