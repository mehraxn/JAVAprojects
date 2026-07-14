# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 / javac 21.0.11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/parkinggaragesystem/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/parkinggaragesystem/*.java` |
| Automated tests | PASS | 92 / 92 test cases passed, 174 assertion checks |
| Vehicle tests | PASS | Plate normalization, validation, equality, snapshot |
| ParkingSpot tests | PASS | Compatibility, occupancy, release rules, snapshot |
| ParkingLevel tests | PASS | Spot map, null/duplicate spots, availability, full detection, snapshot |
| ParkingReceipt tests | PASS | Validation, immutability, fee fields, snapshot |
| Garage tests | PASS | Setup, parking, exit, history, availability, reports |
| Fee calculation tests | PASS | Started-hour boundaries (0/60/61/90 min, 1h1s), per-type rates |
| Defensive snapshot tests | PASS | Unmodifiable results; snapshots cannot mutate internal spot/level/receipt |
| Main CLI tests | PASS | help/demo/parking/exit/fee/full-garage/report/validation + invalid command |
| Main demo | PASS | `java -cp out parkinggaragesystem.Main demo` (exit 0) |
| Parking demo | PASS | `java -cp out parkinggaragesystem.Main parking-demo` (exit 0) |
| Exit demo | PASS | `java -cp out parkinggaragesystem.Main exit-demo` (exit 0) |
| Fee demo | PASS | `java -cp out parkinggaragesystem.Main fee-demo` (exit 0) |
| Full garage demo | PASS | `java -cp out parkinggaragesystem.Main full-garage-demo` (exit 0) |

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
strict test compile, automated test execution (92/92), all seven CLI demos, and
cleanup of generated build output. The scripts differ only in the JVM classpath
separator (`;` on Windows, `:` on POSIX shells).

## Known limitations

- In-memory parking garage system only.
- No database.
- No HTTP API.
- No authentication/users.
- No payment provider.
- No license plate camera integration.
- No real parking barrier/gate integration.
- No production parking guarantees.
- Intended as a Java OOP/service-layer learning project.
