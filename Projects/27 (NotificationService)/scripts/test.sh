#!/usr/bin/env bash
# Full local validation: clean, strict compile, tests, CLI demos.
set -euo pipefail
cd "$(dirname "$0")/.."

echo "[1/6] Cleaning generated folders..."
rm -rf out test-out

echo "[2/6] Compiling application with strict flags..."
javac -Xlint:all -Werror -d out src/notificationservice/*.java

echo "[3/6] Compiling tests with strict flags..."
javac -Xlint:all -Werror -cp out -d test-out tests/notificationservice/*.java

# The JVM classpath separator is ':' on Linux/macOS but ';' on Windows,
# so pick the right one when this script runs under Git Bash/MSYS/Cygwin.
SEP=":"
case "$(uname -s)" in
    MINGW*|MSYS*|CYGWIN*) SEP=";";;
esac

echo "[4/6] Running automated tests..."
java -cp "out${SEP}test-out" notificationservice.TestRunner

echo "[5/6] Running CLI demo..."
java -cp out notificationservice.Main demo

echo "[6/6] Running retry and validation demos..."
java -cp out notificationservice.Main retry-demo
java -cp out notificationservice.Main validation-demo

echo
echo "All validation steps passed."
echo "Note: out/ and test-out/ contain generated classes and are gitignored."
