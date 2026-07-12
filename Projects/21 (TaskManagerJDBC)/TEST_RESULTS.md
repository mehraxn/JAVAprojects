# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/taskmanagerjdbc/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/taskmanagerjdbc/*.java` |
| Automated tests | PASS | 162 checks, 0 failures (`taskmanagerjdbc.TestRunner`) |
| Task model tests | PASS | 31 checks: validation, atomic updates, final class |
| In-memory repository tests | PASS | 38 checks: CRUD, filtering, defensive copies, ID exhaustion |
| Service-layer tests | PASS | 37 checks: workflows, missing-task handling, end-to-end lifecycle |
| Main CLI tests | PASS | 29 checks: help/demo/in-memory-demo/validation-demo/jdbc-info, unknown commands |
| DatabaseConnection tests | PASS | 9 checks: URL validation, wrapped no-driver failure, no password leak |
| JDBC repository static review | PASS | 18 checks: argument validation + source review (PreparedStatement, try-with-resources, generated keys, `?` placeholders, no SQL concatenation, no hardcoded credentials) |
| Real JDBC database integration tests | NOT RUN | No JDBC driver or database is bundled; no real database was used |
| Main demo | PASS | `demo`, `in-memory-demo`, `validation-demo`, `jdbc-info` all exit 0; `bogus` exits 1 |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- The in-memory repository, service layer, domain model, and CLI are fully tested.
- The JDBC repository compiles under `-Xlint:all -Werror` and is statically reviewed; its argument validation and the no-driver failure path are the only parts exercised at runtime.
- Real JDBC database integration tests are NOT RUN — no driver or database is bundled, and no PostgreSQL/MySQL/H2/SQLite testing was performed.
- No HTTP API and no authentication/users.
- No demo database file is included.
- Intended as a Java JDBC/repository-pattern learning project, not production software.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- Run the tests from the project root so the static-review suite can read `src/taskmanagerjdbc/JdbcTaskRepository.java`.
