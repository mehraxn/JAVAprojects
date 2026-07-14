#!/usr/bin/env bash
# Full validation for the Library Management System.
# Strict-compiles the app and tests, runs the test suite, then every CLI demo.
set -euo pipefail

cd "$(dirname "$0")/.."

echo "== Cleaning build output =="
rm -rf out test-out

echo "== Strict compile: application =="
javac -Xlint:all -Werror -d out src/librarymanagementsystem/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/librarymanagementsystem/*.java

echo "== Running automated tests =="
java -cp "out:test-out" librarymanagementsystem.TestRunner

echo "== CLI demos =="
for command in demo borrow-demo return-demo search-demo overdue-demo history-demo validation-demo; do
    echo "--- Main $command ---"
    java -cp out librarymanagementsystem.Main "$command"
done

echo "== Cleaning build output =="
rm -rf out test-out

echo "== All checks passed =="
