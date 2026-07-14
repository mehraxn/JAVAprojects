# Notification Service

A local Java notification service simulation. It demonstrates a pluggable-channel service design — FIFO queue, retries, status tracking, and history — with **mock delivery only**: no real email, SMS, or push provider is ever contacted, and no external dependency is used.

## What it demonstrates

- Java interfaces and pluggable delivery channels (`NotificationChannel`)
- Mock delivery providers with deterministic, configurable failures
- FIFO queue processing and clean lifecycle transitions (`QUEUED → SENDING → SENT/FAILED`)
- Retry behavior with a caller-defined attempt limit and honest exhaustion handling
- Per-channel recipient validation (email shape, phone-like SMS values, app user IDs)
- Defensive snapshots — the queue and history can never be mutated from outside
- Dependency-free automated tests (47 tests, 136 checks) with a custom runner
- Strict compilation: everything builds under `javac -Xlint:all -Werror`
- A command-based demo CLI with correct exit codes and a testable `Main.run`

## Commands

| Command | What it does |
|---|---|
| `help` | Print usage and available commands |
| `demo` | Full workflow: EMAIL success, SMS failure + retry success, APP success, history summary |
| `send <EMAIL\|SMS\|APP> <recipient> <message>` | Send one notification through a mock sender |
| `retry-demo` | Failure followed by retry success, then retry exhaustion at the attempt limit |
| `validation-demo` | Accepted and rejected recipient examples per channel |
| `missing-sender-demo` | Clean `FAILED` record when a channel has no registered sender |

`send` exits 0 only when the notification reaches `SENT`; invalid channels, recipients, or messages print a clear error and exit non-zero — no stack traces.

## Quick start

```text
javac -Xlint:all -Werror -d out src/notificationservice/*.java

java -cp out notificationservice.Main help
java -cp out notificationservice.Main demo
java -cp out notificationservice.Main send EMAIL learner@example.com "Welcome"
java -cp out notificationservice.Main retry-demo
java -cp out notificationservice.Main validation-demo
java -cp out notificationservice.Main missing-sender-demo
```

## Project structure

```text
src/notificationservice/     Application source (model, channel, mock sender, service, CLI)
tests/notificationservice/   Dependency-free tests and TestRunner
scripts/test.sh, test.ps1    One-command validation (clean, compile, test, demos)
README.md, TESTING.md        Documentation
TEST_RESULTS.md              Actual recorded validation results
```

## Main classes

- `Notification` — validated record: recipient, message, channel, lifecycle status, timestamps, attempt count, last error. Status transitions are enforced (`markSent` only from `SENDING`, `requeue` only from `FAILED`, ...).
- `NotificationChannel` — sender interface with `getType()` and `deliver()`.
- `MockNotificationSender` — local mock sender; `new MockNotificationSender(SMS, 1)` fails the first attempt and succeeds afterwards, which makes retry tests deterministic. Prints "deliveries" to a configurable stream.
- `NotificationService` — sender registry (one per channel), FIFO queue, dispatch, retry with attempt limit, ID lookup, and history. All views are defensive copies in unmodifiable lists.
- `Main` — argument parsing and output only; the CLI lives in a testable `run(args, out, err)` method, and only `main` calls `System.exit`.

## Recipient validation rules

- **EMAIL** — must look like `name@domain.tld` (no spaces, exactly one `@`, a dot in the domain). `learner@example.com` is accepted; `bad-email` and `user@nodomain` are rejected.
- **SMS** — 3 to 30 characters of digits, `+`, parentheses, spaces, dots, or dashes. `+393331112222` and `3331112222` are accepted; `abc123` and `12` are rejected.
- **APP** — any non-blank identifier up to 100 characters, e.g. `user-123`.

These are deliberately simple, beginner-appropriate rules — real-world address validation is far more involved.

## How to test

- `TESTING.md` — exact commands for strict compile, the test runner, and the CLI demos.
- `TEST_RESULTS.md` — the honest record of the validation actually performed.
- Quick version: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell).

## What is not production-grade

- Local mock service only — no real email/SMS/push integration
- No database and no persistent queue (everything is in memory)
- No async worker or thread pool; processing is explicit and single-threaded
- No scheduled delivery
- No authentication or user accounts
- No production delivery guarantees

## Possible future improvements

- A worker thread that drains the queue in the background
- Persistent queue storage
- Delivery receipts and per-channel statistics
- Rate limiting per channel
