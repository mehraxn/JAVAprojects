# JPA Relationships, Cascade, and Fetch Types - Complete Guide

## Overview
JPA relationships connect entity classes. The main annotations are `@OneToOne`, `@OneToMany`, `@ManyToOne`, and `@ManyToMany`.

---

## 1. One-to-one

Example: one student has one thesis.

```java
@Entity
class Student {
    @OneToOne
    private Thesis thesis;
}
```

This matches the exam idea: if a student is bound to one thesis and one thesis belongs to one student, use `@OneToOne`.

---

## 2. Many-to-one

Many students can belong to one course.

```java
@Entity
class Student {
    @ManyToOne
    private Course course;
}
```

Many `Student` objects reference one `Course`.

---

## 3. One-to-many

One course has many students.

```java
@Entity
class Course {
    @OneToMany(mappedBy = "course")
    private List<Student> students = new ArrayList<>();
}
```

`mappedBy = "course"` means the `Student.course` field owns the relationship.

---

## 4. Many-to-many

Many students can attend many courses.

```java
@Entity
class Student {
    @ManyToMany
    private List<Course> courses = new ArrayList<>();
}
```

Usually implemented with a join table.

---

## 5. Owning side

The owning side controls the foreign key/join table.

In a bidirectional relationship, one side owns and the other side uses `mappedBy`.

```java
class Student {
    @ManyToOne
    private Course course; // owning side
}

class Course {
    @OneToMany(mappedBy = "course")
    private List<Student> students;
}
```

---

## 6. Fetch types

Fetch type controls when related data is loaded.

| Fetch | Meaning |
|---|---|
| `EAGER` | load immediately |
| `LAZY` | load only when accessed |

Example:

```java
@OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
private List<Student> students;
```

LAZY is often preferred for collections to avoid loading too much data.

---

## 7. Cascade

Cascade means operations on one entity are propagated to related entities.

```java
@OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
private List<Student> students;
```

Common cascade types:

| Type | Meaning |
|---|---|
| `PERSIST` | save child when parent is saved |
| `MERGE` | merge child when parent is merged |
| `REMOVE` | delete child when parent is deleted |
| `ALL` | all cascade operations |

Be careful with `REMOVE`, especially in many-to-many relationships.

---

## 8. Orphan removal

```java
@OneToMany(mappedBy = "course", orphanRemoval = true)
private List<Student> students;
```

If a child is removed from the collection, it is deleted from the database.

---

## Common mistakes

### Mistake 1: using `@OneToMany` on the many side
If many students point to one course, student side is `@ManyToOne`.

### Mistake 2: forgetting `mappedBy`
Can create unnecessary join tables.

### Mistake 3: using eager fetching everywhere
Can load too much data and cause performance problems.

---

## Mini quiz

### Q1. Student has one thesis. Which annotation?
Answer: `@OneToOne`.

### Q2. Many students belong to one course. Annotation on Student?
Answer: `@ManyToOne`.

### Q3. What does LAZY fetch mean?
Answer: related data is loaded only when accessed.
