# Student Grade Manager

## Description

Student Grade Manager is a small, in-memory Java console project for recording students and grades. It calculates useful statistics without databases, external libraries, or a graphical interface.

## Features

- Add and remove students using unique IDs.
- Record multiple grades for named subjects.
- Reject grades outside the 0–100 range.
- Calculate overall average, highest grade, and lowest grade.
- Report pass or fail using a 60-point threshold.
- List students from highest to lowest average.
- Protect returned grade collections from modification.

## Java concepts practiced

- Classes, objects, constructors, and encapsulation
- Map and List collections
- Input validation and exceptions
- Iteration, sorting, and Comparator
- Defensive copies and unmodifiable collections

## Main classes

- Student: owns student identity, subject grades, and grade calculations.
- GradeBook: manages students and coordinates grade operations.
- Main: demonstrates the implemented workflow with sample data.

## How the program works

1. Create a GradeBook.
2. Add Student objects with unique IDs.
3. Record grades through GradeBook.
4. Student validates each grade and stores it by subject.
5. GradeBook can return students sorted by calculated average.

Data exists only for the current program run.

## Example usage

From this project folder:

~~~powershell
javac -d out src\studentgrademanager\*.java
java -cp out studentgrademanager.Main
~~~

The demo prints each student's average, pass/fail status, highest grade, and lowest grade.

## Possible future improvements

- Add an interactive menu with Scanner.
- Add subject-specific averages.
- Save and load grade data from a file.
- Add dependency-free automated test drivers.
- Produce class and subject summary reports.
