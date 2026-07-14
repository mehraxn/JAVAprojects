# Test Results

Date: 2026-07-14

## Environment

| Item | Result |
|---|---|
| Java version | OpenJDK 21.0.11 LTS (Microsoft build 21.0.11+10-LTS) |
| Maven version | Apache Maven 3.9.16 |
| Maven wrapper present | YES — Maven Wrapper 3.3.4, Maven 3.9.16 distribution |

## Baseline

Before repair, `mvn clean test` failed during main compilation with `release version 25 not supported`. The POM requested Java 25 while Maven ran on Java 21, so no baseline tests executed.

## Validation

| Check | Result | Notes |
|---|---:|---|
| Clean generated files | PASS | `target/`, class, log, temporary, IDE, and `.DS_Store` artifacts removed after validation |
| Java version compatibility | PASS | Compiled with `maven.compiler.release=21` |
| Maven wrapper | PASS | Generated and validated with `.\mvnw.cmd clean test` |
| Full Maven test suite | PASS | 65 discovered, 57 passed, 8 intentionally skipped, 0 failures/errors |
| Base/professor tests | PASS | 32 supplied tests discovered; 24 passed and 8 supplied tests are disabled |
| University/rector tests | PASS | Covered by 12 custom basic/validation tests |
| Student tests | PASS | IDs, formatting, trimming, invalid values, and missing lookup covered |
| Course tests | PASS | Codes, formatting, trimming, invalid values, and missing lookup covered |
| Registration tests | PASS | 6 custom tests, including idempotency and capacity |
| Exam/average tests | PASS | 10 custom tests |
| Top-three tests | PASS | 5 custom tests |
| Validation tests | PASS | Validation assertions included across all 33 custom tests |
| `scripts/test.sh` | PASS | Run with Git Bash; full suite passed |
| `scripts/test.ps1` | PASS | Run with `-ExecutionPolicy Bypass`; full suite passed |
| JaCoCo report | PASS | `.\mvnw.cmd clean test jacoco:report`; report generated before final cleanup |

## Commands Run

```text
java -version
mvn -version
mvn clean test                              # baseline: failed on Java release 25
mvn clean test                              # supplied suite passed after core repair
mvn clean test                              # 65 discovered after custom tests
mvn -N wrapper:wrapper
.\mvnw.cmd clean test                       # full suite passed
C:\Program Files\Git\bin\bash.exe scripts/test.sh  # full suite passed
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\test.ps1  # full suite passed
.\mvnw.cmd clean test jacoco:report         # full suite and report passed
```

Direct PowerShell script execution is restricted by the host policy, so the documented one-process execution-policy bypass was used successfully.

## Known Limitations

- Educational, local, in-memory model.
- Fixed limits: 1000 students, 50 courses, 25 courses per student, and 100 attendees per course.
- Linear lookup is appropriate for the assignment scale but not optimized for large datasets.
- No database, REST API, frontend, concurrency, or production deployment.
