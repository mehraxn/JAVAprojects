#!/usr/bin/env bash
# Full validation for the Bank Account Simulator.
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "== Cleaning build output =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/bankaccountsimulator/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/bankaccountsimulator/*.java

echo "== Running automated tests =="
java -cp "out:test-out" bankaccountsimulator.TestRunner

echo "== CLI demos =="
for command in demo deposit-demo withdraw-demo transfer-demo statement-demo validation-demo; do
    echo "--- Main $command ---"
    java -cp out bankaccountsimulator.Main "$command"
done

echo "== Cleaning build output =="
rm -rf out test-out

echo "== All checks passed =="
