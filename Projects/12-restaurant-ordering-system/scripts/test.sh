#!/usr/bin/env bash
# Validates the Restaurant Ordering System project: strict compile, tests, CLI demos.
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
javac -Xlint:all -Werror -d out src/restaurantorderingsystem/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/restaurantorderingsystem/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" restaurantorderingsystem.TestRunner

echo "== CLI demo =="
java -cp out restaurantorderingsystem.Main demo

echo "== CLI order-demo =="
java -cp out restaurantorderingsystem.Main order-demo

echo "== CLI discount-demo =="
java -cp out restaurantorderingsystem.Main discount-demo

echo "== CLI status-demo =="
java -cp out restaurantorderingsystem.Main status-demo

echo "== CLI validation-demo =="
java -cp out restaurantorderingsystem.Main validation-demo

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
