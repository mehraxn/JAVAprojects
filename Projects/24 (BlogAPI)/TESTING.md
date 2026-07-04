# Blog API Testing

Service tests require no server. HTTP tests should use an unused local port and stop the process afterward.

## Normal service tests

| Test | Action | Expected result |
|---|---|---|
| Create users | Create two valid users | IDs `U-1` and `U-2` assigned |
| Create post | Create with valid user, title, and content | Post `P-1` stored with author and timestamps |
| Add comment | Existing user comments on existing post | Comment `C-1` appears under that post |
| List posts | Create several posts | Posts returned in creation order |
| Search title | Search a partial title with different casing | Matching posts returned |
| Search author | Search a partial author name | That author's posts returned |
| Update post | Replace title and content | Values change and updated time advances |
| Delete post | Delete post with comments | Post and its comment collection are removed |

## Edge-case and invalid input test cases

| Test | Input or action | Expected result |
|---|---|---|
| Empty blog | List users or posts | Empty lists |
| No search match | Search unknown text | Empty list |
| Empty search | Null, blank, or whitespace query | `IllegalArgumentException` |
| Blank user name | Null or blank name | Rejected; next successful user keeps expected ID |
| Unknown author | Create post/comment with absent user ID | Rejected |
| Unknown post comment | Comment on absent post | Rejected |
| Blank post fields | Empty title or content | Rejected |
| Blank comment body | Empty or whitespace body | Rejected |
| Unknown update | Update absent valid post ID | Returns `null` |
| Unknown delete | Delete absent valid post ID | Returns `false` |
| Unknown comments list | List comments for absent post | Rejected |
| Defensive result | Modify returned post or collection | Stored data remains unchanged or collection rejects mutation |
| Timestamp order | Construct post with update before creation | Rejected |

## HTTP test cases

| Request | Expected result |
|---|---|
| Valid `POST /users` | 201 and user JSON |
| Valid `POST /posts` | 201, post JSON, and `Location` header |
| `GET /posts` and search query | 200 JSON arrays |
| `GET /posts/P-1` | 200 for existing post; 404 otherwise |
| Valid `PUT /posts/P-1` | 200 updated JSON |
| `POST /posts/P-1/comments` | 201 comment JSON |
| `GET /posts/P-1/comments` | 200 JSON array |
| `DELETE /posts/P-1` | 204; later post/comment requests return 404 |
| Invalid or duplicate form fields | 400 error JSON |
| Unsupported method | 405 with `Allow` header |
| Unknown route | 404 error JSON |
| Oversized request body | 400 error JSON |

## Example HTTP requests

After starting `blogapi.Main server 8082`:

```text
curl -i -X POST -d "name=Ada+Author" http://localhost:8082/users
curl -i -X POST -d "name=Rita+Reader" http://localhost:8082/users
curl -i -X POST -d "authorId=U-1&title=Java+Collections&content=An+introduction" http://localhost:8082/posts
curl -i "http://localhost:8082/posts?q=ada"
curl -i -X POST -d "authorId=U-2&body=Useful+post" http://localhost:8082/posts/P-1/comments
curl -i -X PUT -d "title=Updated+title&content=Updated+content" http://localhost:8082/posts/P-1
curl -i -X DELETE http://localhost:8082/posts/P-1
```

Creation requests should return 201, successful reads/updates 200, and deletion 204. Requests for the deleted post and its comments should then return 404.

## Manual testing checklist

- [ ] Compile all source files.
- [ ] Run the default console demonstration.
- [ ] Start server mode and create two users.
- [ ] Create, list, retrieve, search, and update posts.
- [ ] Add and list multiple comments.
- [ ] Delete a post and confirm its comments are inaccessible.
- [ ] Test blank fields, unknown users/posts, and unsupported methods.
- [ ] Verify JSON escaping with quotes and line breaks.
- [ ] Stop the server with Ctrl+C.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Oversized user name | Use more than 100 characters | Rejected before user creation |
| Oversized title | Use more than 200 characters | Post creation/update is rejected |
| Oversized content | Use more than 50000 characters | Rejected without partially changing the post |
| Oversized comment | Use more than 5000 characters | Rejected; comment list remains unchanged |
