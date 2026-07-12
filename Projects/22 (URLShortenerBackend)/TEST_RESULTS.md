# Test Results

Date: 2026-07-12

## Local validation

| Check | Result | Notes |
|---|---:|---|
| Java version | PASS | OpenJDK 21.0.11 (Microsoft build), Windows 11 |
| Strict application compile | PASS | `javac -Xlint:all -Werror -d out src/urlshortenerbackend/*.java` |
| Strict test compile | PASS | `javac -Xlint:all -Werror -cp out -d test-out tests/urlshortenerbackend/*.java` |
| Automated tests | PASS | 189 checks, 0 failures (`urlshortenerbackend.TestRunner`) |
| Service-layer tests | PASS | 10 + 29 + 35 checks: code generation, URL/code validation, redirects, hit counts, snapshots |
| CSV persistence tests | PASS | 22 checks: round trip, quoting, malformed/duplicate/invalid rows, missing/empty files |
| HTTP smoke tests | PASS | 66 checks against a live `HttpServer` on an ephemeral port |
| Unknown route JSON 404 | PASS | `/unknown`, `/`, `/api/links`, `/links-extra`, `/links/{code}`, `/r-extra`, `/r/abc/extra` all return `{"error":"Endpoint not found."}`; also verified manually with curl |
| Redirect test | PASS | `GET /r/docs` returns 302 with `Location: https://example.com/docs` and increments the hit count (automated + manual curl) |
| Main demo | PASS | `help`, `demo`, `service-demo`, `http-demo` exit 0; bad commands/ports exit 1 (27 checks in `MainTest`) |
| CSV demo | PASS | Saves to a temp CSV, reloads, verifies entries + hit counts, deletes the file |
| scripts/test.sh | PASS | Full pipeline in Git Bash (compile, tests, demos, cleanup) |
| scripts/test.ps1 | PASS | Full pipeline in Windows PowerShell 5.1 |

## Known limitations

- In-memory backend with optional local CSV persistence — no database.
- No authentication, authorization, or rate limiting.
- No analytics dashboard and no custom domain support.
- Request bodies are URL-encoded form fields; there is no production JSON parser or framework.
- Generated codes are sequential, not unpredictable.
- No TLS and no deployment packaging.
- Intended as a Java backend/API learning project, not production software.

## Notes

- Tests were run on Windows 11 with the classpath separator `;`. On Linux/macOS use `out:test-out`.
- The HTTP tests start their own server on port 0 (a free ephemeral port) and stop it when done; CSV tests use temporary files and delete them. No processes or files are left behind.
