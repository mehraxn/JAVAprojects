# URL Shortener Backend Testing

All commands run from the project root. The automated tests need no running server — the HTTP tests start their own server on a free port (port 0) and stop it afterward. CSV tests use `Files.createTempFile` and delete their files.

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
javac -Xlint:all -Werror -d out src/urlshortenerbackend/*.java
```

## C) Strict compile: tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/urlshortenerbackend/*.java
```

## D) Run the automated tests

Linux/macOS:

```text
java -cp "out:test-out" urlshortenerbackend.TestRunner
```

Windows (PowerShell or Git Bash — Windows Java uses `;`):

```text
java -cp "out;test-out" urlshortenerbackend.TestRunner
```

The runner prints per-suite PASS/FAIL counts and a final summary, and exits 0 only if every check passes.

### What the suites cover

| Suite | Coverage |
|---|---|
| `CodeGeneratorTest` | Code shape, Base62 characters, uniqueness across samples, skipping taken codes, determinism |
| `UrlEntryTest` | URL/scheme/host/port validation, short-code rules, hit counting, overflow, preserved counts, `copy()` |
| `ShortenerServiceTest` | Generated and custom links, duplicate rejection, resolve/hit counting, failed resolves not counting, defensive snapshots, `replaceEntries` import |
| `FileUrlStoreTest` | Save/load round trip, comma/quote quoting, malformed rows, duplicate codes, invalid URLs/timestamps/hit counts, missing/empty/header-only files, directory paths |
| `UrlShortenerHttpServerTest` | 201/200/302 happy paths, JSON 400/404/405/409/413, `Allow`/`Location` headers, unknown routes returning JSON (never HTML), duplicate form fields, oversized bodies, hit counts through the API |
| `MainTest` | Exit codes and output for `help`, `demo`, `service-demo`, `csv-demo`, `http-demo`, unknown commands, and bad `server` arguments |

## E) Run the CLI demos

```text
java -cp out urlshortenerbackend.Main help
java -cp out urlshortenerbackend.Main demo
java -cp out urlshortenerbackend.Main service-demo
java -cp out urlshortenerbackend.Main csv-demo
java -cp out urlshortenerbackend.Main http-demo
```

All of these must exit 0. `java -cp out urlshortenerbackend.Main bogus` and `java -cp out urlshortenerbackend.Main server bad` must exit non-zero.

## F) Run the server manually

```text
java -cp out urlshortenerbackend.Main server 8080
```

In another terminal:

```text
curl -i -X POST -d "url=https%3A%2F%2Fexample.com" http://localhost:8080/links
curl -i -X POST -d "url=https%3A%2F%2Fexample.com%2Fdocs&code=docs" http://localhost:8080/links
curl -i http://localhost:8080/links
curl -i http://localhost:8080/r/docs
curl -i http://localhost:8080/unknown
```

Expected: 201 for both creations, 200 for the list, a 302 with `Location: https://example.com/docs` for the redirect, and a JSON 404 (`{"error":"Endpoint not found."}`) for `/unknown`. Repeating the redirect increases `hitCount` in the list output. Stop the server with Ctrl+C.

## G) Scripts

Linux/macOS/Git Bash:

```text
./scripts/test.sh
```

Windows PowerShell:

```text
.\scripts\test.ps1
```

Both scripts clean, strict-compile the app and tests, run the full test suite, run the `demo`, `service-demo`, and `csv-demo` commands, and remove `out/` and `test-out/` afterward.

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

- `POST /links` with `url=ftp%3A%2F%2Fexample.com` → 400 JSON
- `POST /links` with a taken `code` → 409 JSON
- `POST /links` with duplicate form field (`url=...&url=...`) → 400 JSON
- Body over 65,536 bytes → 413 JSON
- `PUT /links` or `DELETE /links` → 405 JSON with `Allow: GET, POST`
- `POST /r/docs` → 405 JSON with `Allow: GET`
- `GET /r/nosuch` → 404 JSON; `GET /r/` → 404 JSON; `GET /r/ab` (too short) → 400 JSON
- `GET /links-extra`, `/api/links`, `/r-extra`, `/r/abc/extra` → 404 JSON
- Restart the process and confirm links are intentionally gone
