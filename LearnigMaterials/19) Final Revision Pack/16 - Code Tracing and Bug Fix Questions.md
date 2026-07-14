# Code Tracing and Bug Fix Questions

## 1. Exposed mutable list bug

### Buggy code

```java
public List<String> items() {
    return items;
}
```

### What is wrong?

Callers can modify internal state without validation.

### Corrected version

```java
public List<String> items() {
    return List.copyOf(items);
}
```

### Explanation

Return a defensive, unmodifiable copy at public boundaries.

## 2. Missing transaction bug

### Buggy code

```java
em.persist(product);
```

### What is wrong?

Persisting data requires a transaction in normal JPA usage.

### Corrected version

```java
EntityTransaction tx = em.getTransaction();
tx.begin();
em.persist(product);
tx.commit();
```

### Explanation

Wrap writes in transaction boundaries and roll back on failure.

## 3. JPQL table-name bug

### Buggy code

```java
em.createQuery("SELECT * FROM products", Product.class);
```

### What is wrong?

JPQL uses entity names and fields, not SQL table syntax.

### Corrected version

```java
em.createQuery("SELECT p FROM Product p", Product.class);
```

### Explanation

Use entity class names and aliases in JPQL.

## 4. Lazy loading boundary bug

### Buggy code

```java
Order order = repository.findById(id);
em.close();
return order.getItems().size();
```

### What is wrong?

Lazy items may be accessed after the persistence context is closed.

### Corrected version

```java
Order order = repository.findByIdWithItems(id);
return OrderSnapshot.from(order);
```

### Explanation

Fetch required data before leaving the persistence boundary and return a snapshot.

## 5. Bad CSV split bug

### Buggy code

```java
String[] parts = line.split(",");
```

### What is wrong?

Quoted values can contain commas.

### Corrected approach

```java
// For learning: parse while tracking quotes.
// For real applications: use a CSV library.
```

### Explanation

Simple split only works for simple CSV.

## 6. N+1 query bug

### Buggy code

```java
List<Order> orders = em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
for (Order order : orders) {
    order.getItems().size();
}
```

### What is wrong?

Each lazy collection access may run another query.

### Corrected version

```java
SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items
```

### Explanation

Fetch required relationships intentionally for the use case.

## 7. Transfer not all-or-nothing bug

### Buggy code

```java
source.withdraw(amount);
accounts.save(source);
target.deposit(amount);
accounts.save(target);
```

### What is wrong?

If deposit or second save fails, only the source may be changed.

### Corrected approach

```java
validate source;
validate target;
validate amount;
validate sufficient balance;
begin transaction;
withdraw;
deposit;
save both;
commit;
rollback on failure;
```

### Explanation

Complete workflows need one all-or-nothing boundary.

## 8. Missing authorization check bug

### Buggy code

```java
public void approve(String requestId, UserContext user) {
    Request request = requests.findRequired(requestId);
    request.approve(user.userId());
}
```

### What is wrong?

Any user can approve.

### Corrected version

```java
public void approve(String requestId, UserContext user) {
    authorization.requireAnyRole(user, Role.ADMIN, Role.MAINTAINER);
    Request request = requests.findRequired(requestId);
    request.approve(user.userId());
}
```

### Explanation

Protected operations need service-layer authorization checks.
