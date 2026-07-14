#!/usr/bin/env bash
set -euo pipefail
trap 'rm -rf out test-out' EXIT
rm -rf out test-out
javac -Xlint:all -Werror -d out src/hospitalqueuemanagement/*.java
javac -Xlint:all -Werror -cp out -d test-out tests/hospitalqueuemanagement/*.java
java -cp "out:test-out" hospitalqueuemanagement.TestRunner
java -cp out hospitalqueuemanagement.Main demo
java -cp out hospitalqueuemanagement.Main queue-demo
java -cp out hospitalqueuemanagement.Main emergency-demo
java -cp out hospitalqueuemanagement.Main status-demo
java -cp out hospitalqueuemanagement.Main statistics-demo
java -cp out hospitalqueuemanagement.Main validation-demo
