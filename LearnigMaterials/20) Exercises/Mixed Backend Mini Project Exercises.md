# Mixed Backend Mini Project Exercises

## Mini project 1: Course enrollment

Build a small Java backend-style application with:

- `Student`
- `Course`
- `EnrollmentService`
- `StudentRepository`
- immutable snapshots
- validation
- tests

Add a workflow that enrolls a student in a course.

## Mini project 2: Product inventory import

Build:

- CSV import service;
- validation;
- import result;
- logging;
- inventory report with min, max, average, and buckets.

## Mini project 3: Order approval workflow

Build:

- `Order`
- `OrderService`
- role-based approval;
- audit fields using `Clock`;
- failing-operation tests.

## Mini project 4: JPA repository practice

Build:

- two entities;
- repositories;
- JPQL custom queries;
- H2 tests;
- transaction rollback test.

## Review questions

- Which layer owns validation?
- Which class owns persistence?
- Which results should be immutable?
- Which tests prove failure safety?
