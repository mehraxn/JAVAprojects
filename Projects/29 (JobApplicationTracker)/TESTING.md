# Job Application Tracker Testing

Use a temporary CSV path for file tests so existing application records are not overwritten.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add application | Add valid company, role, date, status, and notes | Positive ID assigned and application listed |
| Sequential IDs | Add three applications | IDs are `1`, `2`, and `3` |
| Update status | Change an existing application to `INTERVIEW` | Returns `true`; status is updated |
| Company search | Search part of a company name with different casing | Matching applications returned |
| Role search | Search part of a role | Matching applications returned |
| Status filter | Filter for `APPLIED` | Only applied records returned |
| Summary | Add two applied and one interview record | Total is `3`; status counts are `2` and `1` |
| CSV round trip | Save and load records | Fields, IDs, statuses, and count are preserved |
| Load then add | Load highest ID `8`, then add a record | New record receives ID `9` |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Empty tracker | List, filter, and summarize | Empty lists, total `0`, and zero for every status |
| No search match | Search unknown company/role text | Empty list |
| Missing update | Update an unknown positive ID | Returns `false` |
| Missing file | Load a path that does not exist | Empty tracker; no exception |
| Empty file | Load a zero-byte or whitespace-only file | Empty tracker; no exception |
| Header only | Load only the expected header | Empty tracker |
| Blank rows | Put blank lines between valid rows | Blank lines are ignored |
| Quoted fields | Save company, role, or notes containing comma/quotes | Values round-trip correctly |
| Load failure safety | Load malformed data into a populated tracker | Exception occurs before current records are replaced |

## Invalid input test cases

| Test | Input | Expected result |
|---|---|---|
| Empty company | `null`, empty, or whitespace company | `IllegalArgumentException` |
| Empty role | `null`, empty, or whitespace role | `IllegalArgumentException` |
| Null date | Missing application date | `IllegalArgumentException` |
| Null status | Missing status | `IllegalArgumentException` |
| Empty search | Blank or null search text | `IllegalArgumentException` |
| Invalid update ID | ID `0` or negative | `IllegalArgumentException` |
| Wrong header | Unexpected CSV columns | `IOException` with expected header |
| Duplicate ID | Two CSV rows with the same ID | `IOException` identifies duplicate |
| Invalid ID | Zero, negative, or nonnumeric CSV ID | `IOException` identifies invalid row |
| Invalid date | Date such as `2026-02-30` | `IOException` identifies invalid date |
| Invalid status | Unknown enum value | `IOException` identifies invalid application |
| Wrong field count | Row has fewer or more than six fields | `IOException` identifies the line |
| Broken quotes | Unclosed or misplaced quote | `IOException` identifies malformed CSV |
| Multiline text | Company, role, or notes contains a line break | Validation exception |

## Manual testing checklist

- [ ] Compile all files in `src/jobapplicationtracker`.
- [ ] Run `Main` without arguments and verify list, search, and summary output.
- [ ] Run with a temporary CSV path and verify the save/load count.
- [ ] Inspect the header, ISO dates, status names, and quoted fields.
- [ ] Test every status filter and zero-count summary entry.
- [ ] Test missing, empty, header-only, and malformed files.
- [ ] Confirm a failed load preserves existing in-memory records.
- [ ] Confirm adding after a load uses an ID above the largest loaded ID.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Future application date | Add or load a date after today | Rejected as invalid application data |
| Directory CSV path | Load or save using an existing directory | `IOException` identifies a non-regular file |
| Mutate returned application | Change status/notes on an object returned by add/list/search/filter | Stored application remains unchanged |
| Maximum loaded ID | Load `Long.MAX_VALUE`, then add another application | Load succeeds, but add reports ID exhaustion without overflow |
| Null status on unknown ID | Update an absent application using null status | Null status is rejected before the not-found result |
