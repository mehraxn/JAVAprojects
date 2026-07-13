# ORM Relationships in Java - Complete Guide

A comprehensive guide to understanding and implementing relationship mappings in Java Persistence API (JPA) and Hibernate ORM.

## Table of Contents
- [Overview](#overview)
- [Relationship Types](#relationship-types)
- [One-to-One Relationships](#one-to-one-relationships)
- [Many-to-One Relationships](#many-to-one-relationships)
- [One-to-Many Relationships](#one-to-many-relationships)
- [Many-to-Many Relationships](#many-to-many-relationships)
- [Cascade Operations](#cascade-operations)
- [Fetch Strategies](#fetch-strategies)
- [Join Columns and Tables](#join-columns-and-tables)
- [Best Practices](#best-practices)

---

## Overview

Relationship mapping defines how entities are associated in a relational model. JPA manages these relationships using foreign keys and ensures data consistency through entity associations.

### Key Concepts

**Foreign Keys**: Automatically managed by JPA based on relationship annotations. Can be customized using `@JoinColumn` or `@JoinTable`.

**Owning vs. Inverse Side**:
- **Owning Side**: The entity that stores the foreign key
- **Inverse Side**: The entity that is referenced

**Bidirectional vs. Unidirectional**:
- **Unidirectional**: Only the owning side is aware of the relationship
- **Bidirectional**: Both sides are aware; the inverse side uses `mappedBy`

---

## Relationship Types

### 1. One-to-One (@OneToOne)
Each entity instance is associated with exactly one instance of the related entity.

### 2. Many-to-One (@ManyToOne)
Multiple instances of one entity are associated with a single instance of another entity.

### 3. One-to-Many (@OneToMany)
One entity instance is related to multiple instances of another entity.

### 4. Many-to-Many (@ManyToMany)
Entities are related in a many-to-many fashion, requiring a join table.

---

## One-to-One Relationships

### Annotation Parameters

```java
@OneToOne(
    cascade = CascadeType[],      // Optional
    fetch = FetchType,             // Optional, default: EAGER
    mappedBy = String,             // Used on inverse side
    orphanRemoval = boolean,       // Default: false
    optional = boolean             // Default: true
)
```

### Example 1: User and UserProfile (Unidirectional)

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile profile;
    
    // Getters and setters
}

@Entity
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String bio;
    private String website;
    
    // Getters and setters
}
```

### Example 2: Person and Passport (Bidirectional)

```java
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "passport_id", unique = true)
    private Passport passport;
    
    public void setPassport(Passport passport) {
        this.passport = passport;
        if (passport != null) {
            passport.setPerson(this);
        }
    }
}

@Entity
public class Passport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String passportNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    
    @OneToOne(mappedBy = "passport")
    private Person person;
    
    // Getters and setters
}
```

### Example 3: Employee and ParkingSpace (Optional Relationship)

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // optional = true means parking space is not mandatory
    @OneToOne(cascade = CascadeType.ALL, optional = true)
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace parkingSpace;
    
    // Getters and setters
}

@Entity
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String spaceNumber;
    private String level;
    
    @OneToOne(mappedBy = "parkingSpace")
    private Employee employee;
    
    // Getters and setters
}
```

### Example 4: Customer and ShippingAddress (With Orphan Removal)

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // orphanRemoval = true deletes the address if it's removed from customer
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shippingAddress;
    
    public void removeShippingAddress() {
        if (this.shippingAddress != null) {
            this.shippingAddress.setCustomer(null);
            this.shippingAddress = null;
        }
    }
}

@Entity
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String street;
    private String city;
    private String zipCode;
    
    @OneToOne(mappedBy = "shippingAddress")
    private Customer customer;
    
    // Getters and setters
}
```

### Example 5: Device and License (Non-Optional Relationship)

```java
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String deviceId;
    private String type;
    
    // optional = false enforces non-null foreign key
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;
    
    // Constructor ensures license is always present
    public Device(String deviceId, String type, License license) {
        this.deviceId = deviceId;
        this.type = type;
        this.license = license;
    }
}

@Entity
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String licenseKey;
    private LocalDate activationDate;
    
    @OneToOne(mappedBy = "license")
    private Device device;
    
    // Getters and setters
}
```

---

## Many-to-One Relationships

### Annotation Parameters

```java
@ManyToOne(
    cascade = CascadeType[],      // Optional
    fetch = FetchType,             // Optional, default: EAGER
    optional = boolean             // Default: true
)
```

### Example 1: Employee and Department (Basic)

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String position;
    
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Getters and setters
}

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String location;
    
    // No reference back to employees (unidirectional)
    
    // Getters and setters
}
```

### Example 2: Order and Customer (With Cascade)

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    // Getters and setters
}

