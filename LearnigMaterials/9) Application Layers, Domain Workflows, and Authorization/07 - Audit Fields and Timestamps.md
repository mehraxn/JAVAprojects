# Audit Fields and Timestamps

## Learning goals

- Understand audit fields.
- Learn why `Clock` helps deterministic tests.
- Add created and modified metadata safely.

## What are audit fields?

Audit fields record who changed data and when.

Common fields:

- `createdBy`
- `createdAt`
- `modifiedBy`
- `modifiedAt`

## Example

```java
public final class Product {
    private final String id;
    private final String createdBy;
    private final Instant createdAt;
    private String modifiedBy;
    private Instant modifiedAt;

    public Product(String id, String createdBy, Instant createdAt) {
        this.id = id;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.modifiedBy = createdBy;
        this.modifiedAt = createdAt;
    }

    public void rename(String newName, String userId, Instant now) {
        this.modifiedBy = userId;
        this.modifiedAt = now;
    }
}
```

## Using Clock

Calling `Instant.now()` directly makes tests harder because time keeps changing. Inject `Clock` into services.

```java
public final class ProductService {
    private final Clock clock;

    public ProductService(Clock clock) {
        this.clock = clock;
    }

    public Product create(String id, UserContext user) {
        Instant now = Instant.now(clock);
        return new Product(id, user.userId(), now);
    }
}
```

In tests, use a fixed clock.

```java
Clock fixed = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
```

## Common mistakes

- Updating `modifiedAt` before a workflow succeeds.
- Using local system time in tests.
- Forgetting who performed the operation.
- Allowing callers to set audit fields freely.

## Mini exercise

Add audit fields to an `Order` class. Decide which fields are set at creation and which fields change on update.

## Quick summary

Audit fields make workflows traceable. `Clock` makes time-based code testable.
