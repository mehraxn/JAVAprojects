# Student Grade Manager

A small in-memory Java application for managing students and their grades.

## Implemented features

- Add and remove students with unique IDs.
- Record multiple grades per subject.
- Calculate each student's overall average.
- Show pass/fail status using a 60-point passing grade.
- Find each student's highest and lowest grade.
- Sort students from highest to lowest average.
- Reject blank data, duplicate student IDs, unknown students, and grades outside 0–100.

## Structure

- `Student` owns identity and grade data and calculates statistics.
- `GradeBook` manages students and delegates grade operations.
- `Main` runs a small demonstration.

Source files are under `src/studentgrademanager` and use only standard Java.

## Run

From this project folder:

```powershell
javac -d out src\studentgrademanager\*.java
java -cp out studentgrademanager.Main
```

See `TESTING.md` for manual test cases.
