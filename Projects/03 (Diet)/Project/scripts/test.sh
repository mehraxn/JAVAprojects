#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

if [[ -x "./mvnw" ]]; then
  ./mvnw clean test
elif [[ -f "./mvnw" ]]; then
  sh ./mvnw clean test
else
  mvn clean test
fi
