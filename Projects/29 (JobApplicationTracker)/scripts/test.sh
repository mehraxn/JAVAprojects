#!/usr/bin/env bash
# Full local validation: clean, strict compile, tests, CLI smoke test.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "[1/5] Cleaning generated folders..."
rm -rf out test-out

echo "[2/5] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/jobapplicationtracker/*.java

echo "[3/5] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/jobapplicationtracker/*.java

# The JVM classpath separator is ':' on Linux/macOS but ';' on Windows,
# so pick the right one when this script runs under Git Bash/MSYS/Cygwin.
SEP=":"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=";";;
esac

echo "[4/5] Running automated tests..."
java -cp "out${SEP}test-out" jobapplicationtracker.TestRunner

echo "[5/5] Running CLI smoke test (demo)..."
java -cp out jobapplicationtracker.Main demo

echo
echo "All validation steps passed."
echo "Note: out/ and test-out/ contain generated classes and are gitignored."
