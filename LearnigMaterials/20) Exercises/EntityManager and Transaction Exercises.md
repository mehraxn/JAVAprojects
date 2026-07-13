# EntityManager and Transaction Exercises

## 1. Create entities

Create:

- `Student`
- `Course`

Use:

- `@Entity`
- `@Id`
- `@GeneratedValue`
- `@Column`

## 2. Configure persistence

Create `persistence.xml` with:

- development persistence unit;
- test persistence unit;
- H2 database URL;
- no real secrets.

## 3. Persist data

Use `EntityManager` to:

- begin transaction;
- persist a student;
- commit transaction;
- close resources.

## 4. Rollback test

Create a workflow that persists an order and two items. Force the second item to fail validation and prove the transaction rolls back.

## 5. Lifecycle review

Explain transient, managed, detached, and removed states using a `Product` entity.
