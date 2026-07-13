# Repository Pattern Introduction

## Learning goals

- Understand why data access should be separated from business logic.
- Learn basic CRUD repository methods.
- Use repository interfaces for easier testing.

## What is a repository?

A repository is a class or interface that represents a collection of domain objects. It hides where the data comes from.

CRUD means:

- Create
- Read
- Update
- Delete

## Repository interface

```java
public interface BookRepository {
    void save(Book book);
    Optional<Book> findById(String bookId);
    List<Book> findAll();
    void deleteById(String bookId);
}
```

The service layer depends on this interface, not on file, JDBC, or JPA details.

## In-memory repository for tests

```java
public final class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new LinkedHashMap<>();

    @Override
    public void save(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(books.values());
    }

    @Override
    public void deleteById(String bookId) {
        books.remove(bookId);
    }
}
```

This makes service tests fast because they do not need a database.

## Service using a repository

```java
public final class BookService {
    private final BookRepository books;

    public BookService(BookRepository books) {
        this.books = books;
    }

    public BookSnapshot renameBook(String bookId, String title) {
        Book book = books.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.rename(title);
        books.save(book);
        return BookSnapshot.from(book);
    }
}
```

## Common mistakes

- Querying storage directly from UI code.
- Putting business rules inside repository classes.
- Returning mutable internal lists.
- Making repository methods print messages.

## Mini exercise

Design a `ProductRepository` interface with `save`, `findById`, `findAll`, and `deleteById`. Then write an in-memory implementation.

## Quick summary

Repositories keep business workflows separate from data access details.
