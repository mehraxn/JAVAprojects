#!/usr/bin/env bash
# Validates the Task Manager JDBC project: strict compile, automated tests, CLI demos.
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
javac -Xlint:all -Werror -d out src/taskmanagerjdbc/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/taskmanagerjdbc/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" taskmanagerjdbc.TestRunner

echo "== CLI demo =="
java -cp out taskmanagerjdbc.Main demo

echo "== CLI in-memory-demo =="
java -cp out taskmanagerjdbc.Main in-memory-demo

echo "== CLI validation-demo =="
java -cp out taskmanagerjdbc.Main validation-demo

echo "== CLI jdbc-info =="
java -cp out taskmanagerjdbc.Main jdbc-info

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