@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    // Getters and setters
}
```

### Example 3: Comment and Post (Bidirectional with Lazy Loading)

```java
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    // Getters and setters
}

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
}
```

### Example 4: Book and Author (Multiple Books per Author)

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String isbn;
    private LocalDate publishDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    
    // Getters and setters
}

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String biography;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
    
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }
}
```

### Example 5: Product and Category (Optional Relationship)

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    // optional = true allows products without category
    @ManyToOne(optional = true)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Getters and setters
}

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
    
    // Getters and setters
}
```

### Example 6: Transaction and Account (Non-Optional)

```java
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String type; // DEBIT or CREDIT
    
    // optional = false enforces that every transaction must have an account
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    // Getters and setters
}

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String accountNumber;
    private BigDecimal balance;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setAccount(this);
    }
}
```

---

## One-to-Many Relationships

### Annotation Parameters

```java
@OneToMany(
    mappedBy = String,             // Required for bidirectional
    cascade = CascadeType[],       // Optional
    fetch = FetchType,             // Optional, default: LAZY
    orphanRemoval = boolean        // Default: false
)
```

### Example 1: University and Students (Basic Bidirectional)

```java
@Entity
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String location;
    
    @OneToMany(mappedBy = "university", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();
    
    public void addStudent(Student student) {
        students.add(student);
        student.setUniversity(this);
    }
    
    public void removeStudent(Student student) {
        students.remove(student);
        student.setUniversity(null);
    }
}

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String studentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id")
    private University university;
    
    // Getters and setters
}
```

### Example 2: ShoppingCart and CartItems (With Orphan Removal)

```java
@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime createdAt;
    
    // orphanRemoval = true removes items when they're removed from collection
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }
    
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }
    
    public void clearCart() {
        items.clear(); // All items will be deleted due to orphanRemoval
    }
}

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private ShoppingCart cart;
    
    // Getters and setters
}
```

### Example 3: Blog and BlogPosts (Unidirectional)

```java
@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    // Unidirectional: Blog knows about posts, but posts don't know about blog
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "blog_id") // Foreign key in BlogPost table
    private List<BlogPost> posts = new ArrayList<>();
    
    public void addPost(BlogPost post) {
        posts.add(post);
    }
}

@Entity
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    
    // No reference back to Blog
    
    // Getters and setters
}
```

### Example 4: Team and Players (With Cascade Types)

```java
@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String sport;
    
    @OneToMany(
        mappedBy = "team",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    private List<Player> players = new ArrayList<>();
    
    public void addPlayer(Player player) {
        players.add(player);
        player.setTeam(this);
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
        player.setTeam(null);
    }
}

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private Integer jerseyNumber;
    private String position;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    // Getters and setters
}
```

### Example 5: Invoice and InvoiceItems (With OrderBy)

```java
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("lineNumber ASC")
    private List<InvoiceItem> items = new ArrayList<>();
    
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        recalculateTotal();
    }
    
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
        recalculateTotal();
    }
    
    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(InvoiceItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

@Entity
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Integer lineNumber;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
```

### Example 6: Playlist and Songs (Using Set instead of List)

```java
@Entity
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Song> songs = new HashSet<>();
    
    public void addSong(Song song) {
        songs.add(song);
        song.setPlaylist(this);
    }
    
    public void removeSong(Song song) {
        songs.remove(song);
        song.setPlaylist(null);
    }
}

@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String artist;
    private Integer duration; // in seconds
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    private Playlist playlist;
    
    // Equals and hashCode based on id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return id != null && id.equals(song.getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

---

## Many-to-Many Relationships

### Annotation Parameters

```java
@ManyToMany(
    cascade = CascadeType[],       // Optional
    fetch = FetchType              // Optional, default: LAZY
)
```

