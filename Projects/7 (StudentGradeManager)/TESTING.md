# Testing Student Grade Manager

## Testing approach

The project uses no test framework. Compile the source and run Main, or create a small driver that calls Student and GradeBook directly.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add students | Add two students with different IDs | Both appear in listStudents |
| Record grades | Add several grades to one student | Grades are stored under their subjects |
| Calculate statistics | Request average, highest, and lowest | Values match the recorded grades |
| Pass/fail | Test averages below and above 60 | Below 60 fails; 60 or above passes |
| Sort students | List students by average | Highest average appears first |
| Remove student | Remove an existing ID | Method returns true and student disappears |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Boundary grades | Record 0 and 100 | Both grades are accepted |
| No grades | Request average for a new student | Average is 0.0 and status is fail |
| Missing extrema | Request highest or lowest with no grades | IllegalStateException |
| Duplicate names | Add different IDs with the same name | Both students are accepted |
| Remove twice | Remove the same student twice | First returns true; second returns false |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid grade | Use a grade below 0, above 100, NaN, or infinity | IllegalArgumentException |
| Duplicate ID | Add the same student ID twice | IllegalArgumentException |
| Blank values | Use blank ID, name, or subject | IllegalArgumentException |
| Unknown student | Record a grade for an unknown ID | IllegalArgumentException |
| Null student | Add null to GradeBook | IllegalArgumentException |

## Manual testing checklist

- [ ] Compile all source files without external dependencies.
- [ ] Run Main and inspect the printed statistics.
- [ ] Verify grade boundaries 0 and 100.
- [ ] Verify invalid grades do not change existing data.
- [ ] Verify duplicate IDs are rejected.
- [ ] Verify returned grade collections cannot be modified.
- [ ] Verify students sort correctly when averages are equal.
