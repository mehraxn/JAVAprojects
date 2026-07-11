# Blog API

An educational Java Blog API with in-memory service-layer logic and a lightweight REST-style interface built on Java's `HttpServer`.

## What it demonstrates

- Users, posts, and comments with referential validation
- Service-layer search, update, and delete workflows
- Cascading comment cleanup when a post is deleted
- Defensive copies and unmodifiable result collections
- Manual JSON serialization and safe escaping
- Consistent JSON error handling for 400, 404, 405, 413, and 500 responses
- REST-style routes, request validation, and `Allow`/`Location` headers
- Dependency-free automated tests, real HTTP smoke tests, and strict compilation

## Features

- Create and list unique users
- Create, retrieve, list, update, search, and delete posts
- Search case-insensitively across titles, content, and author names
- Create and list comments for existing posts and users
- Remove a deleted post's comment collection
- Reject duplicate form fields, oversized bodies, invalid methods, and malformed routes
- Return JSON for successful API responses and all handled API errors

## Design

- `User`, `Post`, and `Comment` are the domain models.
- `BlogService` owns all in-memory business logic.
- `BlogJson` owns JSON serialization and escaping.
- `BlogHttpServer` owns HTTP routing and response mapping.
- `Main` owns CLI demos and server startup; `Main.run` is directly testable.

Bodies use `application/x-www-form-urlencoded`. Parsing is intentionally small and educational; it is not a general or production JSON/form framework. Responses are manually serialized JSON.

## HTTP routes

| Method and path | Behavior |
|---|---|
| `GET /users` | List users |
| `POST /users` | Create from `name` |
| `GET /posts` | List posts; optional `?q=keyword` search |
| `POST /posts` | Create from `authorId`, `title`, `content` |
| `GET /posts/{id}` | Retrieve a post |
| `PUT /posts/{id}` | Update `title` and `content` |
| `DELETE /posts/{id}` | Delete post and comments |
| `GET /posts/{id}/comments` | List comments |
| `POST /posts/{id}/comments` | Create from `authorId` and `body` |

Unknown and malformed paths return a JSON 404. Unsupported methods return JSON 405 and an `Allow` header.

## Quick start

```text
javac -Xlint:all -Werror -d out src/blogapi/*.java

java -cp out blogapi.Main help
java -cp out blogapi.Main demo
java -cp out blogapi.Main service-demo
java -cp out blogapi.Main server 8082
```

Example requests:

```sh
curl -i -X POST http://localhost:8082/users -d "name=alice"
curl -i -X POST http://localhost:8082/posts -d "authorId=U-1&title=Hello&content=First+post"
curl -i http://localhost:8082/posts
curl -i -X POST http://localhost:8082/posts/P-1/comments -d "authorId=U-1&body=Nice+post"
curl -i http://localhost:8082/posts/P-1/comments
curl -i http://localhost:8082/unknown
```

See [TESTING.md](TESTING.md) for exact commands and [TEST_RESULTS.md](TEST_RESULTS.md) for recorded validation results.

## Limitations

- Local, in-memory operation only; no persistence or database
- No authentication, authorization, or ownership enforcement
- No pagination, TLS, or cloud deployment
- No production JSON parser or web framework such as Spring Boot
- No production concurrency, rate limiting, or deployment packaging guarantees