### Example 1: Student and Course (Basic Bidirectional)

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    
    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
    
    public void enrollInCourse(Course course) {
        courses.add(course);
        course.getStudents().add(this);
    }
    
    public void dropCourse(Course course) {
        courses.remove(course);
        course.getStudents().remove(this);
    }
}

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String code;
    private Integer credits;
    
    @ManyToMany(mappedBy = "courses")
    private Set<Student> students = new HashSet<>();
    
    // Getters and setters
}
```

### Example 2: Book and Author (Multiple Authors per Book)

```java
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "book_author",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();
    
    public void addAuthor(Author author) {
        authors.add(author);
        author.getBooks().add(this);
    }
    
    public void removeAuthor(Author author) {
        authors.remove(author);
        author.getBooks().remove(this);
    }
}

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String nationality;
    
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books = new HashSet<>();
    
    // Getters and setters
}
```

### Example 3: Employee and Project (With Custom Join Table)

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private String department;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "project_assignments",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private Set<Project> projects = new HashSet<>();
    
    public void assignToProject(Project project) {
        projects.add(project);
        project.getEmployees().add(this);
    }
    
    public void removeFromProject(Project project) {
        projects.remove(project);
        project.getEmployees().remove(this);
    }
}

@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees = new HashSet<>();
    
    // Getters and setters
}
```

### Example 4: Movie and Actor (Unidirectional)

```java
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private Integer releaseYear;
    private String genre;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "movie_cast",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private Set<Actor> cast = new HashSet<>();
    
    public void addActorToCast(Actor actor) {
        cast.add(actor);
    }
    
    public void removeActorFromCast(Actor actor) {
        cast.remove(actor);
    }
}

@Entity
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private LocalDate birthDate;
    
    // No reference back to movies (unidirectional)
    
    // Getters and setters
}
```

### Example 5: Tag and Article (With Cascade All)

```java
@Entity
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "article_tags",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getArticles().add(this);
    }
    
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getArticles().remove(this);
    }
    
    public void removeAllTags() {
        tags.forEach(tag -> tag.getArticles().remove(this));
        tags.clear();
    }
}

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String color;
    
    @ManyToMany(mappedBy = "tags")
    private Set<Article> articles = new HashSet<>();
    
    // Getters and setters
}
```

### Example 6: User and Role (Security Example)

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    public void grantRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }
    
    public void revokeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }
    
    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }
}

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
    
    // Getters and setters
}
```

---

## Cascade Operations

Cascade operations define how persistence operations affect related entities.

### CascadeType Values

```java
// ALL - Applies all operations
cascade = CascadeType.ALL

// PERSIST - Saves child entities when parent is saved
cascade = CascadeType.PERSIST

// MERGE - Updates child entities when parent is updated
cascade = CascadeType.MERGE

// REMOVE - Deletes child entities when parent is deleted
cascade = CascadeType.REMOVE

// REFRESH - Reloads child entities when parent is refreshed
cascade = CascadeType.REFRESH

// DETACH - Detaches child entities when parent is detached
cascade = CascadeType.DETACH

// Multiple operations
cascade = {CascadeType.PERSIST, CascadeType.MERGE}
```

### Example 1: CascadeType.PERSIST

```java
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.PERSIST)
    private List<Office> offices = new ArrayList<>();
    
    public void addOffice(Office office) {
        offices.add(office);
        office.setCompany(this);
    }
}

// Usage: When you save the company, offices are also saved
Company company = new Company();
company.setName("Tech Corp");

Office office1 = new Office();
office1.setAddress("123 Main St");
company.addOffice(office1);

entityManager.persist(company); // Both company and office1 are saved
```

### Example 2: CascadeType.MERGE

```java
@Entity
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "library", cascade = CascadeType.MERGE)
    private List<Book> books = new ArrayList<>();
}

// Usage: When you merge the library, books are also merged
Library detachedLibrary = // ... loaded from somewhere
detachedLibrary.setName("Updated Name");
detachedLibrary.getBooks().get(0).setTitle("Updated Title");

Library mergedLibrary = entityManager.merge(detachedLibrary); 
// Both library and books are updated
```

### Example 3: CascadeType.REMOVE

```java
@Entity
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @OneToMany(mappedBy = "forum", cascade = CascadeType.REMOVE)
    private List<Thread> threads = new ArrayList<>();
}

// Usage: When you delete the forum, all threads are also deleted
Forum forum = entityManager.find(Forum.class, forumId);
entityManager.remove(forum); // Forum and all threads are deleted
```

### Example 4: CascadeType.ALL (Practical Example)

```java
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime orderDate;
    
    // ALL includes: PERSIST, MERGE, REMOVE, REFRESH, DETACH
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
    
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productName;
    private Integer quantity;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}

// Usage: All operations cascade
Order order = new Order();
order.setOrderDate(LocalDateTime.now());

