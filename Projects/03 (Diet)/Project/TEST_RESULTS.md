# Test Results

Date: 2026-07-14

## Environment

| Item | Result |
|---|---|
| Java version | openjdk version "21.0.11" 2026-04-21 LTS |
| Maven version | Apache Maven 3.9.16 |
| Maven wrapper present | YES |
| Maven wrapper version | Apache Maven 3.9.16 |
| Operating system | Windows 11 |

## Baseline Before Fixes

| Command | Result | Notes |
|---|---:|---|
| `java -version` | PASS | Java 21.0.11 was available. |
| `mvn -version` | PASS | Maven 3.9.16 was available. |
| `mvn clean test` | FAIL | Original `pom.xml` targeted Java release 25, which was not supported by local Java 21. |

## Validation

| Check | Result | Notes |
|---|---:|---|
| Clean generated files | PASS | Final cleanup removes `target/` and generated class/report output. |
| Java version compatibility | PASS | `pom.xml` now targets Java 21. |
| Maven wrapper | PASS | Generated with `mvn -N wrapper:wrapper`; `mvnw.cmd` was patched for this PowerShell environment. |
| Full Maven test suite | PASS | `mvn clean test`: 49 tests, 0 failures, 0 errors, 0 skipped. |
| Maven wrapper test suite | PASS | `.\mvnw.cmd clean test`: 49 tests, 0 failures, 0 errors, 0 skipped. |
| R1 raw material tests | PASS | Custom R1 test class: 4 tests passed. |
| R2 product tests | PASS | Custom R2 test class: 3 tests passed. |
| R3 recipe tests | PASS | Custom R3 test class: 5 tests passed. |
| R4 menu tests | PASS | Custom R4 test class: 6 tests passed. |
| R5 restaurant tests | PASS | Custom R5 test class: 6 tests passed. |
| R6 customer tests | PASS | Custom R6 test class: 3 tests passed. |
| R7 order tests | PASS | Custom R7 test class: 5 tests passed. |
| R8 query tests | PASS | Custom R8 test class: 4 tests passed. |
| Validation tests | PASS | Custom validation test class: 4 tests passed. |
| End-to-end workflow test | PASS | Custom end-to-end test class: 1 test passed. |
| scripts/test.sh | NOT RUN | Bash maps to WSL here, and WSL has no installed distribution. |
| scripts/test.ps1 | PASS | Direct execution was blocked by local PowerShell policy; `powershell -ExecutionPolicy Bypass -File .\scripts\test.ps1` passed with 49 tests. |
| JaCoCo report | PASS | `.\mvnw.cmd clean test jacoco:report` passed; report generated under `target/site/jacoco/`. |

## Commands Actually Run

```powershell
java -version
mvn -version
mvn clean test
mvn -N wrapper:wrapper
.\mvnw.cmd -version
.\mvnw.cmd clean test
.\scripts\test.ps1
powershell -ExecutionPolicy Bypass -File .\scripts\test.ps1
.\mvnw.cmd clean test jacoco:report
```

## Known Limitations

- Educational/local Java project.
- In-memory model only.
- No database.
- No REST API.
- No authentication.
- No frontend.
- No deployment setup.
- Payment method is recorded as an enum value only.
- No external payment integration.
