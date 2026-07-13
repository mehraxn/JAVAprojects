# Facade Pattern

## Learning goals

- Understand the facade pattern.
- Learn when a simple public entry point is useful.
- Avoid overusing facades as a hiding place for all logic.

## What is a facade?

A facade is a class that provides a simple public API over several internal classes.

Think of it as a front desk. The caller asks one clear object to perform a task. The facade coordinates the internal services.

## Example

```java
public final class EnrollmentFacade {
    private final StudentService students;
    private final CourseService courses;
    private final BillingService billing;

    public EnrollmentFacade(StudentService students, CourseService courses, BillingService billing) {
        this.students = students;
        this.courses = courses;
        this.billing = billing;
    }

    public EnrollmentReceipt enroll(String studentId, String courseId) {
        Student student = students.findActiveStudent(studentId);
        Course course = courses.findOpenCourse(courseId);
        billing.chargeEnrollmentFee(student, course);
        return courses.enroll(student, course);
    }
}
```

The caller does not need to know every internal step.

## When to use it

Use a facade when:

- callers need a small, stable API;
- one operation coordinates several services;
- you want to hide internal complexity;
- you are building a simple interface for a larger subsystem.

## When not to use it

Do not use a facade to hide poor design. If one facade becomes thousands of lines long, it is no longer simplifying the design. It is becoming a new "everything class".

## Common mistakes

- Putting all business rules into the facade.
- Making every method call go through a facade even when it adds no value.
- Returning mutable internal objects from facade methods.
- Giving the facade vague names like `Manager` with unclear responsibility.

## Mini exercise

Create a `LibraryFacade` with methods such as `borrowBook`, `returnBook`, and `registerMember`. List which smaller services it would coordinate.

## Quick summary

A facade gives callers one simple public entry point while keeping internal classes focused and replaceable.
