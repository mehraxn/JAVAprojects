#!/usr/bin/env bash
# Validates the Contacts REST API project: strict compile, automated tests, CLI demos.
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
javac -Xlint:all -Werror -d out src/contactsrestapi/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/contactsrestapi/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" contactsrestapi.TestRunner

echo "== CLI demo =="
java -cp out contactsrestapi.Main demo

echo "== CLI service-demo =="
java -cp out contactsrestapi.Main service-demo

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
