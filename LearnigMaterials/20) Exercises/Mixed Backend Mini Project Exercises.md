# Mixed Backend Mini Project Exercises

These mini projects combine architecture, OOP, exceptions, testing, build tools, persistence, imports, logging, reports, and repository design. Keep the solutions small enough to finish, but structure them like real backend code.

## Mini Project 1: Course Enrollment Application

Difficulty: Medium

## Goal

Build a small backend-style Java application that enrolls students in courses.

## Suggested Packages

```text
com.example.enrollment.domain
com.example.enrollment.repository
com.example.enrollment.service
com.example.enrollment.exception
com.example.enrollment.dto
```

## Requirements

- Create `Student`, `Course`, and `Enrollment` domain classes.
- Add a service method `enrollStudent(studentId, courseId)`.
- Prevent duplicate enrollment.
- Prevent enrollment when a course is full.
- Use repository interfaces.
- Use fake in-memory repositories for tests.
- Return immutable response DTOs.

## Expected Behavior

The service coordinates validation, repository lookup, domain rules, and persistence without exposing mutable domain internals.

## Test Cases

- Student can enroll in an available course.
- Duplicate enrollment is rejected.
- Missing student is rejected.
- Missing course is rejected.
- Full course is rejected.
- Response DTO cannot be used to mutate internal state.

## Hints

Let the service coordinate the workflow. Let the domain object protect its own invariants.

## Common Mistakes

- Putting all validation inside the controller-style entry point.
- Returning mutable collections.
- Using exceptions for normal success responses.
- Testing only the success path.

## Deliverables

- Domain classes.
- Repository interfaces.
- In-memory repository test doubles.
- Service class.
- Custom exceptions.
- Unit tests.
- Short README explaining package responsibilities.

## Bonus Challenge

Add a waitlist when the course is full.

## Mini Project 2: Product Inventory Import and Report

Difficulty: Medium

## Goal

Build an import workflow that reads product inventory rows and produces a summary report.

## Suggested Packages

```text
com.example.inventory.importing
com.example.inventory.domain
com.example.inventory.reporting
com.example.inventory.service
```

## Requirements

- Read CSV files with UTF-8.
- Validate product name, SKU, quantity, and unit price.
- Reject duplicate SKUs in the same file.
- Return an immutable import result.
- Log import start, skipped rows, and completion.
- Generate a report with count, min quantity, max quantity, average quantity, and total inventory value.

## Expected Behavior

Valid product rows are imported. Invalid rows are reported with row numbers. The report uses only accepted rows.

## Test Cases

- Valid file imports all products.
- Duplicate SKU is rejected.
- Invalid quantity is rejected.
- Invalid price is rejected.
- Empty file returns an empty report.
- Report values are correct for accepted rows.

## Hints

Use `BigDecimal` for price and total value.

## Common Mistakes

- Treating duplicate rows as valid without documenting the rule.
- Building reports from skipped rows.
- Using `double` for money.
- Hardcoding local file paths.

## Deliverables

- CSV parser or clearly documented parsing helper.
- Product validator.
- Import service.
- Report service.
- Import tests.
- Report tests.
- Logging explanation.

## Bonus Challenge

Add a command-line main method that accepts a CSV path and prints the import summary.

## Mini Project 3: Order Approval Workflow

Difficulty: Medium

## Goal

Build an order approval workflow with authorization rules and audit fields.

## Suggested Packages

```text
com.example.orders.domain
com.example.orders.authorization
com.example.orders.repository
com.example.orders.service
```

## Requirements

- Create `Order` with statuses such as `DRAFT`, `SUBMITTED`, `APPROVED`, and `REJECTED`.
- Create `OrderService`.
- Create `AuthorizationService`.
- Allow only approved roles to approve orders.
- Reject invalid status transitions.
- Store `createdAt`, `submittedAt`, and `approvedAt` using `Clock`.
- Write tests for successful and failed transitions.

## Expected Behavior

Orders move through valid states only. Unauthorized users cannot approve orders. Audit fields are predictable in tests.

## Test Cases

- Submitted order can be approved by an authorized role.
- Draft order cannot be approved.
- Rejected order cannot be approved.
- Unauthorized role is rejected.
- Approved timestamp uses the injected `Clock`.

## Hints

Use an enum for status and a separate class for authorization checks.

## Common Mistakes

- Comparing roles with unclear string literals everywhere.
- Calling `LocalDateTime.now()` directly inside domain logic.
- Allowing invalid transitions because the service updates fields directly.

## Deliverables

- Domain model.
- Authorization service.
- Repository interface.
- Workflow service.
- Unit tests using fake repositories.
- Short sequence diagram in Markdown.

## Bonus Challenge

Add rejection comments and require a non-blank comment when rejecting an order.

## Mini Project 4: Customer Account Persistence Layer

Difficulty: Hard

## Goal

Build a small JPA persistence layer with entities, repositories, transactions, and JPQL queries.

## Suggested Packages

```text
com.example.accounts.domain
com.example.accounts.repository
com.example.accounts.service
```

## Requirements

- Create `Customer` and `Account` entities.
- Model a one-to-many relationship from customer to accounts.
- Use a test persistence unit with H2.
- Create repository methods for save, find by ID, find by email, and list active accounts.
- Use JPQL parameters safely.
- Add a transaction rollback test.
- Avoid real credentials.

