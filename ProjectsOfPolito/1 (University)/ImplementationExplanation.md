# University Management System - Complete Implementation Guide

## Overview
This project implements a comprehensive university management system that handles students, courses, teachers, exams, and awards. The system is built using Java and belongs to the `university` package.

---

## R1: University Basic Information

### Task Description
Create the foundation of the University class with:
- Constructor that accepts the university name
- Getter method for the university name
- Setter method for the Rector's first and last name
- Getter method that returns the Rector's full name

### Implementation Changes

**Fields Added:**
```java
private String name;
private String rectorFirst;
private String rectorLast;
```

**Constructor Implementation:**
```java
public University(String name) {
    this.name = name;
}
```

**Methods Implemented:**
- `getName()`: Returns the university name
- `setRector(String first, String last)`: Stores rector's first and last name
- `getRector()`: Returns rector's full name as "First Last"

---

## R2: Student Enrollment

### Task Description
Implement student enrollment functionality:
- Enroll new students with first and last names
- Assign progressive ID numbers starting from 10000
- Retrieve student information by ID

### Implementation Changes

**Fields Added:**
```java
private Student[] students = new Student[1000];
private int studentCount = 0;
```

**Inner Class Created:**
```java
private class Student {
    int id;
    String firstName;
    String lastName;
    int[] courses = new int[25];
    int courseCount = 0;
    int[] examCourses = new int[25];
    int[] examGrades = new int[25];
    int examCount = 0;
}
```

**Methods Implemented:**
- `enroll(String first, String last)`: Creates new student, assigns ID (10000 + studentCount), stores in array, logs enrollment
- `student(int id)`: Searches for student by ID, returns formatted string "ID FirstName LastName"

**Key Changes:**
- Student IDs start at 10000 and increment
- Array can hold up to 1000 students
- Logging added for enrollment: "New student enrolled: ID, First Last"

---

## R3: Course Management

### Task Description
Implement course activation and retrieval:
- Activate courses with title and teacher name
- Assign progressive course codes starting from 10
- Retrieve course information by code

### Implementation Changes

**Fields Added:**
```java
private Course[] courses = new Course[50];
private int courseCount = 0;
```

**Inner Class Created:**
```java
private class Course {
    int code;
    String title;
    String teacher;
    int[] students = new int[100];
    int studentCount = 0;
    int[] grades = new int[100];
    int gradeCount = 0;
}
```

**Methods Implemented:**
- `activate(String title, String teacher)`: Creates new course, assigns code (10 + courseCount), logs activation
- `course(int code)`: Searches for course, returns formatted string "code,title,teacher"

**Key Changes:**
- Course codes start at 10 and increment
- Array can hold up to 50 courses
- Logging added: "New course activated: code, title teacher"

---

## R4: Course Attendance

### Task Description
Implement student registration for courses:
- Register students to attend courses
- List all attendees of a course
- Show study plan for a student

### Implementation Changes

**Method: `register(int studentID, int courseCode)`**
- Finds student and course by their IDs
- Adds courseCode to student's courses array
- Adds studentID to course's students array
- Logs: "Student ID signed up for course CODE"

**Method: `listAttendees(int courseCode)`**
- Finds course by code
- Iterates through course's student IDs
- For each ID, calls `student(id)` to get formatted info
- Returns students separated by newline character '\n'

**Method: `studyPlan(int studentID)`**
- Finds student by ID
- Iterates through student's course codes
- For each code, calls `course(code)` to get formatted info
- Returns courses separated by newline character '\n'

**Key Changes:**
- Bidirectional relationship: students store courses, courses store students
- Maximum 100 attendees per course
- Maximum 25 courses per student

---

## R5: Exam Management

### Task Description
Implement exam recording and grade averaging:
- Record exam grades (0-30) for students
- Calculate average grade for a student
- Calculate average grade for a course

### Implementation Changes

**Method: `exam(int studentId, int courseID, int grade)`**
- Finds student and course
- Stores courseID and grade in student's exam arrays
- Stores grade in course's grades array
- Increments counters
- Logs: "Student ID took an exam in course CODE with grade GRADE"

**Method: `studentAvg(int studentId)`**
- Finds student by ID
- If no exams taken: returns "Student ID hasn't taken any exams"
- Calculates sum of all exam grades
- Returns: "Student ID : AVG_GRADE"

