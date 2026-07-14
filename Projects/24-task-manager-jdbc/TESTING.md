# Task Manager JDBC Testing

All commands run from the project root. The automated tests need only a JDK — no database, driver, or network access. Run the tests from the project root so the JDBC static-review suite can find `src/taskmanagerjdbc/JdbcTaskRepository.java`.

## A) Clean

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B) Strict compile: application

```text
javac -Xlint:all -Werror -d out src/taskmanagerjdbc/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/taskmanagerjdbc/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" taskmanagerjdbc.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" taskmanagerjdbc.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `TaskTest` | Title/status/ID validation, optional description and due date, atomic updates, final class |
| `InMemoryTaskRepositoryTest` | Sequential and explicit IDs, duplicates, CRUD, status filtering, unmodifiable lists, defensive copies, ID exhaustion at `Long.MAX_VALUE` |
| `TaskServiceTest` | Create/update/status/filter/delete workflows, missing-task handling, no partial mutation on failed updates, end-to-end lifecycle |
| `DatabaseConnectionTest` | URL validation and the no-driver failure path: wrapped `SQLException` with helpful context that never exposes the password |
| `JdbcRepositoryStaticTest` | Executable argument validation (runs before any JDBC work) plus a static source review: `PreparedStatement`, try-with-resources, generated-key checks, `?` placeholders, no SQL concatenation, no hardcoded credentials |
| `MainTest` | Exit codes and output for `help`, `demo`, `in-memory-demo`, `validation-demo`, `jdbc-info`, no-args default, and unknown commands |

## E) Run the CLI demos

```text
java -cp out taskmanagerjdbc.Main help
java -cp out taskmanagerjdbc.Main demo
java -cp out taskmanagerjdbc.Main in-memory-demo
java -cp out taskmanagerjdbc.Main validation-demo
java -cp out taskmanagerjdbc.Main jdbc-info
```

All of these must exit 0 (the validation-demo failures are intentional demonstrations). `java -cp out taskmanagerjdbc.Main bogus` must print an error to stderr and exit non-zero.

## F) Scripts

Linux/macOS/Git Bash:

```text
./scripts/test.sh
```

Windows PowerShell:

```text
.\scripts\test.ps1
```

Both scripts clean, strict-compile the app and tests, run the full test suite, run all four demo commands, and remove `out/` and `test-out/` afterward.

## G) Real JDBC integration

Real JDBC testing is **not** part of the automated suite and has not been run. To test `JdbcTaskRepository` against a real database you must supply, on your own machine:

1. A database server or embedded database.
2. Its JDBC driver on the runtime classpath.
3. The `tasks` table from README.md (adjust identity syntax for your database).
4. A valid JDBC URL and credentials passed to `new DatabaseConnection(...)`.

Without those, only the failure path (`SQLException` when no driver is available) and the argument validation are exercised — which is exactly what the automated tests check.

## H) Cleanup

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Manual edge cases worth trying

- `addTask("   ", ...)` → `IllegalArgumentException` (blank title)
- `updateTaskStatus(id, null)` → `IllegalArgumentException`
- `findById(0)` / `delete(0)` → `IllegalArgumentException` (stored IDs must be positive)
- Update or delete an unknown positive ID → returns `false`
- Mutate a returned `Task` → stored record unchanged
- Add to a returned list → `UnsupportedOperationException`
- Add a task with explicit ID `Long.MAX_VALUE`, then request a generated ID → `IllegalStateException`