## Expected Behavior

Repository methods persist and query entity data correctly in a local test database.

## Test Cases

- Customer can be saved and loaded.
- Customer email lookup works.
- Active accounts query returns only active accounts.
- Missing record returns an empty optional or documented exception.
- Rollback test proves failed transaction does not persist data.

## Hints

Keep transaction ownership in the service or test setup. Do not hide transaction boundaries inside every repository method unless your design explicitly chooses that style.

## Common Mistakes

- Mixing table names and entity names in JPQL.
- Forgetting the owning side of a relationship.
- Using the same database configuration for development and tests.
- Leaving transactions open after tests.

## Deliverables

- Entity classes.
- Repository classes.
- Test persistence configuration.
- Repository tests.
- Rollback test.
- Short README explaining transaction boundaries.

## Bonus Challenge

Add pagination for account listing.

## Mini Project 5: Book Lending Workflow

Difficulty: Hard

## Goal

Create a layered application for lending books to members.

## Suggested Packages

```text
com.example.library.domain
com.example.library.repository
com.example.library.service
com.example.library.reporting
```

## Requirements

- Create `Book`, `Member`, and `Loan` domain classes.
- Prevent lending unavailable books.
- Prevent members from exceeding a maximum active-loan limit.
- Track loan dates with `Clock`.
- Add a report listing overdue loans.
- Use repository interfaces and fake repositories in tests.
- Add custom exceptions for domain failures.

## Expected Behavior

The lending service enforces rules consistently and the overdue report is generated from active loans.

## Test Cases

- Available book can be lent.
- Unavailable book is rejected.
- Member at loan limit is rejected.
- Returned loan is not reported as overdue.
- Overdue report respects the injected clock.

## Hints

Separate command workflows from report queries. They can share repositories but should have clear responsibilities.

## Common Mistakes

- Letting report code change loan state.
- Using system time directly in tests.
- Returning domain collections that callers can mutate.

## Deliverables

- Domain model.
- Lending service.
- Report service.
- Fake repositories.
- Unit tests.
- Short README explaining business rules.

## Bonus Challenge

Add a renewal workflow with a maximum renewal count.

## Mini Project 6: Employee Department Reporting

Difficulty: Hard

## Goal

Build an application that imports employees, stores departments, and creates department-level reports.

## Suggested Packages

```text
com.example.employeeimport.domain
com.example.employeeimport.importing
com.example.employeeimport.repository
com.example.employeeimport.reporting
```

## Requirements

- Import employee rows from CSV.
- Validate employee ID, name, department code, and salary.
- Store employees through repository interfaces.
- Generate a department salary report.
- Calculate employee count, minimum salary, maximum salary, and average salary per department.
- Return immutable reports.
- Add tests for empty departments and malformed rows.

## Expected Behavior

Valid employee rows are accepted, invalid rows are reported, and department reports are generated from valid data only.

## Test Cases

- Valid employees are imported.
- Missing department code is rejected.
- Invalid salary is rejected.
- Department report groups employees correctly.
- Empty department returns a documented empty result.

## Hints

Use `Map<DepartmentCode, List<Employee>>` or stream grouping for the report layer.

## Common Mistakes

- Mixing parsing, persistence, and reporting in one large method.
- Ignoring row numbers in import errors.
- Using mutable report objects.
- Assuming every department has employees.

## Deliverables

- Import service.
- Employee validator.
- Repository interface.
- Department report service.
- Import tests.
- Report tests.
- Short README explaining edge cases.

## Bonus Challenge

Add CSV export for the generated department report.

## Mini Project 7: Payment Reconciliation

Difficulty: Hard

## Goal

Compare invoice records with payment records and produce a reconciliation report.

## Suggested Packages

```text
com.example.reconciliation.domain
com.example.reconciliation.importing
com.example.reconciliation.service
com.example.reconciliation.reporting
```

## Requirements

- Import invoices from one CSV file.
- Import payments from another CSV file.
- Match payments to invoices by invoice number.
- Report paid invoices, partially paid invoices, unpaid invoices, and unmatched payments.
- Use `BigDecimal` for money.
- Include row-level import errors.
- Add tests for matching and mismatch cases.

## Expected Behavior

The reconciliation service compares validated invoices and payments and returns a clear immutable report.

## Test Cases

- Fully paid invoice is reported as paid.
- Partial payment is reported as partial.
- Invoice with no payment is unpaid.
- Payment without a matching invoice is unmatched.
- Invalid payment amount is rejected during import.

## Hints

Keep import validation separate from reconciliation rules.

## Common Mistakes

- Matching by customer name instead of invoice number.
- Comparing `BigDecimal` with `equals` when scale may differ.
- Dropping unmatched payments from the report.
- Combining import errors with reconciliation warnings without labels.

## Deliverables

- Invoice import service.
- Payment import service.
- Reconciliation service.
- Immutable report.
- Unit tests.
- Short README explaining matching rules.

## Bonus Challenge

Add a tolerance rule for tiny rounding differences and document it clearly.

## Final Review Questions

- Which layer owns business rules?
- Which class coordinates a workflow?
- Which class owns persistence calls?
- Which result objects should be immutable?
- Which tests prove failure safety?
- Which tests prove transaction rollback?
- Which dependencies should be replaced by fakes in unit tests?
- Which examples need integration tests instead of only unit tests?
