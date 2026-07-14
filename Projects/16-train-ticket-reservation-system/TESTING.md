# Testing the Train Ticket Reservation System

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`) and a runner (`TestRunner`). No JUnit, Maven, Gradle, or
other external libraries are involved. Tests live in
`tests/trainticketreservationsystem/` and share the source package, so they can
exercise package-private behaviour directly.

## What is covered

- `RouteTest` — validation, case-insensitive/direction-sensitive matching, value equality.
- `SeatTest` — availability state and reserve/release transitions.
- `TrainTest` — seat validation, duplicates, availability, full detection, unmodifiable views.
- `ReservationTest` — field validation, timestamp, `ACTIVE`/`CANCELLED` status.
- `ReservationStatusTest` — enum values.
- `ReservationSystemTest` — route/train management, reservations, cancellation,
  search, deterministic clock, unchanged state after failures.
- `SnapshotTest` — public data cannot mutate internal state (unmodifiable lists, decoupled snapshots).
- `MainTest` — `Main.run` smoke tests, called in-process (no separate JVM).

## Commands

### A) Clean

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

### B) Strict compile: application

~~~
javac -Xlint:all -Werror -d out src/trainticketreservationsystem/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/trainticketreservationsystem/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" trainticketreservationsystem.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" trainticketreservationsystem.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out trainticketreservationsystem.Main help
java -cp out trainticketreservationsystem.Main demo
java -cp out trainticketreservationsystem.Main reservation-demo
java -cp out trainticketreservationsystem.Main cancellation-demo
java -cp out trainticketreservationsystem.Main search-demo
java -cp out trainticketreservationsystem.Main full-train-demo
java -cp out trainticketreservationsystem.Main validation-demo
~~~

### F) Scripts

Linux/macOS/Git Bash:

~~~
./scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

### G) Cleanup

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

## Manual testing checklist

- [ ] Compile strictly with `-Xlint:all -Werror`.
- [ ] Run `TestRunner` and confirm all cases pass.
- [ ] Test specific and automatic seat selection.
- [ ] Fill a train and verify full-train rejection leaves state unchanged.
- [ ] Verify search is case-insensitive but direction-sensitive.
- [ ] Verify cancellation releases the recorded seat and the seat can be re-reserved.
- [ ] Verify returned lists cannot be modified and snapshots cannot mutate internal state.
