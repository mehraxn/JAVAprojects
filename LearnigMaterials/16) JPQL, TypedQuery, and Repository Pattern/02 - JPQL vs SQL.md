# JPQL vs SQL

## Learning goals

- Compare JPQL and SQL.
- Understand entity names vs table names.
- Understand field names vs column names.

## Main difference

| Concept | SQL | JPQL |
|---|---|---|
| Query target | tables | entity classes |
| Field names | columns | Java fields/properties |
| Result | rows | entities or selected values |

## Example

SQL:

```sql
SELECT * FROM students WHERE student_name = ?
```

JPQL:

```java
SELECT s FROM Student s WHERE s.name = :name
```

`Student` is the entity class name. `name` is the Java field/property name.

## ORDER BY

```java
SELECT p FROM Product p ORDER BY p.name ASC
```

## JOIN basics

```java
SELECT o FROM Order o JOIN o.items i WHERE i.productName = :name
```

The join follows entity relationships.

## Common mistakes

- Using column names instead of Java field names.
- Writing `SELECT *`.
- Forgetting aliases.
- Assuming JPQL supports every database-specific SQL feature.

## Mini exercise

Convert this SQL idea to JPQL: select all products where category is equal to a parameter, ordered by name.

## Quick summary

JPQL looks similar to SQL, but it works through the entity model.
