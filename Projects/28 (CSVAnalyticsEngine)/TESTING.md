# Testing the CSV Analytics Engine

This guide lists the exact commands used to validate the project. The results of actually running them are recorded in `TEST_RESULTS.md`. Everything is dependency-free: only a JDK (21 used for validation) is required — no Maven, Gradle, or JUnit.

Quick version: run `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell) — they perform steps A–F below.

## A) Clean

Linux/macOS/Git Bash:

```bash
rm -rf out test-out filtered.csv
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
Remove-Item filtered.csv -ErrorAction SilentlyContinue
```

## B) Strict compile — application

```text
javac -Xlint:all -Werror -d out src/csvanalyticsengine/*.java
```

Every warning is an error; the build must be completely clean.

## C) Strict compile — tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/csvanalyticsengine/*.java
```

## D) Run automated tests

Linux/macOS/Git Bash (classpath separator `:`):

```bash
java -cp "out:test-out" csvanalyticsengine.TestRunner
```

Windows PowerShell (classpath separator `;`):

```powershell
java -cp "out;test-out" csvanalyticsengine.TestRunner
```

Expected: `PASS` per test, a summary like `Tests passed: 64, failed: 0 (149 checks total)`, `RESULT: PASS`, and exit code 0. Any failure prints `FAIL` with the reason and exits 1. All file-based tests use `Files.createTempFile` and delete their temp files — no CSV files are left behind.

## E) CLI demo

```text
java -cp out csvanalyticsengine.Main demo
```

Expected: loads a temporary sample CSV, prints summary, statistics, group counts, filtered rows, and an export round trip, then `Demo completed successfully (temporary files deleted).` with exit code 0.

## F) Manual CLI workflow

```text
java -cp out csvanalyticsengine.Main help
java -cp out csvanalyticsengine.Main summary examples/sales.csv
java -cp out csvanalyticsengine.Main stats examples/sales.csv amount
java -cp out csvanalyticsengine.Main group examples/sales.csv category
java -cp out csvanalyticsengine.Main filter examples/sales.csv category Food
java -cp out csvanalyticsengine.Main export-filtered examples/sales.csv filtered.csv category Food
```

Expected for `stats` on the sample data: 4 valid values, 1 missing, 1 invalid, min `-5.75`, max `35.00`, sum `45.95`, average `11.4875`. The exported `filtered.csv` can itself be read back with `summary` (round trip) and is gitignored.

## G) Error behavior

Each of these prints a clear message to stderr and exits non-zero (check `echo $?` on Linux/macOS or `$LASTEXITCODE` in PowerShell):

```text
java -cp out csvanalyticsengine.Main summary missing.csv          # missing file
java -cp out csvanalyticsengine.Main stats examples/sales.csv nosuchcolumn   # unknown column
java -cp out csvanalyticsengine.Main frobnicate                   # unknown command
java -cp out csvanalyticsengine.Main stats                        # missing arguments
```

Malformed CSV test — create a broken file and load it:

```bash
printf 'id,name\n1,"Unclosed\n' > malformed.csv
java -cp out csvanalyticsengine.Main summary malformed.csv        # exits non-zero
```

## H) Scripts

Linux/macOS/Git Bash:

```bash
./scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

Both scripts: clean → strict compile app → strict compile tests → TestRunner → demo → summary/stats/group against `examples/sales.csv`, stopping at the first failure. `test.sh` picks the right classpath separator automatically, so it also works from Git Bash on Windows.

## I) Cleanup

Linux/macOS/Git Bash:

```bash
rm -rf out test-out filtered.csv malformed.csv
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
Remove-Item filtered.csv,malformed.csv -ErrorAction SilentlyContinue
```
