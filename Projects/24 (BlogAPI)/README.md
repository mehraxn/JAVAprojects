# Blog API

## Status

Post, comment, service, and built-in HttpServer skeleton created. CRUD logic is pending.

## Planned features

- Create, update, delete, list, and search posts.
- Add and list comments for a post.
- Remove comments consistently when a post is deleted.
- Validate request data and IDs.
- Return manually created JSON-like response strings.

## Current classes

- Post: blog-post model.
- Comment: comment model linked by post ID.
- BlogService: in-memory application logic.
- BlogHttpServer: optional HTTP adapter.
- Main: safe non-server demonstration entry point.

## Constraints

The first implementation will remain in memory and use only Java HttpServer for HTTP.

## Source layout

Source files are under src/blogapi.
