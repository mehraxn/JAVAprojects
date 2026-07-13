#!/usr/bin/env bash
# Full validation for the Movie Ticket Booking System.
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "== Cleaning build output =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/movieticketbookingsystem/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/movieticketbookingsystem/*.java

echo "== Running automated tests =="
java -cp "out:test-out" movieticketbookingsystem.TestRunner

echo "== CLI demos =="
for command in demo booking-demo cancellation-demo full-showtime-demo availability-demo validation-demo; do
    echo "--- Main $command ---"
    java -cp out movieticketbookingsystem.Main "$command"
done

echo "== Cleaning build output =="
rm -rf out test-out

echo "== All checks passed =="
