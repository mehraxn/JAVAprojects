# Testing Task Manager JDBC

## Static checks

- Verify imports come only from java.sql, java.time, and java.util.
- Verify SQL operations will use PreparedStatement.
- Verify resources will use try-with-resources.

## Planned database tests

- Create and retrieve a task.
- Update title, due date, and status.
- Filter by each status.
- Delete an existing and missing task.
- Roll back or preserve state after SQL failures.

## Planned validation tests

- Reject blank titles.
- Reject non-positive IDs where appropriate.
- Reject null status and invalid due dates.
- Handle unavailable connections with SQLException.

## Manual checklist

- [ ] Define a schema before execution.
- [ ] Supply an existing JDBC driver.
- [ ] Implement repository SQL.
- [ ] Keep connection configuration outside source constants.
- [ ] Run only after driver and database configuration are confirmed.
