# JPA Hibernate MCQs

### Q1. What does `@Entity` mean?

A. The class is persistent  
B. The class can map to a database table  
C. The class is automatically immutable  
D. The class needs no ID  
E. JPA should manage instances of the class

**Correct:** A, B, E  
**Explanation:** A, B, and E are correct. C and D are false.

### Q2. What marks the primary key field?

A. `@Id`  
B. `@Column` only  
C. `@Table`  
D. `@GeneratedValue` alone  
E. `@Transient`

**Correct:** A  
**Explanation:** `@Id` marks the primary key. The other annotations do not by themselves.

### Q3. Which are ID generation strategies?

A. `IDENTITY`  
B. `SEQUENCE`  
C. `AUTO`  
D. `MANUAL_ONLY_ALWAYS`  
E. UUID concept in some designs

**Correct:** A, B, C, E  
**Explanation:** A-C are JPA strategies; E is a common concept. D is not a standard strategy.

### Q4. What does `nullable = false` express?

A. Database column should reject null  
B. Java automatically trims strings  
C. A database constraint idea  
D. No validation is needed anywhere else  
E. It can support data quality

**Correct:** A, C, E  
**Explanation:** A, C, and E are correct. B and D are false.

### Q5. Which relationship commonly owns a foreign key in order items?

A. `OrderItem` many-to-one side  
B. `Order` inverse one-to-many side  
C. The side with `@JoinColumn`  
D. The side with `mappedBy`  
E. The helper method name

**Correct:** A, C  
**Explanation:** A and C identify the owning side. `mappedBy` marks the inverse side.

### Q6. What does `mappedBy` reference?

A. Field name on the owning side  
B. Table name always  
C. Column name always  
D. Relationship ownership  
E. A Java package name

**Correct:** A, D  
**Explanation:** A is exactly what `mappedBy` points to; D is the purpose. B, C, and E are wrong.

### Q7. Which cascade type removes children when parent is removed?

A. `REMOVE`  
B. `PERSIST`  
C. `MERGE`  
D. `ALL` includes remove  
E. `LAZY`

**Correct:** A, D  
**Explanation:** A removes related entities; D includes all cascade types. E is fetch behavior, not cascade.

### Q8. What is orphan removal?

A. Delete child when removed from parent relationship  
B. Same as lazy loading  
C. Useful for parent-owned child entities  
D. Dangerous for shared entities  
E. Required for every many-to-many

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B and E are wrong.

### Q9. What does lazy loading mean?

A. Related data loads when accessed  
B. Related data always loads immediately  
C. Can cause lazy access errors after context close  
D. Often preferred for large collections  
E. Deletes child rows

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B is eager loading; E is unrelated.

### Q10. What is the N+1 problem?

A. One query plus one query per parent row  
B. A performance issue  
C. Often caused by lazy relationship loops  
D. Solved by ignoring SQL  
E. Sometimes helped by fetch joins

**Correct:** A, B, C, E  
**Explanation:** A-C define it; E is one solution. D is wrong.

### Q11. Why is entity equality tricky?

A. Generated IDs can be null before persist  
B. Mutable fields can break hash-based collections  
C. Collections should usually be included in hashCode  
D. Business keys must be chosen carefully  
E. Every entity must use name only

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are common mistakes.

### Q12. What is `@Transient` used for?

A. Field not persisted by JPA  
B. Primary key generation  
C. UI-only or calculated state  
D. Defining a foreign key  
E. Preventing all validation

**Correct:** A, C  
**Explanation:** A and C are correct. B, D, and E are wrong.

### Q13. What can cause "unknown entity"?

A. Missing `@Entity`  
B. Entity not included in configuration/scanning  
C. Wrong query class name  
D. Too many tests  
E. A non-entity class passed to JPA

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are plausible. D is unrelated.

### Q14. What is a foreign key constraint?

A. Database rule linking rows  
B. Data integrity protection  
C. Java constructor only  
D. A relationship support mechanism  
E. A logging level

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong.

### Q15. When is many-to-many often replaced by an explicit entity?

A. When the relationship has extra fields  
B. For enrollment date or status  
C. When final grade belongs to the relationship  
D. Always, even for tiny examples  
E. Never

**Correct:** A, B, C  
**Explanation:** A-C are good reasons. D and E are extreme and incorrect.
