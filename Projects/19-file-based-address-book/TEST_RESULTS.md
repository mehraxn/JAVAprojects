# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/filebasedaddressbook/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/filebasedaddressbook/*.java` |
| Automated tests | PASS | 149 checks, 0 failures (`filebasedaddressbook.TestRunner`) |
| Contact model tests | PASS | 43 checks: validation, email rules, atomic updates, copy behavior |
| AddressBook tests | PASS | 48 checks: CRUD, search, sort, conflict-safe import |
| FileStore tests | PASS | 29 checks: UTF-8 round trip, malformed files, atomic save, export/import |
| Defensive copy tests | PASS | Returned contacts from get/find/list/search and input contacts cannot mutate internal state (part of the AddressBook suite) |
| Main CLI tests | PASS | 29 checks: help/demo/file-demo/import-demo/validation-demo, unknown commands |
| Main demo | PASS | `demo` exits 0 |
| File demo | PASS | Saves to a temp TSV, reloads, verifies count, deletes the file |
| Import demo | PASS | Successful import plus a rejected conflicting import that changes nothing |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- Local file-based address book — no database.
- No HTTP API and no authentication/users.
- No GUI.
- No advanced contact fields (multiple numbers, addresses, groups).
- Email validation is simple educational validation, not production-grade.
- Intended as a Java file-I/O learning project.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- File tests and demos write only to system temporary files and delete them; no TSV/TXT files are created inside the repository.
