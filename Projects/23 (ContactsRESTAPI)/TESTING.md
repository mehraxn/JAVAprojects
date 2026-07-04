# Testing Contacts REST API

## Planned service tests

- Create, retrieve, update, search, and delete contacts.
- Verify pagination boundaries.
- Verify repository result ordering.

## Planned HTTP tests

- Test CRUD methods and status codes.
- Reject unsupported methods.
- Verify Content-Type and escaped JSON response text.
- Verify missing contact and malformed request responses.

## Planned validation tests

- Reject blank IDs and names.
- Reject invalid email and pagination values.
- Reject duplicate IDs.
- Reject malformed or unsupported JSON shapes.

## Manual checklist

- [ ] Implement repository operations.
- [ ] Implement service validation.
- [ ] Implement limited manual JSON parsing and escaping.
- [ ] Implement HttpServer contexts.
- [ ] Start the server only in explicit server mode.
