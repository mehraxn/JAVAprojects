# Testing Blog API

## Planned service tests

- Create, update, search, and delete posts.
- Add several comments to a post.
- Delete a post and verify its comments are removed.

## Planned HTTP tests

- Verify post and comment endpoint methods.
- Verify success, validation, and not-found status codes.
- Verify manually escaped response strings.

## Planned validation tests

- Reject blank titles, content, authors, and comment bodies.
- Reject unknown post IDs.
- Reject duplicate IDs.
- Handle empty searches and empty comment lists.

## Manual checklist

- [ ] Implement post CRUD.
- [ ] Implement comment operations.
- [ ] Keep post/comment deletion consistent.
- [ ] Implement HttpServer routing and method checks.
- [ ] Start the server only when explicitly requested.
