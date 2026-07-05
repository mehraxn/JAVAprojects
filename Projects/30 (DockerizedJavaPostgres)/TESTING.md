# Testing Dockerized Java PostgreSQL

No tests or commands described here were executed during implementation.

## Static checks

- Confirm `.env.example` contains only placeholder values.
- Confirm no password is printed by Java code.
- Confirm SQL uses prepared statements.
- Confirm JDBC resources use try-with-resources.
- Confirm the app waits for the PostgreSQL health check in Compose.
- Confirm schema, environment-variable, service, and volume names agree.

## Java validation cases

| Test | Action | Expected result |
|---|---|---|
| Valid task | Create a task with a short title | Task is accepted |
| Blank title | Use null, empty, or whitespace title | `IllegalArgumentException` |
| Long title | Use more than 200 characters | `IllegalArgumentException` |
| Negative ID | Construct a task with negative ID | `IllegalArgumentException` |
| Invalid stored ID | Find, update, or delete ID zero or below | `IllegalArgumentException` |
| Missing configuration | Omit a required DB environment variable | Clear configuration error; password is not shown |

## Repository integration cases

These require PostgreSQL and the PostgreSQL JDBC driver.

| Test | Action | Expected result |
|---|---|---|
| Empty database | Start with an empty initialized task table | Starter task is inserted once |
| Existing data | Start again using the same volume | Existing tasks are listed; no extra starter task |
| Create | Add a valid task | Generated positive ID returned |
| Find | Query existing and missing IDs | Task or empty `Optional` returned |
| Complete | Mark an existing task complete | Exactly one row updated |
| Delete | Delete existing then missing task | `true`, then `false` |
| Database unavailable | Use an unreachable JDBC URL | Clear `SQLException`; credentials are not printed |
| Driver missing | Run without PostgreSQL JDBC driver | Connection fails honestly; no fallback claim |

## Docker and Compose manual checklist

- [ ] Copy `.env.example` to an uncommitted `.env` file.
- [ ] Replace `CHANGE_ME` with a local-only password.
- [ ] Review resolved environment values before startup.
- [ ] Build the Java image.
- [ ] Start the database and application services.
- [ ] Verify PostgreSQL becomes healthy before the app starts.
- [ ] Verify the first run inserts and prints one task.
- [ ] Verify a second run reuses the named volume.
- [ ] Stop services without deleting the volume and confirm persistence.
- [ ] Remove the volume only when its data is intentionally disposable.

## Expected limitations during current review

- Java was not run.
- Maven was not run and no dependency was downloaded.
- Docker and Docker Compose were not run.
- PostgreSQL was not installed or started.
- JDBC connectivity and SQL behavior were reviewed statically only.
