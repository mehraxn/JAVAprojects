# Maven CI CD MCQs

### Q1. What does `mvn test` do?

A. Compiles main code  
B. Compiles test code  
C. Runs unit tests  
D. Deletes the repository  
E. May use Surefire

**Correct:** A, B, C, E  
**Explanation:** A, B, C, and E are part of Maven test behavior. D is false.

### Q2. Which Maven scope is right for JUnit?

A. `test`  
B. `compile` always  
C. `provided` always  
D. `runtime` always  
E. No dependency needed

**Correct:** A  
**Explanation:** JUnit normally belongs in test scope. The other options are incorrect for normal unit tests.

### Q3. What does `runtime` scope mean?

A. Needed when running  
B. Not needed to compile main code  
C. Useful for some database drivers  
D. Same as test-only  
E. Always provided by the container

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E confuse scopes.

### Q4. What is Maven Wrapper useful for?

A. Running a known Maven version  
B. Avoiding global Maven installation requirements  
C. Making CI setup easier  
D. Replacing Java  
E. Deleting dependencies

**Correct:** A, B, C  
**Explanation:** A-C are wrapper benefits. D and E are false.

### Q5. What is `maven.compiler.release` for?

A. Setting target Java API level  
B. Making local and CI Java behavior clearer  
C. Choosing Git branch names  
D. Replacing tests  
E. Avoiding accidental newer APIs

**Correct:** A, B, E  
**Explanation:** A, B, and E are correct. C and D are unrelated.

### Q6. Surefire usually runs which tests?

A. Unit tests  
B. Classes such as `*Test`  
C. Integration tests only  
D. During the `test` phase  
E. YAML workflows

**Correct:** A, B, D  
**Explanation:** A, B, and D describe Surefire. C describes Failsafe more often. E is unrelated.

### Q7. Failsafe usually runs which tests?

A. Integration tests  
B. Classes such as `*IT`  
C. During integration-test/verify phases  
D. Unit tests only  
E. Source formatting only

**Correct:** A, B, C  
**Explanation:** A-C are correct. D and E are wrong.

### Q8. What is JaCoCo used for?

A. Test coverage reports  
B. Line coverage  
C. Branch coverage  
D. Guaranteeing bug-free code  
E. Replacing assertions

**Correct:** A, B, C  
**Explanation:** A-C are coverage features. D and E are false.

### Q9. What belongs in `src/test/resources`?

A. Test CSV files  
B. Test configuration  
C. Main application source code  
D. Expected test output files  
E. Build output

**Correct:** A, B, D  
**Explanation:** A, B, and D are test resources. C and E are wrong folders.

### Q10. What is a Maven profile?

A. Named build configuration variation  
B. A Java class type  
C. Activated with `-PprofileName`  
D. Useful for controlled dev/test settings  
E. A replacement for `pom.xml`

**Correct:** A, C, D  
**Explanation:** A, C, and D are correct. B and E are not.

### Q11. What should you inspect first in a Maven failure?

A. The first real error  
B. The phase that failed  
C. Whether it is dependency, compile, test, or plugin failure  
D. Only the last line  
E. Random files

**Correct:** A, B, C  
**Explanation:** A-C are practical diagnostics. D and E often waste time.

### Q12. What is a CI artifact?

A. A file uploaded from a CI run  
B. Useful for test reports  
C. Always source code  
D. Can include logs or reports  
E. A Java keyword

**Correct:** A, B, D  
**Explanation:** A, B, and D are correct. C and E are wrong.

### Q13. What does semantic versioning use?

A. MAJOR  
B. MINOR  
C. PATCH  
D. Random words only  
E. Optional release tags

**Correct:** A, B, C, E  
**Explanation:** A-C define semantic versioning; E is often used. D is wrong.

### Q14. What should a build checklist include?

A. Run tests  
B. Check Java version  
C. Check generated files are ignored  
D. Review README instructions  
E. Commit `target` always

**Correct:** A, B, C, D  
**Explanation:** A-D are good checks. E is normally wrong.

### Q15. What is a common CI problem?

A. Different Java version than local  
B. Missing files not committed  
C. Tests depend on local paths  
D. CI runs no tests  
E. Tests are too clear

**Correct:** A, B, C, D  
**Explanation:** A-D are real problems. E is not.
