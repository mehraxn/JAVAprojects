# Testing Student Grade Manager

The project has no external test dependencies. Compile and run `Main`, or call the classes from a small Java driver.

## Manual test cases

1. Add two students with different IDs and confirm both appear in `listStudents()`.
2. Add several valid grades and verify the average, highest grade, and lowest grade.
3. Verify an average of 60 or more reports passing and an average below 60 reports failing.
4. Add grades `0` and `100` and confirm both are accepted.
5. Try grades below `0`, above `100`, `NaN`, and infinity; expect `IllegalArgumentException`.
6. Add the same student ID twice; expect `IllegalArgumentException`.
7. Record a grade for an unknown student; expect `IllegalArgumentException`.
8. Request highest or lowest grade for a student with no grades; expect `IllegalStateException`.
9. Verify `listStudentsByAverage()` returns highest average first.
10. Remove an existing student, then remove the same ID again; expect `true` followed by `false`.
