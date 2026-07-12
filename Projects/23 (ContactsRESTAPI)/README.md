# Contacts REST API

An educational Java Contacts REST API with service-layer CRUD/search/pagination logic and a lightweight interface built on Java's built-in `HttpServer`. It uses no framework, database, JSON library, or external dependency — plain `javac`/`java` is enough to build, run, and test it.

## What it demonstrates

- A validated contact domain model (`Contact`)
- In-memory repository with synchronized access and defensive copies
- Service-layer CRUD, case-insensitive search, and offset/limit pagination
- Manual JSON serialization with safe escaping (`JsonUtil`)
- REST-style routing, status codes, and `Allow`/`Location` headers on `com.sun.net.httpserver.HttpServer`
- Consistent JSON error responses (400/404/405/413/500) — unknown routes return JSON, not the default HTML page
- Request validation and a request-size limit
- Dependency-free automated tests (custom assertion helper + test runner)
- Strict compilation with `-Xlint:all -Werror`

## Features

- Create, list, get, update, and delete contacts
- Sequential contact IDs (`C-1`, `C-2`, …)
- Search across name, email, phone, and notes (case-insensitive)
- Offset/limit pagination with bounds checking
- HTTP API server with JSON responses and JSON errors
- CLI demos of the service layer

## Main classes

- `Contact` — validated, final contact model.
- `InMemoryContactRepository` — synchronized `LinkedHashMap` storage and defensive copies.
- `ContactService` — ID generation, CRUD operations, search, and pagination.
- `JsonUtil` — small response-only JSON serializer and escaper.
- `ContactHttpServer` — request routing, HTTP status handling, and JSON errors.
- `Main` — CLI commands (`help`, `demo`, `service-demo`, `http-demo`, `server`).

## Quick start

```text
javac -Xlint:all -Werror -d out src/contactsrestapi/*.java

java -cp out contactsrestapi.Main help
java -cp out contactsrestapi.Main demo
java -cp out contactsrestapi.Main service-demo
java -cp out contactsrestapi.Main http-demo
java -cp out contactsrestapi.Main server 8082
```

`demo`/`service-demo` run a service-layer walkthrough (create, list, search, update, pagination, delete). `http-demo` prints example curl commands. `server <port>` starts the HTTP API; the port argument is required. `Main.run(args, out, err)` returns an exit code (0 on success, non-zero for unknown commands or bad server arguments), and only `main` calls `System.exit`.

## HTTP endpoints

| Method and path | Behavior |
|---|---|
| `POST /contacts` | Create a contact; returns 201 and a `Location` header |
| `GET /contacts` | List contacts |
| `GET /contacts?q=text&offset=0&limit=20` | Search and paginate contacts |
| `GET /contacts/{id}` | Retrieve one contact |
| `PUT /contacts/{id}` | Replace the contact's editable fields |
| `DELETE /contacts/{id}` | Delete a contact; returns 204 |

POST and PUT bodies use `application/x-www-form-urlencoded` fields: `name`, `email`, `phone`, and `notes` (this project intentionally has no JSON request parser). `name` is required. Email and phone may be empty, but nonempty values must pass their basic format checks. Duplicate emails are allowed — there is no uniqueness rule.

### Error responses

All API errors are JSON, including unknown routes:

| Status | When | Example body |
|---|---|---|
| 400 | Invalid input, bad pagination, malformed/duplicate form fields | `{"error":"Name cannot be empty."}` |
| 404 | Missing contact or unknown route (e.g. `GET /unknown`) | `{"error":"Endpoint not found."}` |
| 405 | Unsupported method (with an `Allow` header) | `{"error":"Method not allowed."}` |
| 413 | Request body over 65,536 bytes | `{"error":"Request body is too large."}` |
| 500 | Unexpected server error (no stack trace leaked) | `{"error":"Internal server error."}` |

### Example curl commands

```text
curl -i -X POST -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+123&notes=Developer" http://localhost:8082/contacts
curl -i http://localhost:8082/contacts
curl -i "http://localhost:8082/contacts?q=ada&offset=0&limit=10"
curl -i http://localhost:8082/contacts/C-1
curl -i -X PUT -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+456&notes=Updated" http://localhost:8082/contacts/C-1
curl -i -X DELETE http://localhost:8082/contacts/C-1
curl -i http://localhost:8082/unknown
```

## In-memory storage

Contacts are keyed by ID in a `LinkedHashMap`, preserving creation order. The repository copies contacts when saving and reading so callers cannot mutate stored records directly, and returned lists are unmodifiable. Repository methods are synchronized for HTTP use. Data exists only while the process runs; restarting the application starts with an empty contact list.

## Testing

The project ships with dependency-free automated tests (custom `Assert` helper + `TestRunner`) covering the model, repository, service, JSON serialization, HTTP endpoints, and CLI exit codes:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/contactsrestapi/*.java
java -cp "out;test-out" contactsrestapi.TestRunner   # Windows (use out:test-out on Linux/macOS)
```

Or run everything with one script: `./scripts/test.sh` (Linux/macOS/Git Bash) or `.\scripts\test.ps1` (Windows PowerShell). See [TESTING.md](TESTING.md) for the full procedure and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded results.

## Java concepts practiced

- Classes, encapsulation, collections, and defensive copies
- CRUD service and repository responsibilities
- Case-insensitive searching and pagination
- Synchronization for shared in-memory data
- Built-in HTTP routing, headers, methods, and status codes
- URL decoding and manual JSON generation
- Exit codes and testable CLI entry points

## Limitations

- In-memory only: no database, persistence, or multi-process coordination
- Request bodies use URL-encoded form fields — no production JSON parser
- Email and phone validation is intentionally basic (an email needs `@` and a dot in the domain; this is not production-grade validation)
- No authentication, authorization, TLS, or rate limiting
- No framework (no Spring Boot) and no deployment packaging — this is a learning project

## Possible future improvements

- File persistence
- Partial updates with `PATCH`
- Stronger international phone/email validation
- Sorting and additional search fields
- Request JSON parsing