OrderItem item1 = new OrderItem();
item1.setProductName("Laptop");
order.addItem(item1);

entityManager.persist(order); // PERSIST cascades to items
entityManager.remove(order);  // REMOVE cascades to items
```

### Example 5: Selective Cascade

```java
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Only PERSIST and MERGE cascade, not REMOVE
    @OneToMany(
        mappedBy = "author", 
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
    )
    private List<Book> books = new ArrayList<>();
}

// When author is deleted, books are NOT deleted (they might have other authors)
```

---

## Fetch Strategies

Fetch strategies determine how and when related entities are loaded from the database.

### FetchType Options

```java
// EAGER - Always loads the related entity immediately
fetch = FetchType.EAGER

// LAZY - Loads the related entity only when accessed
fetch = FetchType.LAZY
```

### Example 1: EAGER Fetching (OneToOne)

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    
    // Profile is loaded immediately with User
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

// When you load User, profile is automatically loaded
User user = entityManager.find(User.class, userId);
String bio = user.getProfile().getBio(); // No additional query
```

### Example 2: LAZY Fetching (OneToMany)

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Employees are loaded only when accessed
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
}

// When you load Department, employees are NOT loaded yet
Department dept = entityManager.find(Department.class, deptId);
// No query for employees yet

// Query is triggered when you access the collection
int count = dept.getEmployees().size(); // Now employees are loaded
```

### Example 3: LAZY with JOIN FETCH Query

```java
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}

// Even with LAZY, you can force loading with JOIN FETCH
String jpql = "SELECT p FROM Post p JOIN FETCH p.comments WHERE p.id = :id";
Post post = entityManager.createQuery(jpql, Post.class)
    .setParameter("id", postId)
    .getSingleResult();

// Comments are already loaded, no additional query needed
System.out.println(post.getComments().size());
```

### Example 4: Performance Comparison

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // EAGER: 1 query loads everything
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;
    
    // LAZY: Separate query when accessed (N+1 problem risk)
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}

// Loading 100 customers with EAGER address:
// 1 query for customers + addresses (JOIN)

// Loading 100 customers with LAZY orders:
// 1 query for customers
// If you access orders for each: 100 additional queries (N+1 problem!)

// Solution: Use JOIN FETCH
List<Customer> customers = entityManager.createQuery(
    "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.orders", 
    Customer.class
).getResultList();
```

### Example 5: Mixed Strategies

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Category is needed frequently - EAGER
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Reviews might not always be needed - LAZY
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
    // Inventory is critical info - EAGER
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;
}
```

### Example 6: Batch Fetching to Optimize LAZY

```java
@Entity
@BatchSize(size = 10) // Hibernate-specific
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    private List<Book> books = new ArrayList<>();
}

// When accessing books for one author, Hibernate will fetch books 
// for up to 10 authors at once, reducing N+1 problem
```

---

## Join Columns and Tables

### @JoinColumn Parameters

```java
@JoinColumn(
    name = "column_name",              // Foreign key column name
    referencedColumnName = "id",       // Referenced column in target table
    nullable = true,                   // Allow NULL values
    unique = false,                    // Enforce uniqueness
    insertable = true,                 // Allow inserts
    updatable = true                   // Allow updates
)
```

### Example 1: Custom Foreign Key Column

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(
        name = "dept_code",              // Custom column name
        referencedColumnName = "code"    // Reference dept code, not id
    )
    private Department department;
}

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String code;  // e.g., "IT", "HR", "FIN"
    
    private String name;
}
```

### Example 2: Non-Nullable Foreign Key

```java
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String text;
    
    @ManyToOne
    @JoinColumn(
        name = "user_id",
        nullable = false  // Every comment MUST have a user
    )
    private User user;
}
```

### Example 3: Unique Constraint

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @OneToOne
    @JoinColumn(
        name = "badge_id",
        unique = true  // Each badge belongs to only one employee
    )
    private Badge badge;
}
```

### Example 4: Read-Only Relationship

```java
@Entity
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String action;
    
    @ManyToOne
    @JoinColumn(
        name = "user_id",
        insertable = false,  // Cannot set user during insert
        updatable = false    // Cannot change user after insert
    )
    private User user;
    
    // User ID must be set separately via SQL or constructor
}
```

### @JoinTable for Many-to-Many

```java
@JoinTable(
    name = "join_table_name",
    joinColumns = @JoinColumn(name = "owner_id"),
    inverseJoinColumns = @JoinColumn(name = "target_id")
)
```

### Example 5: Custom Join Table

```java
@Entity
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "appointments",           // Custom table name
        joinColumns = @JoinColumn(
            name = "doctor_id",
            referencedColumnName = "id"
        ),
        inverseJoinColumns = @JoinColumn(
            name = "patient_id",
            referencedColumnName = "id"
        )
    )
    private Set<Patient> patients = new HashSet<>();
}

