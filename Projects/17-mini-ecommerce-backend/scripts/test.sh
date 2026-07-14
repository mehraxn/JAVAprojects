#!/usr/bin/env bash
set -euo pipefail
rm -rf out test-out
javac -Xlint:all -Werror -d out src/miniecommercebackend/*.java
javac -Xlint:all -Werror -cp out -d test-out tests/miniecommercebackend/*.java
java -cp "out:test-out" miniecommercebackend.TestRunner
java -cp out miniecommercebackend.Main demo
java -cp out miniecommercebackend.Main checkout-demo
java -cp out miniecommercebackend.Main cancel-demo
java -cp out miniecommercebackend.Main failure-demo
