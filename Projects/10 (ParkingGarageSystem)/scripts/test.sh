#!/usr/bin/env bash
# Full validation for the Parking Garage System.
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "== Cleaning build output =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/parkinggaragesystem/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/parkinggaragesystem/*.java

echo "== Running automated tests =="
java -cp "out:test-out" parkinggaragesystem.TestRunner

echo "== CLI demos =="
for command in demo parking-demo exit-demo fee-demo full-garage-demo report-demo validation-demo; do
    echo "--- Main $command ---"
    java -cp out parkinggaragesystem.Main "$command"
done

echo "== Cleaning build output =="
rm -rf out test-out

echo "== All checks passed =="
