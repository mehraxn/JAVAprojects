# Mini MCQ Practice Set 2 — Answers and Explanations

## Q1. Which statement about Java strings is correct?

a. `==` always compares text content  
b. `.equals()` compares text content  
c. `String` is a primitive type  
d. Strings can be changed after creation

**Answer:** b

**Explanation:** `.equals()` compares content. `==` compares references. `String` is a reference type and is immutable.

---

## Q2. What is the output?

```java
System.out.println(10 / 4);
```

a. 2.5  
b. 2  
c. 3  
d. compile error

**Answer:** b

**Explanation:** Both operands are integers, so Java performs integer division.

---

## Q3. Which collection does not allow duplicates?

a. `List`  
b. `Set`  
c. `Map` values  
d. `ArrayList`

**Answer:** b

**Explanation:** `Set` rejects duplicates. `Map` keys are unique, but values can duplicate.

---

## Q4. Which method should be used to check if an Optional has a value without throwing?

a. `get()`  
b. `isPresent()`  
c. `hashCode()`  
d. `wait()`

**Answer:** b

**Explanation:** `isPresent()` checks presence. `get()` can throw `NoSuchElementException`.

---

## Q5. Which annotation marks a JUnit test method?

a. `@BeforeEach`  
b. `@Test`  
c. `@Entity`  
d. `@Override`

**Answer:** b

**Explanation:** `@Test` marks a test method.

---

## Q6. What does `? extends Animal` mean?

a. exactly `Animal` only  
b. unknown type that is `Animal` or subclass  
c. unknown type that is superclass of `Animal`  
d. any primitive type

**Answer:** b

**Explanation:** Upper bounded wildcard accepts `Animal` or subtype producers.

---

## Q7. Which statement about static methods is correct?

a. They belong to objects only  
b. They can directly access instance fields  
c. They belong to the class  
d. They are always abstract

**Answer:** c

**Explanation:** Static methods belong to the class, not individual objects.

---

## Q8. Which JDBC object should you prefer for SQL with user input?

a. `Statement`  
b. `PreparedStatement`  
c. `ResultSet`  
d. `Throwable`

**Answer:** b

**Explanation:** `PreparedStatement` safely binds parameters and helps prevent SQL injection.

---

## Q9. Which class has `readLine()`?

a. `Reader`  
b. `FileReader`  
c. `BufferedReader`  
d. `InputStream`

**Answer:** c

**Explanation:** `BufferedReader` provides `readLine()`.

---

## Q10. Which stream operation is terminal?

a. `filter`  
b. `map`  
c. `sorted`  
d. `collect`

**Answer:** d

**Explanation:** `collect` triggers the stream pipeline and produces a result.
