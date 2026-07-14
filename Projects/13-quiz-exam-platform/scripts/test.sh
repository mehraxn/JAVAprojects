#!/usr/bin/env bash
# Validates the Quiz / Exam Platform project: strict compile, tests, CLI demos.
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
javac -Xlint:all -Werror -d out src/quizexamplatform/*.java

echo "== Strict compile: tests =="
javac -Xlint:all -Werror -cp out -d test-out tests/quizexamplatform/*.java

echo "== Automated tests =="
java -cp "out${SEP}test-out" quizexamplatform.TestRunner

echo "== CLI demo =="
java -cp out quizexamplatform.Main demo

echo "== CLI attempt-demo =="
java -cp out quizexamplatform.Main attempt-demo

echo "== CLI validation-demo =="
java -cp out quizexamplatform.Main validation-demo

echo "== CLI scoreboard-demo =="
java -cp out quizexamplatform.Main scoreboard-demo

echo "== Clean up generated files =="
rm -rf out test-out

echo "ALL CHECKS PASSED"
