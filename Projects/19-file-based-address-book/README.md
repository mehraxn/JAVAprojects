# File-Based Address Book

An educational Java file-based address book with contact CRUD/search and UTF-8 TSV persistence, built entirely with the Java standard library. No framework, database, or external dependency — plain `javac`/`java` is enough to build, run, and test it.

## What it demonstrates

- A validated, immutable-by-convention `Contact` domain model (final class)
- Add / update / delete / search contacts, with deterministic sorted listings
- **Defensive copies** — contacts returned from the address book cannot mutate its internal state
- Conflict-safe imports that check every incoming contact before changing anything
- UTF-8 TSV persistence with malformed-file and duplicate-ID rejection
- **Atomic-style saving** — writes to a temp file, then replaces the target
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Add, list (sorted), update, delete, and search contacts
- Case-insensitive search across ID, name, phone, and email
- Save/load contacts to/from a UTF-8 TSV file
- Import contacts safely (a single conflict aborts the whole import)
- Command-based CLI demos

## Main classes

- `Contact` — validated contact model with a copy constructor and `copy()`.
- `AddressBook` — contact CRUD, search, sort, and conflict-safe import; returns defensive copies.
- `FileStore` — UTF-8 TSV load/save (atomic-style), plus export/import helpers.
- `Main` — CLI commands (`help`, `demo`, `file-demo`, `import-demo`, `validation-demo`).

## Quick start

```text
javac -Xlint:all -Werror -d out src/filebasedaddressbook/*.java

java -cp out filebasedaddressbook.Main help
java -cp out filebasedaddressbook.Main demo
java -cp out filebasedaddressbook.Main file-demo
java -cp out filebasedaddressbook.Main import-demo
java -cp out filebasedaddressbook.Main validation-demo
```

Running with no command prints the usage text. `demo` creates, lists, searches, updates, and deletes contacts. `file-demo` saves to a temporary TSV file, reloads it, verifies the round trip, and deletes the file. `import-demo` shows a successful import and a conflicting import being rejected without partial changes. `validation-demo` intentionally triggers validation failures and exits 0 because the rejections are the point. `Main.run(args, out, err)` returns an exit code (0 for valid commands, non-zero for unknown ones) and only `main` calls `System.exit`.

## Validation rules

- ID, name, phone, and email cannot be null or blank; values are trimmed and may not contain tabs or line breaks.
- Failed updates never partially mutate a contact — all new values are validated first.

**Email validation is simple and educational, not production-grade.** The rules are: exactly one `@`, a non-blank local part before it, and a non-blank domain after it that contains a dot and does not start or end with a dot. This rejects obvious mistakes but is not a full RFC-compliant check.

## File format and persistence

`FileStore` reads and writes UTF-8 TSV with four tab-separated fields per line: `id`, `name`, `phone`, `email`. Because tabs and line breaks are rejected inside fields, no escaping is needed. Missing, empty, and blank-line-only files load as an empty list; blank lines between rows are ignored. Malformed rows (wrong field count), duplicate IDs, and invalid contact data are rejected with an `IOException` naming the line.

**Atomic-style save:** `save` writes to a temporary file in the target directory, then moves it over the target with `REPLACE_EXISTING` (and `ATOMIC_MOVE` where the filesystem supports it, falling back automatically). If saving fails halfway, the original file is left untouched and the temp file is cleaned up.

Generated TSV/TXT files are ignored by Git (`.gitignore`); the demos only ever write to system temp files that they delete.

Example TSV (tabs shown as `<TAB>`):

```text
C001<TAB>Amira Khan<TAB>+49 111 222<TAB>amira@example.com
C002<TAB>Luca Rossi<TAB>+39 333 444<TAB>luca@example.com
```

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper + `TestRunner`) covering the model, address book (including defensive-copy proofs), file store, and CLI:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/filebasedaddressbook/*.java
java -cp "out;test-out" filebasedaddressbook.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- `Map` and `List` collections, encapsulation, and validation
- Immutability by convention, copy constructors, and defensive copies
- Sorting with `Comparator` and unmodifiable collection views
- `Path`, `Files`, UTF-8, and `IOException`
- Atomic-style file replacement with `Files.move`
- Exit codes and testable CLI entry points

## Limitations

- Local file-based storage only — no database and no cloud sync
- No HTTP API, login, or authentication
- No GUI
- No advanced contact fields (multiple numbers, addresses, groups)
- Email validation is simple/educational, not production-grade
- Intended as a Java file-I/O learning project, not production address-book software

## Possible future improvements

- CSV quoting support for richer fields
- Multiple phone numbers and addresses
- Contact groups
- Backup-file creation and duplicate detection by email or phone
