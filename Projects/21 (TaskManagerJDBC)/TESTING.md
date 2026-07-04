# Task Manager JDBC Testing

The in-memory tests require only Java. Database tests are optional and require a separately supplied JDBC driver, database, and matching schema.

## Normal in-memory test cases

| Test | Action | Expected result |
|---|---|---|
| Add task | Add a valid task | A positive ID is assigned and status is `OPEN` |
| Add several tasks | Add tasks with and without due dates | All appear in insertion order |
| Update status | Change an existing task to `IN_PROGRESS` or `DONE` | Returns `true`; later reads show the new status |
| Filter status | Search for `OPEN` | Only open tasks are returned |
| Delete task | Delete an existing task | Returns `true` and task disappears |
| Missing delete | Delete an unknown positive ID | Returns `false` |
| Empty repository | List and filter before adding | Empty lists are returned |

## Edge-case and invalid input test cases

| Test | Input | Expected result |
|---|---|---|
| Blank title | `null`, empty, or whitespace title | `IllegalArgumentException` |
| Null status | Construct or update with `null` | `IllegalArgumentException` |
| Negative model ID | Construct task with ID `-1` | `IllegalArgumentException` |
| Invalid stored ID | Find, update, or delete ID `0` or below | `IllegalArgumentException` |
| Duplicate ID | Add two explicit tasks with the same positive ID | Second add is rejected |
| Missing update | Update status for an unknown positive ID | Returns `false` |
| Empty description | Use `null` or blank description | Stored safely as an empty string |
| No due date | Use `null` due date | Task is accepted and displays `no due date` |
| Defensive read | Modify a returned task object | Stored in-memory task remains unchanged |
| Returned list mutation | Attempt to add to a result list | `UnsupportedOperationException` |

## Optional JDBC test cases

| Test | Action | Expected result |
|---|---|---|
| Insert | Add a task through `JdbcTaskRepository` | One row inserted and generated ID returned |
| Find by ID | Read an existing and missing ID | Existing task or empty `Optional` |
| Update | Change task fields and status | Exactly one row updated; returns `true` |
| Status query | Query each enum status | Only matching rows returned in ID order |
| Delete | Delete existing then missing ID | `true`, then `false` |
| Null due date | Store and retrieve a task without due date | SQL `NULL` maps back to Java `null` |
| Invalid database status | Read an unknown status string | `SQLException` reports invalid stored data |
| Unavailable driver | Open a JDBC URL without its driver | `SQLException`; in-memory functionality remains usable |

## Conceptual JDBC tests without a driver

When no JDBC driver and database are installed, the JDBC table tests above are conceptual static-review cases. Verify that SQL uses `PreparedStatement`, resources use try-with-resources, generated keys are checked, and `SQLException` is propagated with useful context. Do not mark insert/query/update/delete database cases as executed. The in-memory cases remain independently runnable with only a JDK.

## Manual testing checklist

- [ ] Compile all files in `src/taskmanagerjdbc`.
- [ ] Run `Main` and confirm no database connection is attempted.
- [ ] Verify add, status update, status filtering, and deletion output.
- [ ] Exercise blank titles, invalid IDs, and null statuses.
- [ ] Confirm in-memory results are defensive and unmodifiable.
- [ ] Before JDBC tests, supply a compatible driver and create the documented table.
- [ ] Confirm the selected database supports generated keys for the insert statement.
- [ ] Remove the driver/database configuration after testing if it should not be committed.

## Phase 2 validation review additions

| Test | Action | Expected result |
|---|---|---|
| Missing JDBC driver | Open a URL whose driver is absent | `SQLException` names driver/URL/database configuration without exposing credentials |
| Database unavailable | Use a valid driver with an unreachable database | Clear wrapped `SQLException`; in-memory repository remains usable |
| Invalid generated key | Simulate a null, zero, or negative generated ID | JDBC repository rejects it with `SQLException` |
| ID exhaustion | Store explicit `Long.MAX_VALUE`, then request an automatic ID | `IllegalStateException`; no negative ID is generated |
