# Contacts REST API Testing

All commands run from the project root. The automated tests need no running server — the HTTP tests start their own server on a free port (port 0) and stop it afterward.

## A) Clean

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B) Strict compile: application

```text
javac -Xlint:all -Werror -d out src/contactsrestapi/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/contactsrestapi/*.java
```

## D) Run the automated tests

Linux/macOS/Git Bash on Linux/macOS:

```text
java -cp "out:test-out" contactsrestapi.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" contactsrestapi.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `ContactTest` | Field validation, trimming, atomic updates, `copy()`, final class |
| `InMemoryContactRepositoryTest` | Save/find/delete, duplicate IDs, insertion order, defensive copies, unmodifiable lists |
| `ContactServiceTest` | Sequential IDs, CRUD, missing-ID handling, case-insensitive search, pagination bounds, failed updates not corrupting state |
| `JsonUtilTest` | Escaping of quotes, backslashes, newlines, tabs, carriage returns, control characters; list and error JSON |
| `ContactHttpServerTest` | 201/200/204 happy paths, JSON 400/404/405/413, `Allow` and `Location` headers, unknown routes returning JSON (never HTML), duplicate form fields, oversized bodies |
| `MainTest` | Exit codes and output for `help`, `demo`, `service-demo`, `http-demo`, unknown commands, and bad `server` arguments |

## E) Run the CLI demos

```text
java -cp out contactsrestapi.Main help
java -cp out contactsrestapi.Main demo
java -cp out contactsrestapi.Main service-demo
java -cp out contactsrestapi.Main http-demo
```

All of these must exit 0. `java -cp out contactsrestapi.Main bogus` and `java -cp out contactsrestapi.Main server bad` must exit non-zero.

## F) Run the server manually

```text
java -cp out contactsrestapi.Main server 8082
```

In another terminal:

```text
curl -i -X POST -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+123&notes=Developer" http://localhost:8082/contacts
curl -i http://localhost:8082/contacts
curl -i "http://localhost:8082/contacts?q=ada&offset=0&limit=10"
curl -i http://localhost:8082/contacts/C-1
curl -i -X PUT -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+456&notes=Updated" http://localhost:8082/contacts/C-1
curl -i -X DELETE http://localhost:8082/contacts/C-1
curl -i http://localhost:8082/unknown
```

Expected: 201, 200, 200, 200, 200, 204, and finally a JSON 404 (`{"error":"Endpoint not found."}`) for `/unknown`. A later `GET /contacts/C-1` returns a JSON 404. Stop the server with Ctrl+C.

## G) Scripts

Linux/macOS/Git Bash:

```text
./scripts/test.sh
```

Windows PowerShell:

```text
.\scripts\test.ps1
```

Both scripts clean, strict-compile the app and tests, run the full test suite, run the `demo` and `service-demo` commands, and remove `out/` and `test-out/` afterward.

## H) Cleanup

Linux/macOS/Git Bash:

```text
rm -rf out test-out
```

Windows PowerShell:

```text
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Manual edge cases worth trying

- `POST /contacts` without a `name` field → 400 JSON
- `GET /contacts?limit=abc`, `limit=0`, or `offset=-1` → 400 JSON
- `PATCH /contacts` → 405 JSON with `Allow: GET, POST`
- `POST /contacts/C-1` → 405 JSON with `Allow: GET, PUT, DELETE`
- `GET /contacts-extra`, `/api/contacts`, `/contacts/C-1/extra` → 404 JSON
- Body over 65,536 bytes → 413 JSON
- Duplicate form field (`name=A&name=B`) → 400 JSON
- Restart the process and confirm storage is intentionally empty
