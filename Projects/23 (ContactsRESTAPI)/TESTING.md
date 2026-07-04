# Contacts REST API Testing

The service tests need no server. HTTP tests should use an unused local port and stop the process afterward.

## Normal service tests

| Test | Action | Expected result |
|---|---|---|
| Create | Add a complete contact | ID `C-1` assigned and record stored |
| Sequential IDs | Add three contacts | IDs `C-1`, `C-2`, and `C-3` |
| List | Add several contacts | Records returned in creation order |
| Retrieve | Find a known ID | Matching contact returned |
| Update | Replace editable fields | Updated contact returned; ID unchanged |
| Delete | Delete known ID | Returns `true`; contact disappears |
| Search | Search partial name/email/notes text with different casing | Matching records returned |
| Pagination | Search with offset 1 and limit 2 | Correct two-record page returned |

## Edge and invalid-input tests

| Test | Input or action | Expected result |
|---|---|---|
| Empty repository | List or search before adding | Empty list |
| Unknown retrieve/update | Use valid absent ID | Returns `null` |
| Unknown delete | Delete absent ID | Returns `false` |
| Blank name | Null, empty, or whitespace | `IllegalArgumentException` |
| Invalid email | Missing `@` or domain | Rejected when nonempty |
| Invalid phone | Letters or unsupported punctuation | Rejected when nonempty |
| Empty contact methods | Blank email and phone | Accepted |
| Negative offset | Search with `-1` | Rejected |
| Invalid limit | Search with 0 or over 100 | Rejected |
| Offset beyond results | Large valid offset | Empty list |
| Duplicate repository ID | Add same explicit ID twice | Rejected |
| Defensive copy | Mutate a returned contact | Stored record remains unchanged |
| JSON special characters | Quotes, slashes, tabs, or line breaks in notes | Valid escaped JSON produced |

## HTTP test cases

| Request | Expected result |
|---|---|
| Valid `POST /contacts` | 201, contact JSON, and `Location` header |
| `GET /contacts` | 200 JSON array |
| Search/pagination GET | 200 with matching page |
| `GET /contacts/C-1` | 200 for known ID; 404 otherwise |
| Valid `PUT /contacts/C-1` | 200 with updated JSON |
| `DELETE /contacts/C-1` | 204; subsequent GET is 404 |
| Missing required form name | 400 error JSON |
| Invalid pagination text | 400 error JSON |
| Unsupported method | 405 and appropriate `Allow` header |
| Unknown endpoint under `/contacts` | 404 error JSON |
| Duplicate or malformed encoded field | 400 error JSON |
| Request larger than 65,536 bytes | 400 error JSON |
| Invalid port or second start | Validation exception |
| Stop twice | Safe no-op |

## Manual testing checklist

- [ ] Compile all files under `src/contactsrestapi`.
- [ ] Run the default console demonstration.
- [ ] Start server mode and create at least three contacts.
- [ ] Test list, individual retrieval, search, and pagination.
- [ ] Update all editable fields and verify the ID remains unchanged.
- [ ] Delete a contact and confirm later retrieval returns 404.
- [ ] Test validation errors and unsupported methods.
- [ ] Verify JSON escaping with quotation marks and line breaks in notes.
- [ ] Restart the process and confirm storage is intentionally empty.
