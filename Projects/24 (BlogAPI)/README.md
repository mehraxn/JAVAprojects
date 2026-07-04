# Blog API

A standard-Java in-memory blog application with users, posts, comments, search, post management, and an optional REST-style interface using Java's built-in `HttpServer`.

## Features

- Create and list users.
- Create posts owned by existing users.
- Add comments from existing users to existing posts.
- List posts and comments in creation order.
- Search posts by title or author name.
- Retrieve, update, and delete posts.
- Remove a post's comment collection when the post is deleted.
- Return defensive copies so callers cannot modify stored state directly.
- Expose optional HTTP endpoints with manually generated JSON responses.

## Main models and services

- `User` — immutable user ID and display name.
- `Post` — author, title, content, creation time, and last-update time.
- `Comment` — post association, author, body, and creation time.
- `BlogService` — synchronized in-memory user, post, and comment operations.
- `BlogJson` — manual JSON serialization and escaping.
- `BlogHttpServer` — optional HTTP routing using `com.sun.net.httpserver.HttpServer`.
- `Main` — console demonstration or explicit server launcher.

## How the program works

`BlogService` generates user, post, and comment IDs and owns the relationships between them. A post must reference an existing user, and a comment must reference both an existing user and post. Server handlers translate URL-encoded requests into service operations and use `BlogJson` for responses.

## In-memory storage

`BlogService` uses insertion-ordered maps for users and posts and a map of comment lists keyed by post ID. IDs are generated as `U-1`, `P-1`, and `C-1`. All data is process-local and disappears when the application stops.

## HTTP endpoints

Start the optional server on port 8082:

```text
java -cp out blogapi.Main server
```

| Method and path | Behavior |
|---|---|
| `POST /users` | Create a user from form field `name` |
| `GET /users` | List users |
| `POST /posts` | Create a post from `authorId`, `title`, and `content` |
| `GET /posts` | List posts |
| `GET /posts?q=text` | Search titles and author names |
| `GET /posts/{id}` | Retrieve one post |
| `PUT /posts/{id}` | Replace `title` and `content` |
| `DELETE /posts/{id}` | Delete a post and its comments |
| `POST /posts/{id}/comments` | Add a comment from `authorId` and `body` |
| `GET /posts/{id}/comments` | List comments for a post |

POST and PUT bodies use `application/x-www-form-urlencoded`. Responses are manually generated JSON; no JSON library or request JSON parser is used.

Example:

```text
curl -X POST -d "name=Ada+Author" http://localhost:8082/users
curl -X POST -d "authorId=U-1&title=Java+Collections&content=An+introduction" http://localhost:8082/posts
curl -X POST -d "name=Rita+Reader" http://localhost:8082/users
curl -X POST -d "authorId=U-2&body=Useful+post" http://localhost:8082/posts/P-1/comments
curl "http://localhost:8082/posts?q=ada"
```

## Example usage

```text
javac -d out src/blogapi/*.java
java -cp out blogapi.Main
```

The normal command runs a short console demonstration without binding a port.

## Java concepts practiced

- Classes, immutable values, defensive copies, and object relationships
- `Map` and `List` collections
- Case-insensitive searching
- Timestamps with `LocalDateTime`
- Synchronized in-memory service operations
- Built-in HTTP routing, methods, headers, and status codes
- URL-encoded request parsing and manual JSON escaping

## Backend concepts practiced

- Related in-memory resources and referential validation
- CRUD routes, nested comment routes, and search queries
- Cascading in-memory cleanup when a post is deleted
- HTTP method, status, body-size, and not-found handling

## Storage approach

Users and posts are stored in insertion-ordered maps; comments are stored in lists keyed by post ID. Storage is entirely in memory and lasts only for the current process.

## Limitations

- No persistence, authentication, or post ownership enforcement
- Request input is URL-encoded rather than general JSON
- No pagination, tags, drafts, or comment moderation
- The built-in server is an educational local interface

## Possible future improvements

- User authentication and ownership checks for updates
- Pagination and post tags
- Comment editing and deletion
- File persistence
- Request JSON parsing
- Automated service and HTTP tests
