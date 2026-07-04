# URL Shortener Backend

A standard-Java URL shortener with in-memory storage, hit tracking, optional CSV persistence, and an optional HTTP interface built with Java's `HttpServer`.

## Features

- Generate compact, URL-safe short codes.
- Accept custom codes containing 3–20 letters, numbers, underscores, or hyphens.
- Reject duplicate custom codes.
- Validate absolute HTTP and HTTPS URLs with a host.
- Resolve short codes and increment their hit counts.
- List stored links in creation order.
- Save and load mappings, timestamps, and hit counts from UTF-8 CSV.
- Handle missing, empty, whitespace-only, and header-only storage files safely.
- Expose optional REST-style endpoints without external libraries.

## Main classes

- `UrlEntry` — validated URL mapping, creation time, and hit counter.
- `CodeGenerator` — synchronized Base62-style sequential code generator.
- `ShortenerService` — thread-safe in-memory operations and defensive snapshots.
- `FileUrlStore` — optional CSV persistence.
- `UrlShortenerHttpServer` — optional built-in HTTP adapter.
- `Main` — console demonstration or explicit server launcher.

## How the program works

`ShortenerService` validates a long URL, generates or accepts a short code, and stores a `UrlEntry`. Resolving a code looks it up directly and increments its hit counter. HTTP handlers translate local requests into service calls, while `FileUrlStore` remains an optional persistence boundary.

## In-memory storage

`ShortenerService` stores entries in a `LinkedHashMap` keyed by short code. This gives direct code lookup and stable creation order. Service methods are synchronized because the optional HTTP server can process multiple requests. Returned entries are copies, so callers cannot alter stored hit counts. Data is lost when the process exits unless `FileUrlStore` is used explicitly.

## HTTP endpoints

Start the server on the default port 8080:

```text
java -cp out urlshortenerbackend.Main server
```

Or select a port:

```text
java -cp out urlshortenerbackend.Main server 8090
```

| Method and path | Request | Result |
|---|---|---|
| `POST /links` | URL-encoded body `url=...` and optional `code=...` | Creates a link and returns JSON with status 201 |
| `GET /links` | None | Returns all stored links as JSON |
| `GET /r/{code}` | None | Increments hits and returns a 302 redirect |

POST bodies use `application/x-www-form-urlencoded`; no request JSON parser is included. Error responses are manually generated JSON.

Example:

```text
curl -i -X POST -d "url=https%3A%2F%2Fexample.com&code=example" http://localhost:8080/links
curl -i http://localhost:8080/r/example
curl http://localhost:8080/links
```

## Optional CSV persistence

`FileUrlStore` uses this header:

```csv
shortCode,originalUrl,createdAt,hitCount
```

It supports commas and quotation marks in quoted fields. Multiline fields are intentionally unsupported. Persistence is kept separate from the service so the basic demonstration remains entirely in memory.

## Example usage

```text
javac -d out src/urlshortenerbackend/*.java
java -cp out urlshortenerbackend.Main
```

The normal command demonstrates service logic without starting a server or creating files.

## Java concepts practiced

- Classes, encapsulation, collections, and defensive copies
- URI validation and Base62-style encoding
- Synchronization and overflow checks
- File I/O and manual CSV parsing
- Built-in HTTP handlers, status codes, redirects, and headers
- Manual JSON escaping and URL-encoded form parsing

## Backend concepts practiced

- Service-layer validation and in-memory key/value lookup
- Collision handling and custom identifier uniqueness
- HTTP routing, status codes, redirects, headers, and bounded request bodies
- Optional file persistence kept separate from request handling

## Storage approach

The active service uses an insertion-ordered in-memory map. `FileUrlStore` can explicitly save or load UTF-8 CSV snapshots containing codes, URLs, timestamps, and hit counts. It is not invoked automatically by the HTTP server.

## Limitations

- Links are lost on restart unless file storage is integrated by the caller
- The HTTP server has no authentication, TLS configuration, or rate limiting
- Generated codes are sequential rather than unpredictable
- CSV persistence does not support multiline values or concurrent writers

## Possible future improvements

- Expiring links and link deactivation
- Cryptographically random code generation
- Automatic persistence integration and atomic file replacement
- Custom aliases scoped by user
- Automated tests and configurable HTTP limits
