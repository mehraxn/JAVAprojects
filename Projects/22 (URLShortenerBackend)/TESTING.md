# URL Shortener Backend Testing

Use temporary paths for persistence tests and an unused local port for HTTP tests.

## Normal service tests

| Test | Action | Expected result |
|---|---|---|
| Generated code | Shorten a valid HTTPS URL | Six-character code is created and stored |
| Custom code | Shorten with code `java_docs` | Requested code is preserved |
| Resolve | Resolve a stored code twice | Original URL returned and hit count becomes 2 |
| List links | Add several URLs | Entries appear in creation order |
| Find unknown | Find an unused valid code | Returns `null` without changing state |
| Defensive results | Modify a returned `UrlEntry` | Stored entry remains unchanged |
| CSV round trip | Save and load mappings | URL, code, timestamp, and hits are preserved |

## Edge-case and invalid input test cases

| Test | Input or action | Expected result |
|---|---|---|
| Duplicate custom code | Create the same custom code twice | Second request throws `IllegalArgumentException` |
| Invalid scheme | `ftp://example.com` | Rejected |
| Missing host | `https:///path` | Rejected |
| Relative URL | `/articles/java` | Rejected |
| Blank URL | Null, empty, or whitespace | Rejected |
| Invalid code | Too short, too long, spaces, or punctuation | Rejected |
| Unknown resolve | Resolve a valid but absent code | `NoSuchElementException` |
| Negative hits | Construct entry with `-1` hits | Rejected |
| Hit overflow | Record a hit at `Long.MAX_VALUE` | `IllegalStateException` |
| Missing CSV | Load a nonexistent path | Empty map |
| Empty CSV | Load zero-byte or blank file | Empty map |
| Header only | Load only the expected header | Empty map |
| Duplicate CSV code | Load two rows with one code | `IOException` |
| Invalid CSV timestamp/hits | Load malformed stored values | `IOException` identifies the line |
| Broken quoting | Load unclosed or misplaced quotes | `IOException` |

## HTTP test cases

| Request | Expected result |
|---|---|
| `POST /links` with valid encoded URL | 201 and link JSON |
| `POST /links` with valid custom code | 201 with requested code |
| Duplicate custom-code POST | 400 error JSON |
| `GET /links` | 200 JSON array |
| `GET /r/{knownCode}` | 302 with original URL in `Location` |
| `GET /r/{unknownCode}` | 404 error JSON |
| Unsupported method | 405 and `Allow` header |
| Oversized POST body | 400 error JSON |
| Invalid form encoding or duplicate field | 400 error JSON |
| Start with invalid port or start twice | Validation exception |
| Stop twice | Safe no-op |

## Example HTTP requests

After starting `urlshortenerbackend.Main server 8080`:

```text
curl -i -X POST -d "url=https%3A%2F%2Fexample.com%2Fguide&code=guide" http://localhost:8080/links
curl -i http://localhost:8080/r/guide
curl -i http://localhost:8080/links
curl -i http://localhost:8080/r/unknown
```

Expected results are 201 for creation, 302 for the known redirect, 200 for listing, and 404 for the unknown code.

## Manual testing checklist

- [ ] Compile all source files.
- [ ] Run the default demo and verify generated/custom links and hit counts.
- [ ] Save and reload entries through `FileUrlStore` using a temporary file.
- [ ] Start server mode and exercise all three endpoints.
- [ ] Verify JSON escaping with URLs containing query parameters.
- [ ] Confirm redirects increment the displayed hit count.
- [ ] Test missing, empty, header-only, duplicate, and malformed CSV files.
- [ ] Stop the server with Ctrl+C.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Invalid URL port | Shorten `https://example.com:70000/path` | Rejected because the port exceeds 65535 |
| Null restored map key | Replace entries with a map containing a null key | `IllegalArgumentException`, not `NullPointerException` |
| Directory storage path | Load or save using an existing directory | `IOException` identifies a non-regular file |
