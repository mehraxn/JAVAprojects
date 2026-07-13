# Understanding EAGER vs LAZY Fetch Types in JPA/Hibernate

A complete guide to understanding fetch strategies in Java ORM frameworks.

## Table of Contents
- [What is Fetching?](#what-is-fetching)
- [FetchType.EAGER Explained](#fetchtype-eager-explained)
- [FetchType.LAZY Explained](#fetchtype-lazy-explained)
- [Side-by-Side Comparison](#side-by-side-comparison)
- [Database Query Examples](#database-query-examples)
- [The N+1 Problem](#the-n1-problem)
- [Performance Impact](#performance-impact)
- [Default Fetch Types](#default-fetch-types)
- [When to Use EAGER](#when-to-use-eager)
- [When to Use LAZY](#when-to-use-lazy)
- [Common Pitfalls](#common-pitfalls)
- [Solutions and Best Practices](#solutions-and-best-practices)

---

## What is Fetching?

**Fetching** is the process of loading related entities from the database when you retrieve an entity. 

When you have relationships between entities (like a Customer having multiple Orders), JPA needs to decide:
- Should it load the related entities **immediately** (EAGER)?
- Or should it wait until you **actually need them** (LAZY)?

This decision is controlled by the **FetchType** parameter in relationship annotations.

```java
@OneToMany(fetch = FetchType.EAGER)  // or FetchType.LAZY
```

---

## FetchType.EAGER Explained

### What is EAGER Fetching?

**EAGER** means "load everything immediately, right now!"

When you load an entity with EAGER relationships:
1. JPA loads the main entity
2. JPA **immediately** loads ALL related entities in the **same database operation** (or very few operations)
3. Everything is available right away, no additional queries needed

### Visual Representation

```
Loading Customer (ID = 1) with EAGER Orders:

┌─────────────────────────────────────────────┐
│  Database Query Execution                    │
├─────────────────────────────────────────────┤
│  1. SELECT * FROM customer WHERE id = 1      │
│  2. SELECT * FROM orders WHERE customer_id=1 │
│     ↓ (Executed IMMEDIATELY)                 │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  Customer Object (LOADED)                    │
│  ├─ id: 1                                    │
│  ├─ name: "John"                             │
│  └─ orders: [Order1, Order2, Order3]         │
│              ↑ (ALREADY LOADED)              │
└─────────────────────────────────────────────┘
```

### Code Example 1: Basic EAGER Loading

```java
@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // EAGER: Load orders immediately with customer
    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
    private List<Order> orders = new ArrayList<>();
}

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productName;
    private Double amount;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
```

### Using EAGER Entities

```java
// Load customer
Customer customer = entityManager.find(Customer.class, 1L);
// ⚡ At this point, orders are ALREADY loaded!

// Accessing orders - NO additional database query
System.out.println(customer.getName());           // John
System.out.println(customer.getOrders().size());  // 3 (no query!)

// You can safely use orders even outside transaction
for (Order order : customer.getOrders()) {
    System.out.println(order.getProductName());   // No query!
}
```

### Database Queries Generated (EAGER)

```sql
-- When you call: entityManager.find(Customer.class, 1L)

-- Query 1: Get the customer
SELECT c.id, c.name 
FROM customer c 
WHERE c.id = 1;

-- Query 2: Get all orders for this customer (AUTOMATICALLY!)
SELECT o.id, o.product_name, o.amount, o.customer_id
FROM orders o
WHERE o.customer_id = 1;

-- Total: 2 queries, but executed immediately
```

### Real-World Example: User Profile

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    // Profile is ALWAYS needed when showing user info
    // EAGER makes sense here!
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @Id
    private Long id;
    
    private String bio;
    private String avatarUrl;
    private String location;
}

// Usage
User user = userRepository.findById(1L);
// Profile is already loaded!
System.out.println(user.getProfile().getAvatarUrl()); // No extra query!
```

### Advantages of EAGER
✅ All data loaded at once
✅ No lazy initialization exceptions
✅ Can use data outside of transaction
✅ Predictable number of queries
✅ Simpler to understand and debug

### Disadvantages of EAGER
❌ May load unnecessary data
❌ Can cause performance issues with large datasets
❌ Memory intensive
❌ Slower initial load time
❌ May load data you never use

---

## FetchType.LAZY Explained

### What is LAZY Fetching?

**LAZY** means "don't load it until I actually need it!"

When you load an entity with LAZY relationships:
1. JPA loads ONLY the main entity
2. Related entities are **NOT loaded**
3. Instead, JPA creates a **proxy** (placeholder)
4. When you **access** the relationship, **THEN** JPA loads it (separate query)

### Visual Representation

```
Loading Customer (ID = 1) with LAZY Orders:

┌─────────────────────────────────────────────┐
│  Initial Database Query                      │
├─────────────────────────────────────────────┤
│  SELECT * FROM customer WHERE id = 1         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  Customer Object (LOADED)                    │
│  ├─ id: 1                                    │
│  ├─ name: "John"                             │
│  └─ orders: [PROXY - NOT YET LOADED]         │
│              ↑ (Just a placeholder)          │
└─────────────────────────────────────────────┘
                    ↓ (Later, when accessed)
┌─────────────────────────────────────────────┐
│  Access: customer.getOrders()                │
├─────────────────────────────────────────────┤
│  SELECT * FROM orders WHERE customer_id = 1  │
│     ↓ (Executed NOW)                         │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│  Customer Object                             │
│  ├─ id: 1                                    │
│  ├─ name: "John"                             │
│  └─ orders: [Order1, Order2, Order3]         │
│              ↑ (NOW LOADED)                  │
└─────────────────────────────────────────────┘
```

### Code Example 1: Basic LAZY Loading

```java
@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    // LAZY: Don't load posts until needed
    @OneToMany(mappedBy = "blog", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();
}

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;
}
```

### Using LAZY Entities

```java
// Load blog
Blog blog = entityManager.find(Blog.class, 1L);
// ⚠️ At this point, posts are NOT loaded yet!

// Accessing blog properties - NO extra query
System.out.println(blog.getTitle());  // "My Awesome Blog" (no query)

// Now accessing posts - THIS triggers a database query!
System.out.println(blog.getPosts().size());  // 🔄 Database query executed NOW!

// Accessing posts again - NO additional query (already loaded)
for (Post post : blog.getPosts()) {
    System.out.println(post.getContent());  // No query (already loaded)
}
```

### Database Queries Generated (LAZY)

```sql
-- When you call: entityManager.find(Blog.class, 1L)

-- Query 1: Get the blog ONLY
SELECT b.id, b.title 
FROM blog b 
WHERE b.id = 1;

-- Posts are NOT loaded yet!

-- Later, when you call: blog.getPosts().size()

-- Query 2: NOW get the posts
SELECT p.id, p.content, p.blog_id
FROM post p
WHERE p.blog_id = 1;

-- Total: 2 queries, but separated in time
```

### Real-World Example: Product with Reviews

```java
@Entity
public class Product {
    @Id
    private Long id;
    
    private String name;
    private BigDecimal price;
    
    // Reviews might not always be needed
    // LAZY is better here!
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
}

@Entity
public class Review {
    @Id
    private Long id;
    
    private String comment;
    private Integer rating;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}

// Usage - Listing products
List<Product> products = productRepository.findAll();
// Reviews are NOT loaded (good for performance!)

for (Product product : products) {
    // Only show basic info - no reviews loaded
    System.out.println(product.getName() + ": $" + product.getPrice());
}

// Usage - Product details page
Product product = productRepository.findById(1L);
System.out.println(product.getName());

// Now we need reviews
if (showReviews) {
    // This triggers the query to load reviews
    for (Review review : product.getReviews()) {
        System.out.println(review.getComment());
    }
}
```

### Advantages of LAZY
✅ Loads only what you need
✅ Better initial performance
✅ Less memory usage
✅ More efficient for large datasets
✅ Flexible - load when needed

### Disadvantages of LAZY
❌ Can cause N+1 query problem
❌ Requires active transaction to load
❌ Can throw LazyInitializationException
❌ Less predictable query count
❌ More complex to debug

---

## Side-by-Side Comparison

### Scenario: Loading 1 Customer

```java
@Entity
public class Customer {
    @Id
    private Long id;
    private String name;
    
    // Scenario A: EAGER
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;
    
    // Scenario B: LAZY
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
}
```

| Aspect | EAGER | LAZY |
|--------|-------|------|
| **When Loaded** | Immediately with parent | When accessed |
| **Initial Queries** | 2 (Customer + Orders) | 1 (Customer only) |
| **Memory Usage** | Higher (all data loaded) | Lower (load on demand) |
| **Access Speed** | Fast (already loaded) | First access: slow (query), Then: fast |
| **Outside Transaction** | ✅ Works | ❌ Fails (LazyInitializationException) |
| **Unused Data** | Still loaded (waste) | Not loaded (efficient) |
| **Code Complexity** | Simple | May need transaction management |

### Visual Timeline Comparison

**EAGER Loading:**
```
Time →
┌────────────────────────────────────────────────────────────┐
│ Load Customer                                               │
│ ┌─────────┐ ┌──────────┐                                   │
│ │Customer │→│ Orders   │                                   │
│ │Query    │ │Query     │                                   │
│ └─────────┘ └──────────┘                                   │
│ ↓                                                           │
│ Everything loaded                                           │
│                                                             │
│ Access orders: No query needed ✓                           │
└────────────────────────────────────────────────────────────┘
```

**LAZY Loading:**
```
Time →
┌────────────────────────────────────────────────────────────┐
│ Load Customer                                               │
│ ┌─────────┐                                                │
│ │Customer │                                                │
│ │Query    │                                                │
│ └─────────┘                                                │
│ ↓                                                           │
│ Customer loaded, orders = proxy                            │
│                                                             │
│ ... time passes ...                                         │
│                                                             │
│ Access orders:              ┌──────────┐                   │
│                             │ Orders   │                   │
│                             │Query     │                   │
│                             └──────────┘                   │
│                             ↓                               │
│                             Orders loaded ✓                │
└────────────────────────────────────────────────────────────┘
```

---

## Database Query Examples

### Example 1: Simple Find Operation

```java
@Entity
public class Author {
    @Id
    private Long id;
    private String name;
    
    @OneToMany(mappedBy = "author")
    private List<Book> books;
}

@Entity
public class Book {
    @Id
    private Long id;
    private String title;
    
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
```

**With EAGER:**
```java
// Code
Author author = entityManager.find(Author.class, 1L);
System.out.println(author.getName());
System.out.println(author.getBooks().size());

// SQL Generated
-- Query 1:
SELECT a.id, a.name FROM author a WHERE a.id = 1;

-- Query 2 (automatically):
SELECT b.id, b.title, b.author_id 
FROM book b 
WHERE b.author_id = 1;

-- Total: 2 queries at load time
```

**With LAZY:**
```java
// Code
Author author = entityManager.find(Author.class, 1L);
System.out.println(author.getName());
System.out.println(author.getBooks().size());

// SQL Generated
-- Query 1:
SELECT a.id, a.name FROM author a WHERE a.id = 1;

-- Query 2 (when getBooks() is called):
SELECT b.id, b.title, b.author_id 
FROM book b 
WHERE b.author_id = 1;

-- Total: 2 queries, but at different times
```

### Example 2: Loading Multiple Entities

```java
// Load 3 authors
List<Author> authors = entityManager.createQuery(
    "SELECT a FROM Author a WHERE a.id IN (1,2,3)", 
    Author.class
).getResultList();

// Access all books
for (Author author : authors) {
    System.out.println(author.getBooks().size());
}
```

**With EAGER:**
```sql
-- Query 1: Get authors
SELECT a.id, a.name FROM author a WHERE a.id IN (1,2,3);

-- Query 2: Get all books for these authors (in one query!)
SELECT b.id, b.title, b.author_id 
FROM book b 
WHERE b.author_id IN (1,2,3);

-- Total: 2 queries
```

**With LAZY (N+1 Problem!):**
```sql
-- Query 1: Get authors
SELECT a.id, a.name FROM author a WHERE a.id IN (1,2,3);

-- Query 2: Books for author 1
SELECT b.id, b.title, b.author_id FROM book b WHERE b.author_id = 1;

-- Query 3: Books for author 2
SELECT b.id, b.title, b.author_id FROM book b WHERE b.author_id = 2;

-- Query 4: Books for author 3
SELECT b.id, b.title, b.author_id FROM book b WHERE b.author_id = 3;

-- Total: 4 queries! (1 + N where N = 3 authors)
```

---

## The N+1 Problem

### What is the N+1 Problem?

The **N+1 problem** is a common performance issue with LAZY loading:
- 1 query to get N parent entities
- N additional queries (one for each parent) to get related entities
- Total: 1 + N queries instead of 1 or 2!

### Visual Representation

```
Loading 5 Customers with their Orders (LAZY):

Query 1: SELECT * FROM customer;  (Load 5 customers)
   ↓
Customer 1 → Query 2: SELECT * FROM orders WHERE customer_id = 1;
Customer 2 → Query 3: SELECT * FROM orders WHERE customer_id = 2;
Customer 3 → Query 4: SELECT * FROM orders WHERE customer_id = 3;
Customer 4 → Query 5: SELECT * FROM orders WHERE customer_id = 4;
Customer 5 → Query 6: SELECT * FROM orders WHERE customer_id = 5;

Total: 6 queries (1 + 5)
```

### Example: N+1 Problem in Action

```java
@Entity
public class Department {
    @Id
    private Long id;
    private String name;
    
    // LAZY fetch type
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;
}

// This code causes N+1 problem!
List<Department> departments = entityManager.createQuery(
    "SELECT d FROM Department d", Department.class
).getResultList();  // 1 query to get all departments

// Loop through departments
for (Department dept : departments) {
    System.out.println(dept.getName());
    
    // This causes ONE query per department!
    System.out.println("Employees: " + dept.getEmployees().size());
}

// If you have 100 departments: 101 queries! (1 + 100)
```

### Database Queries (N+1 Problem)

```sql
-- Query 1: Load departments
SELECT d.id, d.name FROM department d;
-- Returns: IT, HR, Sales, Finance (4 departments)

-- Query 2: Load employees for IT
SELECT e.id, e.name FROM employee e WHERE e.department_id = 1;

-- Query 3: Load employees for HR  
SELECT e.id, e.name FROM employee e WHERE e.department_id = 2;

-- Query 4: Load employees for Sales
SELECT e.id, e.name FROM employee e WHERE e.department_id = 3;

-- Query 5: Load employees for Finance
SELECT e.id, e.name FROM employee e WHERE e.department_id = 4;

-- Total: 5 queries (1 + 4 departments) = N+1 problem!
```

### Why N+1 is Bad

```
With 1 department:    2 queries   ✓ OK
With 10 departments:  11 queries  ⚠️ Slow
With 100 departments: 101 queries ❌ Very Slow
With 1000 departments: 1001 queries ❌❌❌ Disaster!
```

---

## Performance Impact

### Memory Usage Comparison

**EAGER - High Memory Usage:**
```java
// Load 1000 products with EAGER reviews
List<Product> products = productRepository.findAll();

Memory Used:
┌──────────────────────────────────┐
│ 1000 Products                     │
│ + 50,000 Reviews (50 per product)│
│ = ALL loaded in memory           │
│ = ~500 MB (hypothetical)         │
└──────────────────────────────────┘
```

**LAZY - Low Initial Memory:**
```java
// Load 1000 products with LAZY reviews
List<Product> products = productRepository.findAll();

Memory Used:
┌──────────────────────────────────┐
│ 1000 Products                     │
│ + 0 Reviews (not loaded yet)     │
│ = Only products in memory        │
│ = ~10 MB (hypothetical)          │
└──────────────────────────────────┘

// Load reviews only when needed
Product p = products.get(0);
List<Review> reviews = p.getReviews(); // Load only this product's reviews
```

### Query Performance Comparison

**Scenario: Display list of 100 blog posts with comment counts**

**EAGER Approach:**
```java
@OneToMany(fetch = FetchType.EAGER)
private List<Comment> comments;

// Load 100 blogs
List<Blog> blogs = blogRepository.findAll();

Queries Executed:
- 1 query for blogs
- 1 query for ALL comments (or 100 separate queries)
- Total time: 500ms

Data Loaded:
- 100 blogs
- 5,000 comments (even though we only need the count!)
- Wasted: 4,900 comment objects we don't use
```

**LAZY Approach:**
```java
@OneToMany(fetch = FetchType.LAZY)
private List<Comment> comments;

// Load 100 blogs
List<Blog> blogs = blogRepository.findAll();

for (Blog blog : blogs) {
    int count = blog.getComments().size(); // Triggers query
}

Queries Executed:
- 1 query for blogs
- 100 queries for comment counts (N+1!)
- Total time: 1500ms ❌ Worse!
```

**LAZY + JOIN FETCH (Best):**
```java
@OneToMany(fetch = FetchType.LAZY)
private List<Comment> comments;

// Use JOIN FETCH to load efficiently
List<Blog> blogs = entityManager.createQuery(
    "SELECT DISTINCT b FROM Blog b LEFT JOIN FETCH b.comments",
    Blog.class
).getResultList();

Queries Executed:
- 1 query (with JOIN)
- Total time: 200ms ✓ Best!

Data Loaded:
- 100 blogs
- All comments in one efficient query
```

### Real-World Performance Test

```java
// Test with 1000 customers, each with 10 orders

// EAGER Test
@OneToMany(fetch = FetchType.EAGER)
private List<Order> orders;

long start = System.currentTimeMillis();
List<Customer> customers = customerRepository.findAll();
long time = System.currentTimeMillis() - start;
// Time: 2000ms
// Memory: 100MB
// Queries: 2
// Problem: Loaded 10,000 orders we might not need!

// LAZY Test (with N+1)
@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders;

long start = System.currentTimeMillis();
List<Customer> customers = customerRepository.findAll();
for (Customer c : customers) {
    c.getOrders().size(); // Trigger load
}
long time = System.currentTimeMillis() - start;
// Time: 5000ms (slower!)
// Memory: 10MB initially
// Queries: 1001 (N+1 problem!)

// LAZY + JOIN FETCH (Best Practice)
@OneToMany(fetch = FetchType.LAZY)
private List<Order> orders;

long start = System.currentTimeMillis();
List<Customer> customers = entityManager.createQuery(
    "SELECT DISTINCT c FROM Customer c LEFT JOIN FETCH c.orders",
    Customer.class
).getResultList();
long time = System.currentTimeMillis() - start;
// Time: 800ms (fastest!)
// Memory: 50MB (reasonable)
// Queries: 1 (optimal!)
```

---

## Default Fetch Types

JPA has **default fetch types** for different relationship types:

| Relationship | Default FetchType |
|-------------|-------------------|
| `@OneToOne` | **EAGER** |
| `@ManyToOne` | **EAGER** |
| `@OneToMany` | **LAZY** |
| `@ManyToMany` | **LAZY** |

### Why These Defaults?

**EAGER for @OneToOne and @ManyToOne:**
- Usually small amount of data (1 related entity)
- Often needed immediately
- Low performance impact

```java
@Entity
public class Employee {
    @ManyToOne  // Default: EAGER
    private Department department;  // Just 1 department
}
```

**LAZY for @OneToMany and @ManyToMany:**
- Could be many related entities
- Might not always be needed
- Could impact performance significantly

```java
@Entity
public class Department {
    @OneToMany  // Default: LAZY
    private List<Employee> employees;  // Could be 100s of employees!
}
```

### Overriding Defaults

```java
// Override EAGER default to LAZY
@ManyToOne(fetch = FetchType.LAZY)
private Department department;

// Override LAZY default to EAGER
@OneToMany(fetch = FetchType.EAGER)
private List<Employee> employees;
```

---

## When to Use EAGER

### Use EAGER When:

1. **The relationship is almost always needed**
```java
@Entity
public class User {
    // User profile is always shown with user info
    @OneToOne(fetch = FetchType.EAGER)
    private UserProfile profile;
}
```

2. **The related data is small**
```java
@Entity
public class Product {
    // Just one category, always needed
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
}
```

3. **You want to avoid LazyInitializationException**
```java
@Entity
public class Order {
    @ManyToOne(fetch = FetchType.EAGER)
    private Customer customer;
    
    // Can safely access customer outside transaction
}
```

4. **The entity is rarely loaded**
```java
@Entity
public class SystemConfiguration {
    // Loaded once at startup, always need all settings
    @OneToMany(fetch = FetchType.EAGER)
    private List<ConfigurationItem> items;
}
```

### EAGER Use Cases

**Example 1: E-Commerce Product Display**
```java
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    
    // Category is ALWAYS shown on product pages
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
    
    // Main image is ALWAYS shown
    @OneToOne(fetch = FetchType.EAGER)
    private Image mainImage;
    
    // Reviews might not be needed (LAZY is better)
    @OneToMany(fetch = FetchType.LAZY)
    private List<Review> reviews;
}
```

**Example 2: User Authentication**
```java
@Entity
public class User {
    @Id
    private Long id;
    private String username;
    
    // Roles are needed for every request (authorization)
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
    
    public boolean hasRole(String roleName) {
        // No lazy initialization exception!
        return roles.stream()
            .anyMatch(r -> r.getName().equals(roleName));
    }
}
```

---

## When to Use LAZY

### Use LAZY When:

1. **The relationship might not be needed**
```java
@Entity
public class Blog {
    // Comments might not be viewed on list page
    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments;
}
```

2. **The related data is large**
```java
@Entity
public class Customer {
    // Customer might have 1000s of orders
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orderHistory;
}
```

3. **You want better initial performance**
```java
@Entity
public class Article {
    // Load article fast, content on demand
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String fullContent;  // Could be 100KB of text
}
```

4. **You're loading many entities at once**
```java
// Loading 1000 products
// LAZY prevents loading 50,000 reviews unnecessarily
@Entity
public class Product {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Review> reviews;
}
```

### LAZY Use Cases

**Example 1: Social Media Feed**
```java
@Entity
public class Post {
    @Id
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    
    // In feed view, only show post content
    // Comments loaded only when user clicks "Show Comments"
    @OneToMany(fetch = FetchType.LAZY)
    private List<Comment> comments;
    
    // Likes loaded only on detail view
    @OneToMany(fetch = FetchType.LAZY)
    private List<Like> likes;
}

// Usage - Feed page
List<Post> feed = postRepository.findRecent();
// Fast! No comments or likes loaded

// Usage - Post detail page
Post post = postRepository.findById(id);
List<Comment> comments = post.getComments(); // Load now
```

**Example 2: Employee Management**
```java
@Entity
public class Employee {
    @Id
    private Long id;
    private String name;
    private String email;
    
    // Salary history is sensitive, rarely needed
    @OneToMany(fetch = FetchType.LAZY)
    private List<SalaryRecord> salaryHistory;
    
    // Performance reviews loaded only when viewing profile
    @OneToMany(fetch = FetchType.LAZY)
    private List<PerformanceReview> reviews;
    
    // Time-off requests loaded only in HR view
    @OneToMany(fetch = FetchType.LAZY)
    private List<TimeOffRequest> timeOffRequests;
}
```

**Example 3: Document Management**
```java
@Entity
public class Document {
    @Id
    private Long id;
    private String title;
    private Long fileSize;
    
    // Large binary content loaded only when downloading
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;  // Could be 10MB PDF
    
    // Version history loaded only when needed
    @OneToMany(fetch = FetchType.LAZY)
    private List<DocumentVersion> versions;
}

// Usage - Document list
List<Document> docs = documentRepository.findAll();
// Fast! Shows titles without loading content

// Usage - Download document
Document doc = documentRepository.findById(id);
byte[] content = doc.getContent(); // Load now
```

---

## Common Pitfalls

### Pitfall 1: LazyInitializationException

**Problem:**
```java
@Entity
public class Order {
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items;
}

// Service method
@Transactional
public Order getOrder(Long id) {
    return orderRepository.findById(id);
}

// Controller
public void displayOrder() {
    Order order = orderService.getOrder(1L);
    // Transaction ended here!
    
    // ❌ LazyInitializationException!
    order.getItems().size();  // Outside transaction!
}
```

**Error Message:**
```
org.hibernate.LazyInitializationException: 
failed to lazily initialize a collection of role: Order.items, 
could not initialize proxy - no Session
```

**Solution 1: Load within transaction**
```java
@Transactional
public Order getOrderWithItems(Long id) {
    Order order = orderRepository.findById(id);
    order.getItems().size(); // Force initialization within transaction
    return order;
}
```

**Solution 2: Use JOIN FETCH**
```java
@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
Order findByIdWithItems(@Param("id") Long id);
```

**Solution 3: Use @EntityGraph**
```java
@EntityGraph(attributePaths = {"items"})
Order findById(Long id);
```

### Pitfall 2: Loading Too Much with EAGER

**Problem:**
```java
@Entity
public class Customer {
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;  // Customer has 1000 orders!
}

// Loading 100 customers
List<Customer> customers = customerRepository.findAll();
// Loads 100,000 orders! (100 customers × 1000 orders)
// Memory: 1GB+
// Time: 30 seconds
// Database: Overloaded!
```

**Solution: Use LAZY**
```java
@Entity
public class Customer {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
}

// Load customers fast
List<Customer> customers = customerRepository.findAll();

// Load orders only when needed
Customer customer = customers.get(0);
if (needOrders) {
    List<Order> orders = customer.getOrders();
}
```

### Pitfall 3: N+1 Query Problem

**Problem:**
```java
// LAZY loading with loop
List<Author> authors = authorRepository.findAll();  // 1 query

for (Author author : authors) {
    // Each iteration = 1 query!
    System.out.println(author.getBooks().size());
}
// Total: 1 + 100 = 101 queries for 100 authors!
```

**Solution: Use JOIN FETCH**
```java
@Query("SELECT DISTINCT a FROM Author a LEFT JOIN FETCH a.books")
List<Author> findAllWithBooks();

// Now just 1 query!
List<Author> authors = authorRepository.findAllWithBooks();
for (Author author : authors) {
    System.out.println(author.getBooks().size());  // No query!
}
```

### Pitfall 4: Cartesian Product with Multiple Eager Collections

**Problem:**
```java
@Entity
public class User {
    @OneToMany(fetch = FetchType.EAGER)
    private List<Order> orders;  // 10 orders
    
    @OneToMany(fetch = FetchType.EAGER)
    private List<Review> reviews;  // 10 reviews
}

// Query returns: 10 × 10 = 100 rows! (Cartesian product)
// Hibernate then has to deduplicate
```

**Solution: Use LAZY or load separately**
```java
@Entity
public class User {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;
    
    @OneToMany(fetch = FetchType.LAZY)
    private List<Review> reviews;
}

// Load what you need
@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
User findWithOrders(Long id);

@Query("SELECT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = :id")
User findWithReviews(Long id);
```

---

## Solutions and Best Practices

### Solution 1: Use DTO Projections

Instead of loading full entities, load only what you need:

```java
// DTO
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String categoryName;
    private Integer reviewCount;
    
    // Constructor, getters
}

// Repository
@Query("SELECT new com.example.ProductSummaryDTO(" +
       "p.id, p.name, c.name, SIZE(p.reviews)) " +
       "FROM Product p " +
       "JOIN p.category c")
List<ProductSummaryDTO> findProductSummaries();

// No LAZY loading issues!
// No N+1 problem!
// Only loads exactly what's needed!
```

### Solution 2: Use @EntityGraph

Define graphs of related entities to load:

```java
@Entity
@NamedEntityGraph(
    name = "Order.withItems",
    attributeNodes = @NamedAttributeNode("items")
)
@NamedEntityGraph(
    name = "Order.withItemsAndCustomer",
    attributeNodes = {
        @NamedAttributeNode("items"),
        @NamedAttributeNode("customer")
    }
)
public class Order {
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderItem> items;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;
}

// Use in repository
@EntityGraph("Order.withItems")
Order findOrderWithItems(Long id);

@EntityGraph("Order.withItemsAndCustomer")
Order findOrderWithItemsAndCustomer(Long id);
```

### Solution 3: Use @BatchSize

Reduce N+1 queries by batching:

```java
@Entity
public class Department {
    @OneToMany(mappedBy = "department")
    @BatchSize(size = 10)  // Load employees for 10 departments at once
    private List<Employee> employees;
}

// Load 100 departments
List<Department> depts = deptRepository.findAll();  // 1 query

// Access employees
for (Department dept : depts) {
    dept.getEmployees().size();
}
// Instead of 100 queries, only 10 queries! (100 ÷ 10)
```

### Solution 4: Use JOIN FETCH Queries

**Single entity:**
```java
@Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id")
Post findByIdWithComments(@Param("id") Long id);
```

**Multiple entities:**
```java
@Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.comments")
List<Post> findAllWithComments();
```

**Multiple levels:**
```java
@Query("SELECT DISTINCT o FROM Order o " +
       "LEFT JOIN FETCH o.items i " +
       "LEFT JOIN FETCH i.product")
List<Order> findAllWithItemsAndProducts();
```

### Solution 5: Use Specifications with Fetch

```java
public class OrderSpecs {
    public static Specification<Order> withItems() {
        return (root, query, cb) -> {
            root.fetch("items", JoinType.LEFT);
            return null;
        };
    }
    
    public static Specification<Order> withCustomer() {
        return (root, query, cb) -> {
            root.fetch("customer", JoinType.LEFT);
            return null;
        };
    }
}

// Usage
List<Order> orders = orderRepository.findAll(
    OrderSpecs.withItems().and(OrderSpecs.withCustomer())
);
```

### Solution 6: Open Session in View (Careful!)

```properties
# application.properties
spring.jpa.open-in-view=true
```

**⚠️ Warning:** This keeps the database connection open longer. Use with caution!

**Better alternative:** Use DTOs or load data properly in service layer.

---

## Quick Decision Guide

```
┌─────────────────────────────────────────────┐
│         Should I use EAGER or LAZY?          │
└─────────────────────────────────────────────┘
                    ↓
        ┌───────────┴───────────┐
        │                       │
    Is the relationship      
    ALWAYS needed?           
        │                       │
    ┌───┴───┐             ┌────┴────┐
    │  YES  │             │   NO    │
    └───┬───┘             └────┬────┘
        │                      │
Is the related data      Is the related data
small (1-10 records)?    large (100+ records)?
        │                      │
    ┌───┴───┐             ┌────┴────┐
    │  YES  │             │   YES   │
    └───┬───┘             └────┬────┘
        │                      │
   Use EAGER ✓           Use LAZY ✓
        │                      │
    ┌───┴────────────────────┬┘
    │   Consider using:       │
    ├─────────────────────────┤
    │ • LAZY + JOIN FETCH     │
    │ • @EntityGraph          │
    │ • DTO Projection        │
    └─────────────────────────┘
```

---

## Summary Table

| Feature | EAGER | LAZY |
|---------|-------|------|
| **Load Time** | Immediate (with parent) | On-demand (when accessed) |
| **Queries** | Predictable (few queries) | Variable (can cause N+1) |
| **Memory** | High (loads everything) | Low (loads on demand) |
| **Performance** | Slower initial load | Faster initial load |
| **Complexity** | Simple | More complex |
| **Use Outside Transaction** | ✅ Yes | ❌ No (exception) |
| **Best For** | Small, always-needed data | Large, sometimes-needed data |
| **Default For** | @OneToOne, @ManyToOne | @OneToMany, @ManyToMany |

## Final Recommendations

1. **Start with LAZY by default** (especially for collections)
2. **Use EAGER only when absolutely necessary**
3. **Prevent N+1 problems** with JOIN FETCH or @EntityGraph
4. **Use DTOs** for read-only operations
5. **Profile your queries** to find performance issues
6. **Keep transactions short** to avoid lazy loading issues
7. **Use @BatchSize** to optimize LAZY loading
8. **Test with production-like data volumes**

Remember: **LAZY is generally safer and more performant, but requires more careful management!**

---

Happy coding! 🚀