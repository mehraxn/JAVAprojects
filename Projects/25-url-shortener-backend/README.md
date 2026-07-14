# URL Shortener Backend

## Overview

An educational Java URL shortener backend with service-layer logic, optional CSV persistence, and a lightweight HTTP interface built on Java's built-in `HttpServer`. It uses no framework, database, JSON library, or external dependency — plain `javac`/`java` is enough to build, run, and test it.

## What This Project Demonstrates

- URL validation (absolute `http`/`https` URLs with a host — `ftp:`, `file:`, `javascript:` and relative URLs are rejected)
- Generated Base62-style short codes and custom short codes
- Duplicate-code protection (HTTP 409 for taken custom codes)
- Redirect resolution via `GET /r/{code}` with hit counting
- Defensive copies/snapshots so callers cannot mutate stored state
- CSV save/load with quoting for commas and double quotes
- Manual JSON responses with safe escaping
- Consistent JSON error handling (400/404/405/409/413/500) — unknown routes return JSON, not the default HTML page
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Create a short link with a generated or custom code
- List stored links in creation order
- Redirect via `/r/{code}` and track hits
- Save/load links (codes, URLs, timestamps, hit counts) to/from UTF-8 CSV
- HTTP API server with JSON responses and JSON errors
- CLI demos of the service layer and CSV persistence

## Main classes

- `UrlEntry` — validated URL mapping, creation time, and hit counter.
- `CodeGenerator` — synchronized Base62-style sequential code generator.
- `ShortenerService` — thread-safe in-memory operations and defensive snapshots.
- `FileUrlStore` — CSV persistence, kept separate from request handling.
- `UrlShortenerHttpServer` — HTTP routing, status codes, and JSON errors.
- `Main` — CLI commands (`help`, `demo`, `service-demo`, `csv-demo`, `http-demo`, `server`).

## Validation rules

- **URLs** must be absolute `http` or `https` URLs with a host, at most one port up to 65535. Blank, whitespace-only, relative, schemeless, and non-HTTP-scheme URLs are rejected. URLs must be legal per `java.net.URI` (raw spaces/quotes are rejected; percent-encode them).
- **Short codes** (generated and custom) must match `[A-Za-z0-9_-]{3,20}` — letters, digits, underscore, hyphen; 3–20 characters.
- **Custom codes** that already exist are rejected (`IllegalArgumentException` in the service, HTTP 409 in the API).
- **Hit counts** cannot be negative and overflow at `Long.MAX_VALUE` is detected.

## Tech Stack

- Java 21 standard library and built-in `HttpServer`.
- UTF-8 CSV persistence with `java.nio.file`.
- Plain `javac`/`java`; no Maven or external web framework.
- Dependency-free service, persistence, and live HTTP smoke tests.

## Architecture / Design

```text
HTTP handlers → ShortenerService → FileUrlStore → CSV file
```

`ShortenerService` owns URL/code validation, generation, lookup, and hit counts. `FileUrlStore` persists snapshots, while `UrlShortenerHttpServer` maps HTTP requests and errors without exposing storage details.

## Project Structure

```text
.
├── src/urlshortenerbackend/     # Service, store, HTTP server, models, CLI
├── tests/urlshortenerbackend/   # Unit and live loopback HTTP tests
├── scripts/                     # Cross-platform validation scripts
├── TESTING.md
└── TEST_RESULTS.md
```

## How to Run

```text
javac -Xlint:all -Werror -d out src/urlshortenerbackend/*.java

java -cp out urlshortenerbackend.Main help
java -cp out urlshortenerbackend.Main demo
java -cp out urlshortenerbackend.Main service-demo
java -cp out urlshortenerbackend.Main csv-demo
java -cp out urlshortenerbackend.Main http-demo
java -cp out urlshortenerbackend.Main server 8080
```

`demo`/`service-demo` walk through generated/custom links, duplicate rejection, redirects, and hit counting. `csv-demo` saves links to a temporary CSV file, reloads them, verifies entries and hit counts survived, and deletes the file. `http-demo` prints example curl commands. `server <port>` starts the HTTP API; the port argument is required. `Main.run(args, out, err)` returns an exit code (0 on success, non-zero for unknown commands or bad server arguments), and only `main` calls `System.exit`.

## API Overview

| Method and path | Request | Result |
|---|---|---|
| `POST /links` | URL-encoded body `url=...` and optional `code=...` | 201 with link JSON |
| `GET /links` | None | 200 with a JSON array of all links |
| `GET /r/{code}` | None | 302 redirect with the original URL in `Location`; increments hits |

POST bodies use `application/x-www-form-urlencoded`; this project intentionally has no JSON request parser. Responses and errors are manually generated JSON.

### Error responses

| Status | When | Example body |
|---|---|---|
| 400 | Invalid URL/code, malformed or duplicate form fields | `{"error":"URL must be an absolute HTTP or HTTPS URL with a host."}` |
| 404 | Unknown short code or unknown route (e.g. `GET /unknown`) | `{"error":"Endpoint not found."}` |
| 405 | Unsupported method (with an `Allow` header) | `{"error":"Method not allowed."}` |
| 409 | Custom code already exists | `{"error":"Custom code already exists."}` |
| 413 | Request body over 65,536 bytes | `{"error":"Request body is too large."}` |
| 500 | Unexpected server error (no stack trace leaked) | `{"error":"Internal server error."}` |

### Example curl commands

```text
curl -i -X POST -d "url=https%3A%2F%2Fexample.com" http://localhost:8080/links
curl -i -X POST -d "url=https%3A%2F%2Fexample.com%2Fdocs&code=docs" http://localhost:8080/links
curl -i http://localhost:8080/links
curl -i http://localhost:8080/r/docs
curl -i http://localhost:8080/unknown
```

## CSV persistence

`FileUrlStore` uses this header:

```text
shortCode,originalUrl,createdAt,hitCount
```

It quotes fields containing commas or double quotes. Multiline fields are intentionally unsupported. Missing, empty, and header-only files load as an empty map; duplicate codes, malformed rows, invalid URLs, invalid timestamps, and invalid hit counts are rejected with an `IOException` naming the line. Persistence stays separate from the service; the HTTP server never touches the filesystem.

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper + `TestRunner`) covering the code generator, entry validation, service behavior, CSV persistence, HTTP endpoints, and CLI exit codes:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/urlshortenerbackend/*.java
java -cp "out;test-out" urlshortenerbackend.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Classes, encapsulation, collections, and defensive copies
- URI validation and Base62-style encoding
- Synchronization and overflow checks
- File I/O and manual CSV parsing/quoting
- Built-in HTTP handlers, status codes, redirects, and headers
- Manual JSON escaping and URL-encoded form parsing
- Exit codes and testable CLI entry points

## Known Limitations

- Links live in memory and are lost on restart unless CSV storage is used explicitly
- No database, authentication, rate limiting, TLS, or custom domain support
- Generated codes are sequential rather than unpredictable
- CSV persistence does not support multiline values or concurrent writers
- No production JSON parser and no framework (no Spring Boot) — this is a learning project

## Resume Value

Built a framework-free Java URL-shortening backend with generated codes, validated HTTP behavior, redirect lookup, hit tracking, CSV persistence, concurrent service access, and automated loopback tests.

## Possible future improvements

- Expiring links and link deactivation
- Cryptographically random code generation
- Automatic persistence integration and atomic file replacement
- Custom aliases scoped by user
- Configurable HTTP limits
