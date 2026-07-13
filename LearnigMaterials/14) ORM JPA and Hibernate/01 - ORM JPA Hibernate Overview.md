# ORM, JPA, and Hibernate Overview

## Learning goals

- Understand what ORM means.
- Understand the difference between JPA and Hibernate.
- Review core entity annotations.

## What ORM means

ORM means Object-Relational Mapping. It maps Java objects to relational database tables.

Example idea:

| Java | Database |
|---|---|
| `Student` class | `students` table |
| `studentId` field | `student_id` column |
| `Course` reference | foreign key relationship |

ORM lets Java code work mostly with objects while the ORM provider creates SQL behind the scenes.

## What JPA is

JPA is the Java Persistence API. It is a specification: a set of interfaces, annotations, and rules.

JPA defines concepts such as:

- `@Entity`
- `@Id`
- `EntityManager`
- JPQL
- entity relationships
- transactions

## What Hibernate is

Hibernate is a popular implementation of JPA. You write JPA-style code, and Hibernate performs the actual persistence work.

## Basic entity example

```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    protected Student() {
        // Required by JPA
    }

    public Student(String name) {
        this.name = name;
    }
}
```

## Common annotations

| Annotation | Purpose |
|---|---|
| `@Entity` | Marks a class as persistent |
| `@Table` | Configures the table name |
| `@Id` | Marks the primary key |
| `@GeneratedValue` | Configures generated IDs |
| `@Column` | Configures a table column |
| `@OneToMany` | One object relates to many objects |
| `@ManyToOne` | Many objects relate to one object |
| `@OneToOne` | One object relates to one object |
| `@ManyToMany` | Many objects relate to many objects |

## Common mistakes

- Forgetting the no-argument constructor.
- Making entity IDs mutable without a reason.
- Confusing table names with entity class names.
- Treating ORM as magic instead of understanding the generated database operations.

## Mini exercise

Create `Author` and `Book` entities. Decide which fields should be required and which table names you would use.

## Quick summary

JPA is the standard. Hibernate is an implementation. ORM maps Java entities to relational database tables.
