#!/usr/bin/env bash
set -euo pipefail
cleanup() { rm -rf out test-out; }
trap cleanup EXIT
cleanup
javac -Xlint:all -Werror -d out src/blogapi/*.java
javac -Xlint:all -Werror -cp out -d test-out tests/blogapi/*.java
test_classpath="out:test-out"
case "${OSTYPE:-}" in
  msys*|cygwin*) test_classpath="out;test-out" ;;
esac
java -cp "$test_classpath" blogapi.TestRunner
java -cp out blogapi.Main demo
java -cp out blogapi.Main service-demo
