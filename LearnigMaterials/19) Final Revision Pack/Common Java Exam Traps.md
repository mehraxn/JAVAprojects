# Common Java Exam Traps - Final Revision

## 1. `Reader` vs `BufferedReader`

`Reader` does not have `readLine()`.

Correct:

```java
BufferedReader r = new BufferedReader(new FileReader("data.txt"));
String line = r.readLine();
```

---

## 2. `c++` in `Map.compute`

Wrong:

```java
map.compute(word, (w, c) -> c == null ? 1L : c++);
```

Because `c++` returns old value.

Correct:

```java
map.compute(word, (w, c) -> c == null ? 1L : c + 1);
```

Or:

```java
map.merge(word, 1L, Long::sum);
```

---

## 3. Stream method reference vs method call

Wrong:

```java
NutritionalElement.getCalories()
```

Correct:

```java
NutritionalElement::getCalories
```

---

## 4. Streams are lazy

Intermediate operations do not run until terminal operation.

```java
list.stream()
    .filter(x -> {
        System.out.println(x);
        return true;
    });
```

No output because there is no terminal operation.

---

## 5. Streams are single-use

```java
Stream<String> s = list.stream();
s.count();
s.count(); // IllegalStateException
```

Create a new stream each time.

---

## 6. Interface fields

Interface fields are always:

```java
public static final
```

So this statement is false:

```text
An interface can have non-static attributes.
```

---

## 7. Interface can be empty

Marker interface:

```java
interface SerializableLike { }
```

So this statement is false:

```text
An interface must have at least one abstract method.
```

---

## 8. UML class middle block

```text
Top    = class name
Middle = attributes
Bottom = methods
```

---

## 9. JUnit failure vs error

```text
Failure = assertion failed
Error   = unexpected exception
```

`fail()` creates failure, not error.

---

## 10. `List<Dog>` is not `List<Animal>`

Even if `Dog extends Animal`, this is wrong:

```java
List<Animal> animals = new ArrayList<Dog>(); // compile error
```

Use wildcards:

```java
List<? extends Animal>
```

---

## 11. `HashSet` and `HashMap` need good `equals`/`hashCode`

If two objects are equal, they must have the same hash code.

---

## 12. `Comparable` vs `Comparator`

```text
Comparable = natural order inside class
Comparator = external/custom order
```

---

## 13. Overloading vs overriding

```text
Overloading = same name, different parameters, compile-time
Overriding  = subclass replaces method, runtime dispatch
```

---

## 14. `finally` usually runs

Even if there is a `return` in try/catch, `finally` usually executes.

---

## 15. JPA relationship quick check

```text
Student -> Thesis one-to-one = @OneToOne
Many students -> one course = @ManyToOne on Student
One course -> many students = @OneToMany on Course
```
