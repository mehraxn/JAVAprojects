# Many To Many and Join Tables

## Learning goals

- Understand many-to-many relationships.
- Use join tables.
- Know when to replace many-to-many with an explicit entity.

## Example: Student and Course

```java
@Entity
public class Student {
    @ManyToMany
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new LinkedHashSet<>();
}
```

```java
@Entity
public class Course {
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new LinkedHashSet<>();
}
```

## Join table

The join table stores pairs of IDs. It connects students and courses without putting a foreign key directly on either main table.

## When many-to-many is not enough

If the relationship has extra data, create an entity.

Example: `Enrollment` can have:

- enrollment date;
- status;
- final grade.

## Common mistakes

- Using many-to-many when the relationship needs fields.
- Forgetting `mappedBy`.
- Cascading remove between shared entities.
- Returning mutable sets directly.

## Mini exercises

1. Model `Book` and `Author` as many-to-many.
2. Convert `Student` and `Course` into `Enrollment`.
3. Explain why cascade remove is risky.

## Quick summary

Many-to-many uses a join table. If the relationship has its own data, create an explicit relationship entity.
