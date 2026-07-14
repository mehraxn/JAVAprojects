#!/usr/bin/env bash
# Validates the Expense Tracker project: strict compile, automated tests, CLI demos.
# Run from the project root: ./scripts/test.sh
set -euo pipefail

cd "$(dirname "$0")/.."

# Classpath separator: ':' on Linux/macOS, ';' on Windows (Git Bash/MSYS/Cygwin)
SEP=":"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=";" ;;
esac

echo "== Clean =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/expensetracker/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/expensetracker/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" expensetracker.TestRunner

echo "== CLI demo =="
java -cp out expensetracker.Main demo

echo "== CLI csv-demo =="
java -cp out expensetracker.Main csv-demo

echo "== CLI report-demo =="
java -cp out expensetracker.Main report-demo

echo "== CLI validation-demo =="
java -cp out expensetracker.Main validation-demo

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
