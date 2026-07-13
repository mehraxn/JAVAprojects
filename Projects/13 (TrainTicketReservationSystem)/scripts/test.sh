#!/usr/bin/env bash
# Full validation for the Train Ticket Reservation System.
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "== Cleaning build output =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/trainticketreservationsystem/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/trainticketreservationsystem/*.java

echo "== Running automated tests =="
java -cp "out:test-out" trainticketreservationsystem.TestRunner

echo "== CLI demos =="
for command in demo reservation-demo cancellation-demo search-demo full-train-demo validation-demo; do
    echo "--- Main $command ---"
    java -cp out trainticketreservationsystem.Main "$command"
done

echo "== Cleaning build output =="
rm -rf out test-out

echo "== All checks passed =="
