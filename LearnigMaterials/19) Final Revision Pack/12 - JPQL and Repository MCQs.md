# JPQL and Repository MCQs

### Q1. JPQL queries what?

A. Entity classes and fields  
B. Tables and columns only  
C. Java entity model  
D. `SELECT e FROM Entity e` style queries  
E. CSS selectors

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B describes SQL more than JPQL. E is unrelated.

### Q2. Why use `TypedQuery`?

A. Type-safe results  
B. Avoids raw casts  
C. Can use `setParameter`  
D. Makes all queries faster automatically  
E. Replaces transactions

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are false.

### Q3. What is the safe way to pass user input?

A. Named parameters  
B. `setParameter`  
C. String concatenation  
D. Raw query text injection  
E. Positional or named parameters used carefully

**Correct:** A, B, E  
**Explanation:** A, B, and E are safe. C and D are unsafe.

### Q4. What is a normal join useful for?

A. Filtering by related data  
B. Joining entity relationships  
C. Always loading all child collections  
D. Querying related fields  
E. Replacing repositories

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C describes fetch join behavior more closely. E is wrong.

### Q5. What does `FETCH JOIN` do?

A. Loads a relationship with the main entity  
B. Can reduce repeated lazy queries  
C. Can create duplicate parent rows  
D. Never needs `DISTINCT`  
E. Should be used for every relationship always

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are mistakes.

### Q6. Why use DTO projections?

A. Select only needed fields  
B. Return read-only report data  
C. Avoid exposing entities unnecessarily  
D. Always update entities faster  
E. Use constructor expressions

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is not the purpose.

### Q7. What is required for stable pagination?

A. `setFirstResult`  
B. `setMaxResults`  
C. Deterministic `ORDER BY`  
D. Negative page size  
E. Clear validation of page inputs

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is invalid.

### Q8. What can `getSingleResult` throw?

A. `NoResultException`  
B. `NonUniqueResultException`  
C. Nothing ever  
D. Errors when result count is not exactly one  
E. A CSS error

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are false.

### Q9. What do JPQL bulk updates do?

A. Update rows directly  
B. Bypass managed entity dirty checking  
C. Require care with persistence context staleness  
D. Never need transactions  
E. May require `clear`

**Correct:** A, B, C, E  
**Explanation:** A-C and E are correct. D is false.

### Q10. What is N+1 in JPQL usage?

A. Parent query plus one query per related collection access  
B. Often caused by lazy loops  
C. Sometimes solved with fetch join  
D. Solved by ignoring generated SQL  
E. A pagination formula only

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are wrong.

### Q11. Why use Criteria API?

A. Dynamic queries  
B. Type-oriented query building  
C. More verbose than JPQL  
D. Always simpler for static queries  
E. Avoids every runtime error

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are false.

### Q12. Who usually owns a multi-object transaction boundary?

A. Service/use-case method  
B. Random getter  
C. Repository only in every case  
D. The layer that understands the full workflow  
E. CLI printing method

**Correct:** A, D  
**Explanation:** A and D are correct. B and E are unrelated. C is too absolute.

### Q13. What is a repository method good for?

A. Hiding JPQL  
B. Returning clear results  
C. Keeping query code out of services where practical  
D. Printing menus  
E. Encapsulating persistence access

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are repository purposes. D belongs elsewhere.

### Q14. What is JPQL injection risk?

A. User input changes query meaning if concatenated  
B. Prevented by parameters  
C. Impossible in JPQL  
D. Similar habit to SQL injection prevention  
E. Encouraged by string concatenation

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are false.

### Q15. What should `findBySku` usually return if missing is normal?

A. `Optional<Product>`  
B. `null` silently always  
C. A clear exception if method promises required  
D. A list of random products  
E. A safe result type matching method meaning

**Correct:** A, C, E  
**Explanation:** A, C, and E are valid depending on contract. B and D are poor designs.
