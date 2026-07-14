# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/contactsrestapi/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/contactsrestapi/*.java` |
| Automated tests | PASS | 213 checks, 0 failures (`contactsrestapi.TestRunner`) |
| Contact model tests | PASS | 39 checks: validation, trimming, atomic updates, final class |
| Repository/service tests | PASS | 28 + 40 checks: CRUD, defensive copies, search, pagination |
| JSON serialization tests | PASS | 23 checks: escaping and error JSON |
| HTTP smoke tests | PASS | 59 checks against a live `HttpServer` on an ephemeral port |
| Unknown route JSON 404 | PASS | `/unknown`, `/`, `/api/contacts`, `/contacts-extra`, `/contacts/C-1/extra` all return `{"error":"Endpoint not found."}`; also verified manually with curl |
| Main demo | PASS | `help`, `demo`, `service-demo`, `http-demo` exit 0; bad commands/ports exit 1 (24 checks in `MainTest`) |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- In-memory API only — no database or persistence.
- No authentication, authorization, TLS, or rate limiting.
- Request bodies are URL-encoded form fields; there is no production JSON parser or framework.
- Pagination is simple offset/limit with no metadata (total counts, links).
- Email/phone validation is intentionally basic.
- No deployment packaging.
- Intended as a Java REST/API learning project, not production software.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- The HTTP tests start their own server on port 0 (a free ephemeral port) and stop it when done; no processes are left running.
