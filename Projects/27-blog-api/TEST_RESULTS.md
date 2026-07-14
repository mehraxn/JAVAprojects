# Test Results

Date: 2026-07-11

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 LTS |
| Strict application compile | PASS | `javac -Xlint:all -Werror` |
| Strict test compile | PASS | `javac -Xlint:all -Werror` |
| Automated tests | PASS | 125 checks across 5 suites |
| Service-layer tests | PASS | Users, posts, comments, search, update, delete, cleanup |
| JSON serialization tests | PASS | Escaping, arrays, models, and error JSON |
| HTTP smoke tests | PASS | 44 loopback checks using Java `HttpClient` |
| Unknown route JSON 404 | PASS | `/unknown` returned JSON, not HTML |
| Main demo | PASS | `Main demo` completed successfully |

## Known limitations

- In-memory API only.
- No database.
- No authentication.
- No pagination.
- No production JSON parser/framework.
- No TLS.
- No deployment packaging.
- Intended as a Java backend/API learning project.