@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany(mappedBy = "patients")
    private Set<Doctor> doctors = new HashSet<>();
}
```

### Example 6: Complex Join Table with Additional Constraints

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToMany
    @JoinTable(
        name = "course_enrollments",
        joinColumns = @JoinColumn(
            name = "student_id",
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "course_id",
            nullable = false
        ),
        uniqueConstraints = @UniqueConstraint(
            columnNames = {"student_id", "course_id"}
        )
    )
    private Set<Course> courses = new HashSet<>();
}
```

---

## Best Practices

### 1. Always Initialize Collections

```java
@Entity
public class Team {
    @OneToMany(mappedBy = "team")
    private List<Player> players = new ArrayList<>();  // Initialize!
}
```

### 2. Use Bidirectional Utility Methods

```java
@Entity
public class Parent {
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();
    
    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);  // Keep both sides in sync
    }
    
    public void removeChild(Child child) {
        children.remove(child);
        child.setParent(null);  // Keep both sides in sync
    }
}
```

### 3. Implement equals() and hashCode() for Entities

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sku;  // Business key
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return sku != null && sku.equals(product.getSku());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

### 4. Choose Appropriate Fetch Strategies

```java
// Frequently accessed together - EAGER
@ManyToOne(fetch = FetchType.EAGER)
private Category category;

// Rarely needed - LAZY
@OneToMany(fetch = FetchType.LAZY)
private List<Comment> comments;
```

### 5. Be Careful with Cascade Operations

```java
// Good: Cascade for composition (parent owns children)
@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderItem> items;

// Bad: Cascade REMOVE for shared entities
@ManyToOne(cascade = CascadeType.REMOVE)  // Don't do this!
private Category category;  // Deleting product would delete category!
```

### 6. Use orphanRemoval for True Composition

```java
@Entity
public class Order {
    // Items belong exclusively to this order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    public void clearItems() {
        items.clear();  // All items are deleted from database
    }
}
```

### 7. Avoid Circular Dependencies in toString()

```java
@Entity
public class Author {
    @OneToMany(mappedBy = "author")
    private List<Book> books;
    
    @Override
    public String toString() {
        return "Author{id=" + id + ", name='" + name + "'}";
        // Don't include books.toString() to avoid infinite recursion
    }
}
```

### 8. Use DTOs for API Responses

```java
// Entity
@Entity
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

// DTO for API
public class UserDTO {
    private Long id;
    private String name;
    private int orderCount;  // Instead of full orders list
}
```

### 9. Transaction Management

```java
@Service
@Transactional
public class OrderService {
    
    @Autowired
    private EntityManager em;
    
    public void createOrder(Order order) {
        // All cascade operations happen within this transaction
        em.persist(order);
        // Items are also persisted due to cascade
    }
    
    @Transactional(readOnly = true)
    public Order findOrder(Long id) {
        // Read-only for better performance
        return em.find(Order.class, id);
    }
}
```

### 10. Use @EntityGraph to Avoid N+1

```java
@Entity
@NamedEntityGraph(
    name = "User.orders",
    attributeNodes = @NamedAttributeNode("orders")
)
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

// In repository
@EntityGraph(value = "User.orders", type = EntityGraph.EntityGraphType.LOAD)
List<User> findAll();
```

---

## Summary

This guide covered all major relationship types in JPA/Hibernate:

- **One-to-One**: For 1:1 associations like User-Profile
- **Many-to-One**: For N:1 associations like Employee-Department  
- **One-to-Many**: For 1:N associations like Order-OrderItems
- **Many-to-Many**: For N:M associations like Student-Course

Key concepts to remember:
- Use `mappedBy` on the inverse side of bidirectional relationships
- Choose appropriate cascade types based on ownership
- Select EAGER vs LAZY fetch based on access patterns
- Initialize collections to avoid NullPointerExceptions
- Keep both sides of bidirectional relationships in sync
- Be mindful of N+1 query problems with LAZY loading

Happy coding! 🚀