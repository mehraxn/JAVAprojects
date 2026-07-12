# File-Based Address Book Testing

All commands run from the project root. The automated tests need only a JDK — file tests use `Files.createTempFile` and delete their files; nothing is written inside the repository.

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
javac -Xlint:all -Werror -d out src/filebasedaddressbook/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/filebasedaddressbook/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" filebasedaddressbook.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" filebasedaddressbook.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `ContactTest` | Field validation, trimming, simple email rules, atomic updates, `copy()` and copy constructor, `matches` |
| `AddressBookTest` | Add/find/update/delete, duplicate IDs, deterministic sorting, case-insensitive search, defensive copies (get/find/list/search results and input contacts cannot mutate state), conflict-safe import |
| `FileStoreTest` | UTF-8 round trip (accents and non-Latin), malformed rows, duplicate IDs, invalid data, missing/empty/blank files, atomic save leaving no temp files, export/import through an AddressBook |
| `MainTest` | Exit codes and output for `help`, `demo`, `file-demo`, `import-demo`, `validation-demo`, no-args default, and unknown commands |

## E) Run the CLI demos

```text
java -cp out filebasedaddressbook.Main help
java -cp out filebasedaddressbook.Main demo
java -cp out filebasedaddressbook.Main file-demo
java -cp out filebasedaddressbook.Main import-demo
java -cp out filebasedaddressbook.Main validation-demo
```

All of these must exit 0 (the validation-demo failures are intentional demonstrations). `java -cp out filebasedaddressbook.Main bogus` must print an error to stderr and exit non-zero.

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

## G) Cleanup

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Expected file format

Each non-blank line contains exactly four tab-separated fields:

```text
contact-id<TAB>name<TAB>phone<TAB>email
```

Tabs and line breaks are rejected inside fields, so no escaping is required.

## Manual edge cases worth trying

- Create a contact with `user@example` (no dot in domain) → `IllegalArgumentException`
- Create a contact with `a@@b.co` (two `@`) → `IllegalArgumentException`
- Add two contacts with the same ID → `IllegalArgumentException`
- `updateContact` on a missing ID → returns `false`
- Update with an invalid email → throws, stored contact unchanged
- Mutate a contact returned by `getContact`/`findById`/`listContactsSorted`/`searchContacts` → stored state unchanged
- Import a batch where one ID conflicts → nothing from the batch is added
- Load a TSV with a duplicate ID or wrong field count → `IOException` naming the line
- Save over an existing file → contents fully replaced, no `.tsv.tmp` files left behind
