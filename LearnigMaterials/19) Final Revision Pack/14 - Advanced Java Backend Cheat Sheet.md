# Advanced Java Backend Cheat Sheet

## Architecture

- UI/CLI/controller starts workflows.
- Service layer coordinates use cases.
- Domain model protects business rules.
- Repository hides data access.
- DTOs/snapshots protect boundaries.
- Factories centralize meaningful object creation.
- Facades provide simple entry points over multiple services.

## SOLID reminders

- SRP: one main reason to change.
- OCP: extend behavior without careless modification.
- LSP: subtype should honor parent contract.
- ISP: prefer focused interfaces.
- DIP: high-level code depends on abstractions.

## Workflows

- Validate input.
- Check existence.
- Check authorization.
- Check business rules.
- Mutate state.
- Save changes.
- Return immutable result.

## JPA annotations

- `@Entity`: persistent class.
- `@Table`: table settings.
- `@Id`: primary key.
- `@GeneratedValue`: generated ID.
- `@Column`: column settings.
- `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`: relationships.
- `mappedBy`: inverse side.
- `@JoinColumn`: foreign key column.
- `@JoinTable`: join table.

## EntityManager

- `persist`: make new entity persistent.
- `find`: load by primary key.
- `merge`: copy detached state into managed entity.
- `remove`: delete managed entity.
- `createQuery`: JPQL query.
- `flush`: send SQL, not commit.
- `clear`: detach all.
- `detach`: detach one.
- `close`: release resources.

## Transactions

```java
try {
    tx.begin();
    // work
    tx.commit();
} catch (RuntimeException ex) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw ex;
}
```

## JPQL

- Query entities, not tables.
- Use field names, not column names.
- Use parameters: `:name`.
- Use `TypedQuery<T>`.
- Use fetch join when related data is needed.
- Use DTO projection for report data.
- Use pagination with deterministic `ORDER BY`.

## Maven and CI

- `mvn clean test`: clean and run tests.
- Surefire: unit tests.
- Failsafe: integration tests.
- JaCoCo: coverage.
- Maven Wrapper: repeatable Maven version.
- CI should set Java version and run tests.

## Data import and reports

- Use UTF-8.
- Validate headers and rows.
- Return `ImportResult`.
- Include row-number errors.
- Use logging, not only `System.out`.
- Use immutable report objects.
- Use `BigDecimal` for money.
- Test empty data, boundaries, and division by zero.
