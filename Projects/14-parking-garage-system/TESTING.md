# Testing the Parking Garage System

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`, including `assertBigDecimalEquals` and
`assertDoubleEquals`) and a runner (`TestRunner`). No JUnit, Maven, Gradle, or
other external libraries are involved. Tests live in
`tests/parkinggaragesystem/` and share the source package, so they can exercise
package-private behaviour directly.

## What is covered

- `VehicleTest` — plate normalization, validation, equality, snapshot.
- `ParkingSpotTest` — compatibility, occupancy, release rules, snapshot.
- `ParkingLevelTest` — spot map, null/duplicate spots, availability, snapshot.
- `ParkingReceiptTest` — validation, immutability, fee fields, snapshot.
- `FeeCalculationTest` — started-hour billing boundaries and per-type rates.
- `GarageTest` — parking, exit, history, reports (the most important file).
- `SnapshotTest` — unmodifiable results and proof that returned data cannot mutate
  internal spot/level/receipt state.
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
javac -Xlint:all -Werror -d out src/parkinggaragesystem/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/parkinggaragesystem/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" parkinggaragesystem.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" parkinggaragesystem.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out parkinggaragesystem.Main help
java -cp out parkinggaragesystem.Main demo
java -cp out parkinggaragesystem.Main parking-demo
java -cp out parkinggaragesystem.Main exit-demo
java -cp out parkinggaragesystem.Main fee-demo
java -cp out parkinggaragesystem.Main full-garage-demo
java -cp out parkinggaragesystem.Main report-demo
java -cp out parkinggaragesystem.Main validation-demo
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
- [ ] Verify plate normalization (`" b-ab 123 "` == `"B-AB 123"`).
- [ ] Verify a vehicle only parks in a compatible spot.
- [ ] Verify duplicate active vehicles and full-garage cases are rejected.
- [ ] Verify exit releases the exact spot and creates a receipt in history.
- [ ] Verify started-hour billing at 0/60/61/90 minutes and 1h1s.
- [ ] Verify exit before entry is rejected.
- [ ] Verify total/by-type revenue and occupancy percentage.
- [ ] Verify returned lists are unmodifiable and snapshots cannot mutate state.
