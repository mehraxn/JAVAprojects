# Test Results

Date: 2026-07-13

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | NOT RUN | JDK version |
| Strict application compile | NOT RUN | `javac -Xlint:all -Werror` |
| Strict test compile | NOT RUN | `javac -Xlint:all -Werror` |
| Automated tests | NOT RUN | Number of tests/checks |
| Student tests | NOT RUN | Validation and grade calculations |
| GradeBook tests | NOT RUN | Student/grade/search/ranking workflows |
| Report tests | NOT RUN | Transcript, class, and subject reports |
| Statistics tests | NOT RUN | Average/highest/lowest/pass/fail/letter grade |
| Defensive snapshot tests | NOT RUN | Public data cannot mutate internal state |
| Main CLI tests | NOT RUN | help/demo/grade/report/ranking/search/validation |
| Main demo | NOT RUN | Main demo command |
| Grade demo | NOT RUN | Grade recording workflow |
| Report demo | NOT RUN | Class and subject reports |
| Ranking demo | NOT RUN | Student ranking behavior |

## Known limitations

- In-memory student grade manager only.
- No database.
- No HTTP API.
- No authentication/users.
- No file import/export.
- No weighted grading.
- No production school system guarantees.
- Intended as a Java OOP/service-layer learning project.
