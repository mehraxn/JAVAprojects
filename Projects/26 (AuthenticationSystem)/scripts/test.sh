#!/usr/bin/env bash
# Full local validation: clean, strict compile, tests, CLI demos.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "[1/6] Cleaning generated folders..."
rm -rf out test-out

echo "[2/6] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/authenticationsystem/*.java

echo "[3/6] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/authenticationsystem/*.java

# The JVM classpath separator is ':' on Linux/macOS but ';' on Windows,
# so pick the right one when this script runs under Git Bash/MSYS/Cygwin.
SEP=":"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=";";;
esac

echo "[4/6] Running automated tests (PBKDF2 makes this take a few seconds)..."
java -cp "out${SEP}test-out" authenticationsystem.TestRunner

echo "[5/6] Running CLI demo..."
java -cp out authenticationsystem.Main demo

echo "[6/6] Running authorization and expiry demos..."
java -cp out authenticationsystem.Main authorization-demo
java -cp out authenticationsystem.Main expiry-demo

echo
echo "All validation steps passed."
echo "Note: out/ and test-out/ contain generated classes and are gitignored."
