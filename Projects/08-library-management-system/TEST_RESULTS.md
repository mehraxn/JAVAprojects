# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 / javac 21.0.11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/librarymanagementsystem/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/librarymanagementsystem/*.java` |
| Automated tests | PASS | 80 / 80 test cases passed, 185 assertion checks |
| Book tests | PASS | Validation, category/year, availability transitions, snapshot |
| Member tests | PASS | Validation, simple email rule, borrowed-set tracking, snapshot |
| LoanRecord tests | PASS | Due dates, status/return transition, overdue logic, snapshot |
| Library tests | PASS | Borrow/return/history workflows, limits, deterministic IDs, fixed Clock |
| Search tests | PASS | Case-insensitive title/author search, available-book listing |
| State consistency tests | PASS | Failed borrow/return leave book/member/loan state unchanged |
| Defensive snapshot tests | PASS | Unmodifiable results; snapshots cannot mutate internal state |
| Main CLI tests | PASS | help/demo/borrow/return/search/overdue/history/validation + invalid command |
| Main demo | PASS | `java -cp out librarymanagementsystem.Main demo` (exit 0) |
| Borrow demo | PASS | `java -cp out librarymanagementsystem.Main borrow-demo` (exit 0) |
| Return demo | PASS | `java -cp out librarymanagementsystem.Main return-demo` (exit 0) |
| Overdue demo | PASS | `java -cp out librarymanagementsystem.Main overdue-demo` (exit 0) |

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
strict test compile, automated test execution (80/80), all seven CLI demos, and
cleanup of generated build output. The scripts differ only in the JVM classpath
separator (`;` on Windows, `:` on POSIX shells).

## Known limitations

- In-memory library management system only.
- No database.
- No HTTP API.
- No authentication/users.
- No barcode scanner.
- No real library integration.
- No fines/payment system.
- No production library guarantees.
- Intended as a Java OOP/service-layer learning project.
