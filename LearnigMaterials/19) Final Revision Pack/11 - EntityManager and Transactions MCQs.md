# EntityManager and Transactions MCQs

### Q1. What does `EntityManager` do?

A. Persists entities  
B. Finds entities  
C. Creates JPQL queries  
D. Replaces Java classes  
E. Removes entities

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are `EntityManager` responsibilities. D is false.

### Q2. What is `EntityManagerFactory`?

A. Creates `EntityManager` instances  
B. Usually expensive to create  
C. Should often be created once per application lifecycle  
D. Must be created for every query  
E. Should be closed on shutdown

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is inefficient and usually wrong.

### Q3. What is a persistence context?

A. Set of managed entities  
B. Owned by an `EntityManager`  
C. Provides first-level cache  
D. A Git branch  
E. Enables dirty checking

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is unrelated.

### Q4. What does first-level cache guarantee inside one `EntityManager`?

A. Same database row maps to same managed object instance  
B. It is shared across all applications  
C. It prevents all database queries forever  
D. It is scoped to the persistence context  
E. It helps identity consistency

**Correct:** A, D, E  
**Explanation:** A, D, and E are correct. B and C exaggerate its scope.

### Q5. What is dirty checking?

A. JPA detects changes to managed entities  
B. Changes can be flushed at commit  
C. It works for detached objects automatically  
D. It avoids every transaction  
E. It depends on persistence context management

**Correct:** A, B, E  
**Explanation:** A, B, and E are correct. C and D are false.

### Q6. What does `flush` do?

A. Sends pending SQL to the database  
B. Commits the transaction by itself  
C. Can reveal constraint errors earlier  
D. Does not replace commit  
E. Can happen before commit

**Correct:** A, C, D, E  
**Explanation:** A, C, D, and E are correct. B is the common mistake.

### Q7. What does `clear` do?

A. Detaches all managed entities  
B. Deletes all database rows  
C. Stops dirty checking for previously managed objects  
D. Closes the factory  
E. Clears the persistence context

**Correct:** A, C, E  
**Explanation:** A, C, and E are correct. B and D are wrong.

### Q8. What does `detach` do?

A. Detaches one entity  
B. Stops automatic tracking for that entity  
C. Removes the database row  
D. Can make later changes unsaved unless merged  
E. Commits the transaction

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong.

### Q9. What is true about `merge`?

A. Copies detached state into a managed instance  
B. Returns the managed instance  
C. The returned value should not be ignored if continuing updates  
D. Always deletes the detached instance  
E. Is the same as `persist`

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are false.

### Q10. Which operations generally require a transaction?

A. `persist`  
B. `remove`  
C. modifying managed entities  
D. rollback practice  
E. reading a local variable

**Correct:** A, B, C, D  
**Explanation:** A-D involve persistence changes or transaction handling. E is unrelated.

### Q11. What should happen after an exception inside a transaction?

A. Roll back if active  
B. Commit anyway  
C. Preserve the root cause  
D. Throw or report failure honestly  
E. Return success silently

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B and E are dangerous.

### Q12. Why separate development and test persistence units?

A. Keep test data separate  
B. Allow create-drop for tests  
C. Avoid damaging development data  
D. Make names irrelevant  
E. Improve repeatability

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is false; names must match.

### Q13. Which schema option is useful for tests?

A. `create-drop`  
B. `validate` only always  
C. `none` always  
D. It depends on test strategy  
E. Never create schema

**Correct:** A, D  
**Explanation:** A is often useful for tests; D acknowledges context. B, C, and E are too absolute.

### Q14. What is a good `EntityManager` practice?

A. One per unit of work  
B. Close after use  
C. Static global for everything  
D. Clear transaction boundaries  
E. Handle rollback

**Correct:** A, B, D, E  
**Explanation:** A, B, D, and E are good practices. C is a common mistake.

### Q15. What does `close` do?

A. Releases `EntityManager` resources  
B. Makes the `EntityManager` unusable  
C. Detaches managed entities from that context  
D. Deletes every database row  
E. Should be done after the unit of work

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is false.
