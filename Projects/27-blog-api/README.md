# Blog API

## Overview

This educational Java backend manages users, posts, and comments through an in-memory service and a lightweight REST-style interface built with Java's `HttpServer`. It uses manual form parsing and JSON serialization to make HTTP behavior visible without a web framework.

## Features

- Create and list unique users.
- Create, retrieve, list, update, search, and delete posts.
- Search post titles, content, and author names case-insensitively.
- Create and list comments for existing users and posts.
- Remove a post's comment collection when the post is deleted.
- Reject duplicate form fields, oversized bodies, invalid methods, and malformed routes.
- Return JSON for successful responses and handled errors.

## What This Project Demonstrates

- Multi-resource domain modeling with referential validation.
- Service-layer CRUD, search, update, delete, and cascading cleanup.
- HTTP routing, request parsing, response status/header mapping, and body limits.
- Manual JSON serialization and escaping.
- Defensive copies, dependency-free tests, and live loopback HTTP smoke tests.

## Tech Stack

- Java 21 standard library.
- Built-in `com.sun.net.httpserver.HttpServer` and Java `HttpClient` tests.
- Manual JSON and `application/x-www-form-urlencoded` processing.
- Plain `javac`/`java`; no Maven or external framework.

## Architecture / Design

```text
BlogHttpServer → BlogService → in-memory users/posts/comments
       ↓
    BlogJson
```

The HTTP layer owns routing and protocol responses, `BlogService` owns relationships and business rules, and `BlogJson` serializes success/error payloads. `Main` exposes demos and server startup.

## Project Structure

```text
.
├── src/blogapi/     # Models, service, JSON, HTTP server, CLI
├── tests/blogapi/   # Unit and live loopback HTTP tests
├── scripts/         # test.sh and test.ps1
├── TESTING.md
└── TEST_RESULTS.md
```

## API Overview

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/users` | List users. |
| `POST` | `/users` | Create a user from `name`. |
| `GET` | `/posts` | List posts; optional `q` search. |
| `POST` | `/posts` | Create from `authorId`, `title`, and `content`. |
| `GET` | `/posts/{id}` | Retrieve a post. |
| `PUT` | `/posts/{id}` | Update title and content. |
| `DELETE` | `/posts/{id}` | Delete a post and its comments. |
| `GET` | `/posts/{id}/comments` | List comments. |
| `POST` | `/posts/{id}/comments` | Create a comment from `authorId` and `body`. |

## How to Run

```bash
javac -Xlint:all -Werror -d out src/blogapi/*.java
java -cp out blogapi.Main demo
java -cp out blogapi.Main server 8082
```

The port `8082` above is supplied explicitly to the CLI; it is not an assumed deployment default.

## Testing

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

See [TESTING.md](TESTING.md) for exact requests and [TEST_RESULTS.md](TEST_RESULTS.md) for the latest recorded validation results.

## Known Limitations

- In-memory only; no database or persistence across restarts.
- No authentication, authorization, ownership enforcement, pagination, TLS, or rate limiting.
- Request bodies use form encoding and responses use a small manual JSON serializer.
- No Spring Boot or deployment packaging.

## Resume Value

Built a framework-free Java blog API with users, posts, comments, referential validation, cascading cleanup, JSON responses, HTTP error handling, and automated loopback tests.
