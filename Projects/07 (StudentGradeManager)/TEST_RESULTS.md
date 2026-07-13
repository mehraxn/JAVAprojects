# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 Microsoft build |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/studentgrademanager/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/studentgrademanager/*.java` |
| Automated tests | PASS | 5 test classes, 146 assertion checks |
| Student tests | PASS | 46 checks for validation, grade calculations, boundaries, final class, and snapshots |
| GradeBook tests | PASS | 41 checks for student/grade/search/ranking workflows |
| Report tests | PASS | 24 checks for transcript, class, and subject reports |
| Statistics tests | PASS | Covered by Student, GradeBook, and Report tests |
| Defensive snapshot tests | PASS | 22 checks proving public data cannot mutate internal state |
| Main CLI tests | PASS | 13 checks for help/demo/grade/report/ranking/search/validation and invalid command |
| Main demo | PASS | `java -cp out studentgrademanager.Main demo` |
| Grade demo | PASS | `java -cp out studentgrademanager.Main grade-demo` |
| Report demo | PASS | `java -cp out studentgrademanager.Main report-demo` |
| Ranking demo | PASS | `java -cp out studentgrademanager.Main ranking-demo` |
| PowerShell script | PASS | `powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\test.ps1` |
| Bash script | NOT RUN | `bash` points to WSL, but no WSL distribution is installed, so `scripts/test.sh` did not execute |

## Known limitations

- In-memory student grade manager only.
- No database.
- No HTTP API.
- No authentication/users.
- No file import/export.
- No weighted grading.
- No production school system guarantees.
- Intended as a Java OOP/service-layer learning project.
