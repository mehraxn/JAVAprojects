# JDBC Basics, PreparedStatement, and Transactions - Complete Guide

## Overview
JDBC allows Java programs to connect to relational databases. It is lower-level than ORM/JPA/Hibernate.

---

## 1. What is JDBC?

JDBC means Java Database Connectivity.

It lets Java code:

- connect to a database
- execute SQL queries
- read results
- insert/update/delete data
- manage transactions

---

## 2. Main JDBC classes/interfaces

| Type | Meaning |
|---|---|
| `Connection` | connection to database |
| `Statement` | executes SQL |
| `PreparedStatement` | safer SQL with parameters |
| `ResultSet` | query results |
| `SQLException` | database error |

---

## 3. Connecting to a database

Example with SQLite-style URL:

```java
String url = "jdbc:sqlite:database.db";

try (Connection connection = DriverManager.getConnection(url)) {
    System.out.println("Connected");
} catch (SQLException e) {
    System.out.println("Database error: " + e.getMessage());
}
```

---

## 4. Querying data

```java
String sql = "SELECT id, name FROM students";

try (Connection connection = DriverManager.getConnection(url);
     Statement statement = connection.createStatement();
     ResultSet rs = statement.executeQuery(sql)) {

    while (rs.next()) {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        System.out.println(id + " " + name);
    }
}
```

---

## 5. PreparedStatement

Use `PreparedStatement` for SQL with parameters.

```java
String sql = "SELECT id, name FROM students WHERE city = ?";

try (Connection connection = DriverManager.getConnection(url);
     PreparedStatement ps = connection.prepareStatement(sql)) {

    ps.setString(1, "Turin");

    try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }
}
```

Why `PreparedStatement` is better:

- avoids SQL injection
- handles quoting/escaping
- clearer parameter binding

---

## 6. Inserting data

```java
String sql = "INSERT INTO students(name, city) VALUES (?, ?)";

try (Connection connection = DriverManager.getConnection(url);
     PreparedStatement ps = connection.prepareStatement(sql)) {

    ps.setString(1, "Sara");
    ps.setString(2, "Turin");
    int rows = ps.executeUpdate();

    System.out.println("Inserted rows: " + rows);
}
```

Use `executeUpdate` for INSERT, UPDATE, DELETE.

---

## 7. Transactions

A transaction groups multiple operations so they succeed or fail together.

```java
try (Connection connection = DriverManager.getConnection(url)) {
    connection.setAutoCommit(false);

    try {
        // operation 1
        // operation 2
        connection.commit();
    } catch (SQLException e) {
        connection.rollback();
        throw e;
    }
}
```

Example: money transfer should debit one account and credit another account together.

---

## 8. JDBC vs ORM

| JDBC | ORM/JPA/Hibernate |
|---|---|
| write SQL manually | map Java objects to tables |
| more control | less boilerplate |
| lower level | higher level |
| good for simple/direct queries | good for object domain models |

---

## Common mistakes

### Mistake 1: building SQL with string concatenation
Bad:

```java
"SELECT * FROM users WHERE name = '" + name + "'"
```

Use `PreparedStatement`.

### Mistake 2: forgetting to close resources
Use try-with-resources.

### Mistake 3: forgetting rollback
If transaction fails, rollback to keep database consistent.

---

## Mini quiz

### Q1. Which JDBC object stores query results?
Answer: `ResultSet`.

### Q2. Which is safer: Statement or PreparedStatement?
Answer: `PreparedStatement`.

### Q3. Which method is used for INSERT/UPDATE/DELETE?
Answer: `executeUpdate`.
