# Data Import Logging Reports MCQs

### Q1. Why can `String.split(",")` fail for CSV?

A. Quoted values may contain commas  
B. Escaped quotes exist  
C. Empty fields need careful handling  
D. It parses every CSV perfectly  
E. Real CSV can be more complex than simple examples

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is false.

### Q2. What should an `ImportResult` include?

A. Rows read  
B. Rows imported  
C. Rows skipped  
D. Errors and warnings  
E. Random UI color

**Correct:** A, B, C, D  
**Explanation:** A-D make imports testable. E is unrelated.

### Q3. What belongs in an import row error?

A. Row number  
B. Field name  
C. Message  
D. Raw line when useful  
E. Secret credentials

**Correct:** A, B, C, D  
**Explanation:** A-D are useful. E is unsafe.

### Q4. Why specify UTF-8?

A. Avoid platform default surprises  
B. Make file reading predictable  
C. Replace all validation  
D. Document encoding choice  
E. Avoid every parse error

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are false.

### Q5. What does `DateTimeFormatter` help with?

A. Date parsing  
B. Date formatting  
C. Clear expected date patterns  
D. Database indexing  
E. Handling parse errors with exceptions

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is unrelated.

### Q6. Which log levels are real common levels?

A. trace  
B. debug  
C. info  
D. warn  
E. sparkle

**Correct:** A, B, C, D  
**Explanation:** A-D are common log levels. E is not.

### Q7. What is an appender?

A. Logging destination  
B. Console output target  
C. File output target  
D. Rolling file target  
E. Java arithmetic operator

**Correct:** A, B, C, D  
**Explanation:** A-D are correct. E is unrelated.

### Q8. What should not be logged?

A. Passwords  
B. Private payment details  
C. Import summary counts  
D. Access tokens  
E. Sensitive personal data

**Correct:** A, B, D, E  
**Explanation:** A, B, D, and E are sensitive. C is usually safe.

### Q9. Which report edge cases matter?

A. Empty data  
B. One value  
C. All values equal  
D. Division by zero  
E. Bucket boundaries

**Correct:** A, B, C, D, E  
**Explanation:** All options are important report test cases.

### Q10. Why use immutable report objects?

A. Prevent accidental mutation  
B. Make results stable  
C. Support safer boundaries  
D. Avoid every calculation  
E. Make tests clearer

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is false.

### Q11. What is a histogram?

A. Values grouped into buckets  
B. A distribution summary  
C. A database transaction  
D. Useful for score or order-total ranges  
E. A CSV parser only

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong.

### Q12. What is an outlier?

A. A value far from most other values  
B. Always invalid data  
C. Context-dependent  
D. Should be detected with a documented rule  
E. A Maven plugin

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B and E are false.

### Q13. Why use `BigDecimal` for financial reports?

A. Avoid floating-point money errors  
B. Control scale  
C. Control rounding mode  
D. It is always shorter than `double`  
E. Exact decimal input can be represented carefully

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are correct. D is false.

### Q14. What should data import tests cover?

A. Valid file  
B. Empty file  
C. Header only  
D. Invalid rows  
E. Missing file

**Correct:** A, B, C, D, E  
**Explanation:** All are useful tests.

### Q15. What is a partial-success import?

A. Valid rows are imported  
B. Invalid rows are reported/skipped  
C. No errors are collected  
D. Useful when rows are independent  
E. Always better than all-or-nothing

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong.
