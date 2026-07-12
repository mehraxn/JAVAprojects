#!/usr/bin/env bash
# Validates the File-Based Address Book project: strict compile, tests, CLI demos.
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
javac -Xlint:all -Werror -d out src/filebasedaddressbook/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/filebasedaddressbook/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" filebasedaddressbook.TestRunner

echo "== CLI demo =="
java -cp out filebasedaddressbook.Main demo

echo "== CLI file-demo =="
java -cp out filebasedaddressbook.Main file-demo

echo "== CLI import-demo =="
java -cp out filebasedaddressbook.Main import-demo

echo "== CLI validation-demo =="
java -cp out filebasedaddressbook.Main validation-demo

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