**Method: `courseAvg(int courseId)`**
- Finds course by code
- If no exams taken: returns "No student has taken the exam in COURSE_TITLE"
- Calculates average of all grades for that course
- Returns: "The average for the course COURSE_TITLE is: COURSE_AVG"

**Key Changes:**
- Student class now tracks exam courses and grades separately
- Course class tracks all grades received by all students
- Logging added for exam completion

---

## R6: Student Awards

### Task Description
Identify top three students for awards based on:
- Average exam grade
- Bonus: (number of exams taken / number of enrolled courses) × 10
- Final score = average + bonus

### Implementation Changes

**Method: `topThreeStudents()`**
- Calculates score for each student who has taken at least one exam
- Score formula: exam_average + (examCount / courseCount) × 10
- Uses selection sort approach to find top 3
- Returns formatted string with top three students

**Algorithm:**
1. Calculate scores for all students with exams
2. Mark students without exams as -1
3. Find maximum score, add to result, mark as -1
4. Repeat for second and third place
5. Return "FirstName LastName : SCORE" for each, separated by '\n'

**Key Changes:**
- Bonus rewards students who take more exams relative to their enrollment
- Handles case where fewer than 3 students have taken exams
- No ties are assumed

---

## R7: Logging System

### Task Description
Add comprehensive logging using `java.util.logging.Logger` for:
- Student enrollment
- Course activation
- Student course registration
- Exam completion

### Implementation Changes

**Logger Declaration:**
```java
public static final Logger logger = Logger.getLogger("University");
```

**Logging Messages Added:**

1. **In `enroll()` method:**
   - Message: "New student enrolled: 10000, Mario Rossi"
   - Format: "New student enrolled: ID, FirstName LastName"

2. **In `activate()` method:**
   - Message: "New course activated: 11, Object Oriented Programming James Gosling"
   - Format: "New course activated: CODE, TITLE TEACHER"

3. **In `register()` method:**
   - Message: "Student 10004 signed up for course 11"
   - Format: "Student ID signed up for course CODE"

4. **In `exam()` method:**
   - Message: "Student 10001 took an exam in course 12 with grade 27"
   - Format: "Student ID took an exam in course CODE with grade GRADE"

**Key Changes:**
- All major operations are now logged
- Uses `logger.info()` method
- Messages printed to console by default
- Helps track system activities and debugging

---

## Complete Data Model

### Student Class
- **Identification:** id, firstName, lastName
- **Enrollment:** courses[] (up to 25), courseCount
- **Exams:** examCourses[], examGrades[], examCount

### Course Class
- **Identification:** code, title, teacher
- **Enrollment:** students[] (up to 100), studentCount
- **Grading:** grades[], gradeCount

### University Class
- **Basic Info:** name, rectorFirst, rectorLast
- **Collections:** students[] (up to 1000), courses[] (up to 50)
- **Counters:** studentCount, courseCount

---

## Key Assumptions & Constraints

1. **Maximum Capacities:**
   - 1000 students per university
   - 50 courses per university
   - 100 attendees per course
   - 25 courses per student

2. **ID Assignment:**
   - Student IDs: start at 10000, increment by 1
   - Course codes: start at 10, increment by 1

3. **Exam Assumptions:**
   - Students only take exams for enrolled courses
   - Grades range from 0-30
   - No ties in top three students ranking

4. **Data Persistence:**
   - All data stored in memory (arrays)
   - No database or file persistence implemented

---

## Usage Example

```java
University polito = new University("Politecnico di Torino");
polito.setRector("Guido", "Saracco");

int student1 = polito.enroll("Mario", "Rossi");      // Returns 10000
int student2 = polito.enroll("Luigi", "Verdi");      // Returns 10001

int course1 = polito.activate("OOP", "James Gosling"); // Returns 10
int course2 = polito.activate("Algorithms", "Knuth");  // Returns 11

polito.register(student1, course1);
polito.register(student1, course2);
polito.register(student2, course1);

polito.exam(student1, course1, 28);
polito.exam(student1, course2, 30);

String avg = polito.studentAvg(student1); // "Student 10000 : 29.0"
String top = polito.topThreeStudents();    // Shows rankings
```

---

## Version Information
**Version:** 1.1  
**Last Updated:** 2024-03-16