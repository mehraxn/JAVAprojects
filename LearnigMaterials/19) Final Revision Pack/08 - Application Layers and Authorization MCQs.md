# Application Layers and Authorization MCQs

### Q1. What is a domain workflow?

A. A meaningful business operation  
B. A package naming rule  
C. A database index only  
D. A use case such as approve, cancel, assign, or transfer  
E. A code formatter setting

**Correct:** A, D  
**Explanation:** A and D define workflows. B, C, and E are unrelated.

### Q2. What is a domain invariant?

A. A rule that must always remain true  
B. A temporary UI message  
C. An example such as account balance not becoming negative  
D. A role name only  
E. A generated report file

**Correct:** A, C  
**Explanation:** A defines invariant; C is an example. B, D, and E are not invariants.

### Q3. Why validate before mutating state?

A. To avoid half-updated objects  
B. To make rollback easier  
C. To hide failures  
D. To preserve consistency  
E. To remove the need for tests

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong because failures should be visible and tested.

### Q4. What does all-or-nothing mean?

A. All required changes succeed together  
B. Partial changes are acceptable without reporting  
C. Failure leaves previous state safe  
D. Every method must be static  
E. Rollback may be needed

**Correct:** A, C, E  
**Explanation:** A, C, and E describe all-or-nothing behavior. B and D are incorrect.

### Q5. Which are validation categories?

A. Input validation  
B. Existence validation  
C. Duplicate validation  
D. Relationship validation  
E. Screen color validation

**Correct:** A, B, C, D  
**Explanation:** A-D are real validation categories. E is not part of business workflow validation.

### Q6. What is idempotency?

A. Repeating an operation has no additional side effect  
B. Running a workflow twice always creates two records  
C. Useful for retryable operations  
D. Relevant to cancellation and payment retry  
E. A JPA annotation

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B is the problem idempotency avoids. E is unrelated.

### Q7. Why centralize authorization?

A. To avoid scattered role checks  
B. To make permission rules easier to test  
C. To remove all service validation  
D. To make UI checks the only protection  
E. To keep workflow rules consistent

**Correct:** A, B, E  
**Explanation:** A, B, and E are correct. C and D weaken correctness.

### Q8. What should an authorization service commonly provide?

A. `requireRole`  
B. `requireAnyRole`  
C. Permission matrix support or clear rules  
D. Direct database schema generation  
E. Password printing

**Correct:** A, B, C  
**Explanation:** A-C are useful. D and E are unrelated or unsafe.

### Q9. Which audit fields are common?

A. `createdBy`  
B. `createdAt`  
C. `modifiedBy`  
D. `modifiedAt`  
E. `randomBy`

**Correct:** A, B, C, D  
**Explanation:** A-D are common audit fields. E is not meaningful.

### Q10. Why use `Clock` in services?

A. To make time deterministic in tests  
B. To avoid hard-coded system time  
C. To generate database indexes  
D. To support audit timestamps  
E. To replace all date parsing

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are unrelated.

### Q11. Which tests should workflow tests include?

A. Success path  
B. Invalid input path  
C. Unauthorized path  
D. State unchanged after failure  
E. Only console output

**Correct:** A, B, C, D  
**Explanation:** A-D are important. E alone is not enough.

### Q12. What is a fake repository?

A. A simple working in-memory implementation for tests  
B. A test double that can avoid a database  
C. A broken repository  
D. A useful tool for service tests  
E. A production-only database driver

**Correct:** A, B, D  
**Explanation:** A, B, and D describe fake repositories. C and E are wrong.

### Q13. Which exceptions match business logic failures?

A. `NotFoundException`  
B. `DuplicateIdException`  
C. `UnauthorizedException`  
D. `BusinessRuleException`  
E. `GreenButtonException`

**Correct:** A, B, C, D  
**Explanation:** A-D are meaningful business exceptions. E is not.

### Q14. Where should protected operation checks happen?

A. In the service workflow  
B. Only in UI button visibility  
C. In a centralized authorization service called by the workflow  
D. Nowhere if the user seems trusted  
E. In tests too

**Correct:** A, C, E  
**Explanation:** A and C protect the operation; E verifies it. B and D are unsafe.

### Q15. Which are good workflow sequence diagram participants?

A. Caller  
B. Service  
C. Authorization service  
D. Repository  
E. Random font selector

**Correct:** A, B, C, D  
**Explanation:** A-D can clarify workflow calls. E is unrelated.
