# Test Results

Date: 2026-07-14

## Environment

| Item | Result |
|---|---|
| Java version | OpenJDK 21.0.11 LTS (Microsoft build 21.0.11+10-LTS) |
| Maven version | Apache Maven 3.9.16 |
| Maven wrapper present | YES — Maven Wrapper 3.3.4, Maven 3.9.16 distribution |

## Baseline

Before repair, `mvn clean test` failed during main compilation with `release version 25 not supported` because the POM requested Java 25 while Maven ran on Java 21. The failure occurred before any tests executed.

## Validation

| Check | Result | Notes |
|---|---:|---|
| Clean generated files | PASS | `target/`, class, log, temporary, and `.DS_Store` artifacts removed after validation |
| Java version compatibility | PASS | Compiled with `maven.compiler.release=21` |
| Maven wrapper | PASS | Generated and validated with `.\mvnw.cmd clean test` |
| Full Maven test suite | PASS | 94 tests, 0 failures, 0 errors, 0 skipped |
| Base/professor tests | PASS | 38 professor tests, 0 failures; 8 example tests also pass |
| Custom validation tests | PASS | 10 tests |
| Custom simulation tests | PASS | 7 tests |
| Custom split/multisplit tests | PASS | 12 tests |
| Custom delete tests | PASS | 8 tests |
| Custom builder/toString tests | PASS | 11 tests |
| `scripts/test.sh` | PASS | Run with Git Bash; 94 tests passed |
| `scripts/test.ps1` | PASS | Run with `-ExecutionPolicy Bypass`; 94 tests passed |
| JaCoCo report | PASS | `.\mvnw.cmd clean test jacoco:report`; report generated before final cleanup |

## Commands Run

```text
java -version
mvn -version
mvn clean test                              # baseline: failed on Java release 25
mvn test                                    # 46 existing tests passed after core repair
mvn clean test                              # 94 tests passed after custom tests
mvn -N wrapper:wrapper
.\mvnw.cmd clean test                       # 94 tests passed
C:\Program Files\Git\bin\bash.exe scripts/test.sh  # 94 tests passed
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\test.ps1  # 94 tests passed
.\mvnw.cmd clean test jacoco:report         # 94 tests passed; report generated
```

The unqualified `bash scripts/test.sh` command resolved to WSL on this Windows host, where no Linux distribution is installed. The same script was then successfully validated with Git Bash. Direct PowerShell script invocation was blocked by the host execution policy, so the documented bypass invocation was used successfully.

## Known Limitations

- Educational, in-memory hydraulic model rather than a physical-fluid solver.
- Simulation starts at the first source in insertion order.
- Recursive flow simulation assumes an acyclic network.
- No database, REST API, frontend, or production deployment layer.
