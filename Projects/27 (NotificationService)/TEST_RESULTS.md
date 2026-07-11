# Test Results

Date: 2026-07-11

All commands below were actually executed on a Windows 11 machine from this project directory. No system-wide JDK was installed, so validation used a portable Eclipse Temurin JDK 21.0.11 (`javac`/`java` only — the project needs no build tool or libraries). Nothing in this file is estimated or assumed. No real email, SMS, or push service was contacted at any point — all delivery is `MockNotificationSender`.

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK Temurin 21.0.11+10 (portable; no system JDK installed) |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/notificationservice/*.java` — clean |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/notificationservice/*.java` — clean |
| Automated tests | PASS | `TestRunner`: 47 tests passed, 0 failed, 136 assertion checks total, exit code 0 |
| Main demo | PASS | `java -cp out notificationservice.Main demo` — EMAIL/APP success, one intentional SMS failure, retry success, history shows all 3 SENT (SMS attempts=2), exit code 0 |
| Retry demo | PASS | Retry success after one failure, then retry exhaustion (`false` at the 2-attempt limit), exit code 0 |
| Validation demo | PASS | ACCEPTED/REJECTED lines for EMAIL/SMS/APP samples, exit code 0 |
| Missing-sender demo | PASS | Clean FAILED record with "No mock sender is registered" error, exit code 0 (intentional) |
| send command | PASS | `send EMAIL learner@example.com "Welcome"` → SENT, exit 0; `send SMS abc123 "Hi"` → clean error, exit 1; unknown command/channel and missing arguments → exit 1 |
| Defensive snapshot tests | PASS | `viewQueue`/`getHistory` are unmodifiable; mutating snapshot copies or `findNotification` results does not change stored records (verified in automated tests) |
| scripts/test.ps1 | PASS | Full pipeline (clean, strict compiles, 47 tests, demo, retry-demo, validation-demo), exit code 0 |
| scripts/test.sh | PASS | Run via Git Bash on Windows; picks the `;` classpath separator automatically, exit code 0 |

Test breakdown: 11 `Notification` tests, 6 recipient-validation tests, 6 `MockNotificationSender` tests, 14 `NotificationService` tests, 10 CLI smoke tests (calling `Main.run` directly with captured streams).

## Known limitations

- Local mock notification service only.
- No real email/SMS/push provider integration.
- No persistent queue.
- No database.
- No async worker/thread pool.
- No scheduled delivery.
- No authentication or user accounts.
- Intended as a Java OOP/service-design learning project.
