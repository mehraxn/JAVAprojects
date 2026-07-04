# Testing Job Application Tracker

## Static checks

- Verify only java.sql, java.time, and java.util APIs are imported.
- Verify SQL will use PreparedStatement and try-with-resources.
- Verify Main does not connect automatically.

## Planned database tests

- Create, retrieve, update, and delete an application.
- Filter by every stage.
- Search by company and role.
- Find reminders due on and before a date.

## Planned validation tests

- Reject blank company and role.
- Reject null or impossible dates.
- Reject non-positive IDs.
- Reject invalid stage transitions.
- Handle unavailable database connections with SQLException.

## Manual checklist

- [ ] Define a schema before execution.
- [ ] Supply an existing JDBC driver.
- [ ] Implement repository SQL and row mapping.
- [ ] Keep connection details outside source constants.
- [ ] Run only after configuration is confirmed.
