# Testing the Movie Ticket Booking System

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`, including `assertBigDecimalEquals`) and a runner
(`TestRunner`). No JUnit, Maven, Gradle, or other external libraries are
involved. Tests live in `tests/movieticketbookingsystem/` and share the source
package, so they can exercise package-private behaviour directly.

## What is covered

- `MovieTest` — validation, default genre, snapshot data.
- `SeatTest` — availability, reserve/release rules, snapshot data.
- `ShowtimeTest` — seat map, duplicate/null seats, ticket price, full detection.
- `BookingTest` — validation, status transitions, unmodifiable seat list.
- `BookingSystemTest` — booking, all-or-nothing safety, cancellation, availability
  (the most important file), with a fixed `Clock` for deterministic timestamps.
- `SnapshotTest` — unmodifiable results and proof that returned data cannot mutate
  internal seat, showtime, or booking state.
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
javac -Xlint:all -Werror -d out src/movieticketbookingsystem/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/movieticketbookingsystem/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" movieticketbookingsystem.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" movieticketbookingsystem.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out movieticketbookingsystem.Main help
java -cp out movieticketbookingsystem.Main demo
java -cp out movieticketbookingsystem.Main booking-demo
java -cp out movieticketbookingsystem.Main cancellation-demo
java -cp out movieticketbookingsystem.Main full-showtime-demo
java -cp out movieticketbookingsystem.Main availability-demo
java -cp out movieticketbookingsystem.Main validation-demo
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

> Note: the JVM classpath separator differs by platform — `:` on Linux/macOS,
> `;` on Windows. `test.sh` uses `:` and `test.ps1` uses `;` accordingly.

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
- [ ] Verify single- and multi-seat booking, and deterministic `B0001`/`B0002` IDs.
- [ ] Verify all-or-nothing: a mixed valid/invalid request reserves nothing.
- [ ] Verify duplicate-seat requests and already-booked seats are rejected.
- [ ] Verify booking into a full showtime is rejected.
- [ ] Verify cancellation releases exactly the booked seats and keeps history.
- [ ] Verify seats can be rebooked after cancellation.
- [ ] Verify total price equals ticket price × seat count with 2 decimals.
- [ ] Verify returned lists are unmodifiable and snapshots cannot mutate state.
