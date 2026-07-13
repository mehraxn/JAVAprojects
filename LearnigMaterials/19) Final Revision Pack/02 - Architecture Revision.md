# Architecture Revision

## Key concepts

- Architecture gives responsibilities clear homes.
- `Main`, CLI, or controller code should not own business rules.
- Services coordinate workflows.
- Domain objects protect their own rules.
- Repositories hide data access.
- DTOs and snapshots protect boundaries.

## Patterns to remember

| Pattern | Purpose |
|---|---|
| Layered architecture | Separates UI, service, domain, repository, persistence |
| Facade | Provides one simple public entry point |
| Factory | Centralizes object creation |
| Repository | Separates data access from workflows |
| Snapshot/DTO | Returns safe read-only data |

## Common interview questions

1. Why should business logic not live in `main`?
2. What is the difference between a service and a domain object?
3. Why return a snapshot instead of a mutable entity?
4. When is a facade useful?

## Quick summary

Good architecture is practical separation of responsibilities, not extra complexity for its own sake.
