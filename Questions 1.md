# MCQ Set (10 Questions) — with Answers + Explanations

## Q1. Git conflicts

**Question:** Which strategy does git use to resolve conflicts?

* a. Lock-Unlock-Modify
* b. Check-out/Check-in
* c. Copy-Modify-Merge
* d. Lock-Modify-Unlock
* e. Check-out/Commit

**Answer:** c) **Copy-Modify-Merge**

**Why this is correct:**

* Git is a **distributed** version control system designed for many people to edit the same files **in parallel**.
* Instead of locking files, each developer **copies** the repository (clone), **modifies** files locally, then **merges** changes when integrating.
* Conflicts happen during the **merge** step if two changes overlap in the same region. Git then asks the user to resolve the conflict manually.
* The “lock” strategies (a, d) are typical of older centralized systems and are not Git’s default collaboration model.

---

## Q2. Java interfaces

**Question:** Which of the following sentences applies to Java interfaces?

* a. An interface must have at least one abstract method.
* b. An interface can be empty.
* c. An interface can have methods defined using the abstract keyword.
* d. An interface can have static methods.
* e. An interface can have non-static attributes.

**Answer:** b) **An interface can be empty** **and** d) **An interface can have static methods**.

**Why these are correct:**

* **b is true:** Java allows *marker interfaces* (interfaces with no methods), e.g. historically `Serializable`.
* **d is true:** Since **Java 8**, interfaces can declare **static methods** (and also `default` methods). Static methods belong to the interface itself, not to instances.

**Why the others are not selected:**

* **a is false:** An interface can have zero abstract methods (empty interface) and still be valid.
* **c is misleading / usually treated as false in MCQs:** Interface methods are *implicitly* `public abstract` (unless `default`, `static`, `private` etc.). You *can* write `abstract` explicitly, but it’s redundant. Many courses/tests mark this as “not the right statement” because it suggests something special.
* **e is false:** Fields in interfaces are **implicitly `public static final`** (constants). So “non-static attributes” are not allowed.

---

## Q3. UML class blocks

**Question:** In UML notation, what is reported in the middle block of a class?

* a. The implementation of the class.
* b. The name of the class
* c. Its methods.
* d. Its attributes.
* e. An interface

**Answer:** d) **Its attributes.**

**Why this is correct:**
In a standard UML class diagram, the class is drawn as a rectangle with up to **three compartments**:

1. **Top:** the **class name** (and sometimes stereotypes like `<<interface>>`).
2. **Middle:** the **attributes/fields** (data members), often with visibility (`+`, `-`, `#`) and types.
3. **Bottom:** the **operations/methods**.
   So the “middle block” is the attributes section.

---

## Q4. Streams groupingBy output

**Question:** What is the output of the following code?

```java
List<String> words = Arrays.asList("apple", "banana", "cherry", "avocado", "blueberry");

Map<Integer, List<String>> lengthToWords = words.stream()
    .collect(Collectors.groupingBy(String::length));

System.out.println(lengthToWords.get(6));
```

* a. [banana, cherry]
* b. [banana, cherry, avocado, blueberry]
* c. banana
* d. {6=[cherry, banana], 7=[avocado], 5=[apple], 9=[blueberry]}
* e. apple, avocado

**Answer:** a) **[banana, cherry]**

**Why this is correct:**

* `Collectors.groupingBy(String::length)` groups the strings by their **length**.
* Compute lengths:

  * `"apple"` → 5
  * `"banana"` → 6
  * `"cherry"` → 6
  * `"avocado"` → 7
  * `"blueberry"` → 9
* Therefore, the group for key `6` is `["banana", "cherry"]`.
* `lengthToWords.get(6)` returns the list in that bucket, and that is what gets printed.

---

## Q5. JUnit: error vs failure

**Question:** An error (with respect to a failure) in a JUnit test:

* a. may correspond to a runtime error which verified during the execution of the tested code
* b. may correspond to the method fail()
* c. may correspond to a wrong assertion
* d. will never correspond to a line with an assert statement in the test
* e. may correspond to a throw instruction in the tested code.

**Answer:** a) and e)

**Why these are correct:**
JUnit distinguishes **failures** from **errors**:

* A **failure** is when an **assertion fails** (e.g., `assertEquals(2, 1+1)` fails). That’s usually “the code ran, but the expected result was not met.”
* An **error** is when the test or code under test throws an **unexpected exception** or causes a runtime problem (e.g., `NullPointerException`, `ArrayIndexOutOfBoundsException`).

So:

* **a is correct**: a runtime error during execution of the tested code typically produces a JUnit **error**.
* **e is correct**: if the tested code executes a `throw` (throws an exception) that is not handled/expected by the test, that results in a JUnit **error**.

**Why the others are not selected:**

* **b (`fail()`)** forces the test to fail on purpose → that is a **failure**, not an error.
* **c wrong assertion** → failure.
* **d is false**: errors can happen anywhere; the statement is absolute and incorrect.

---

## Q6. Updating a word frequency map

**Question:** The following attribute maintains the word frequency count:

```java
private Map<String,Long> wordFrequency;
```

How is it possible to update the frequency given a new word `word`?

* a. `wordFrequency.computeIfAbsent(word, w -> 1)++`
* b. `wordFrequency.put(word, c++)`
* c. `wordFrequency.compute(word, (w,c) -> c++)`
* d. `wordFrequency.computeIfAbsent(word, w -> c++)`
* e. `wordFrequency.compute(word, (w,c) -> c==null ? 1 : c++)`

**Answer:** e) `wordFrequency.compute(word, (w,c) -> c==null ? 1 : c++)`

**Why this is correct (conceptually):**

* `Map.compute(key, remappingFunction)` recalculates the value for a key.
* Inside the lambda:

  * `c` is the **current** value associated with `word` (or `null` if absent).
  * If `c == null`, the word is new → set frequency to `1`.
  * Otherwise, increase the existing count.

**Important Java detail:**

* Using `c++` returns the **old** value (post-increment). So `c++` would typically store the old value back.
* A safer/correct increment expression for a `Long` is usually `c + 1`.

So the idea the option is testing is:

```java
wordFrequency.compute(word, (w, c) -> c == null ? 1L : c + 1);
```

That’s the standard, correct pattern.

**Why the others are wrong (main reasons):**

* **a, d:** `computeIfAbsent` returns a value, but you can’t increment the map entry like that; also types (`Long`) don’t support `++` safely in this context.
* **b:** `put(word, c++)` uses `c` that isn’t defined in that scope, and doesn’t handle “missing key”.
* **c:** doesn’t handle `null` (absent key) and also suffers from the `c++` post-increment issue.

---

## Q7. Reader type for `readLine()` loop

**Question:** The following code snippet counts the number of comment lines:

```java
String line;
long comments=0;
while((line=r.readLine()) != null){
    if(line.matches("^\s*//.*")){
        comments++;
    }
}
```

What type must `r` have to work correctly?

* a. It doesn't work because the regular expression is wrong
* b. `FileReader`
* c. `InputStreamReader`
* d. `BufferedReader`
* e. `Reader`

**Answer (as marked in your image):** e) `Reader`

**Why this was likely chosen in the quiz context:**

* `Reader` is the **general superclass** for character input.
* The code conceptually needs “a reader of characters from which we can read lines”. Many teaching materials treat `Reader` as the *general expected type* at the API level (e.g., a method might accept a `Reader` so it can work with many sources).

**But a key Java compilation detail to know:**

* In the **standard Java library**, `readLine()` is **not** a method of `Reader`.
* `readLine()` is provided by **`BufferedReader`**.

So, in normal Java:

* If the variable is declared as `Reader r`, the expression `r.readLine()` does **not compile**.
* The object that truly “makes this work” is a **`BufferedReader`**, typically created like:

  ```java
  BufferedReader r = new BufferedReader(new FileReader("file.txt"));
  ```

**Best interpretation that matches the marked answer:**

* The **runtime object** should be a `BufferedReader`, but the quiz might be focusing on “the general abstraction” (`Reader`) used at the API boundary.

---

## Q8. JPA relationship Student–Thesis

**Question:** How is it necessary to annotate the attribute to make persistent the relationship that binds a student to his thesis (after turning it in)?

```java
@Entity
class Student {
    Thesis thesis;
}
```

* a. `@OneToMany`
* b. `@ManyToMany`
* c. `@Optional`
* d. `@OneToOne`
* e. `@ManyToOne`

**Answer:** d) `@OneToOne`

**Why this is correct:**

* The domain statement is: **a student has (at most) one thesis** and a thesis belongs to **one student**.
* That is a classic **1–1** relationship.
* `@OneToOne` tells JPA/Hibernate to persist a single reference (often implemented with a unique foreign key or shared primary key, depending on mapping).

**Why the others are wrong (for this scenario):**

* `@OneToMany` would mean a student has many theses.
* `@ManyToOne` would mean many students share the same thesis.
* `@ManyToMany` would mean students and theses can be associated many-to-many.
* `@Optional` is not a JPA relationship annotation.

---

## Q9. Invoking a `max` helper with streams

**Question:** In the context of Lab 3:

```java
static double max(Stream<NutritionalElement> elements,
                  ToDoubleFunction<NutritionalElement> extractor) {
    return elements
        .mapToDouble(extractor)
        .max()
        .orElse(0.0);
}
```

How should the method be invoked to get the maximum calories in a list of nutrients?

* a. `max(elements.stream(), NutritionalElement::getCalories)`
* b. `max(elements, NutritionalElement::getCalories)`
* c. `max(elements.stream(), NutritionalElement::getCarbs)`
* d. `max(elements.stream(), NutritionalElement.getCarbs())`
* e. `max(elements.stream(), NutritionalElement.getCalories())`

**Answer:** a) `max(elements.stream(), NutritionalElement::getCalories)`

**Why this is correct:**

* The method expects:

  1. a **`Stream<NutritionalElement>`**
  2. a **`ToDoubleFunction<NutritionalElement>`** that extracts the double value to maximize.
* If you have a `List<NutritionalElement> elements`, then `elements.stream()` produces the required stream.
* `NutritionalElement::getCalories` is a **method reference** that matches `ToDoubleFunction<NutritionalElement>` (it takes a `NutritionalElement` and returns a `double`).

**Why others are wrong:**

* **b:** passes a list (or collection) instead of a `Stream`.
* **c:** extracts carbs, not calories.
* **d/e:** call the method immediately (`getCarbs()` / `getCalories()`), but you need a *function*, not the value for one element.

---

## Q10. Sorting stream by decreasing word length

**Question:** The following code:

```java
Stream.of("There", "must", "be", "some", "way", "out", "of", "here")
    .sorted(comparing(s -> s.length()).reversed())
    .toList();
```

* a. Returns the list sorted by decreasing word length
* b. Returns the list sorted by word length
* c. is incorrect because the compiler is unable to make the inference of type
* d. returns the list of word lengths
* e. is incorrect because the reversed method is not defined in the `Comparator` interfaces.

**Answer (as marked in your image):** c) is incorrect because the compiler is unable to make the inference of type

**Why this can be considered correct (for the snippet exactly as written):**

* As written, `comparing(...)` is **not qualified**.
* In real Java code, you usually write **`Comparator.comparing(...)`** (or you must have a correct static import: `import static java.util.Comparator.comparing;`).
* If `comparing` is not visible (no static import) or if the compiler cannot resolve which `comparing` you mean, compilation fails—often reported as a **type inference / cannot infer type arguments** kind of error (depending on context and IDE/compiler message).

**What the intended working version looks like:**

```java
Stream.of("There", "must", "be", "some", "way", "out", "of", "here")
    .sorted(java.util.Comparator.comparingInt(String::length).reversed())
    .toList();
```

(or)

```java
import static java.util.Comparator.comparingInt;

Stream.of(...)
    .sorted(comparingInt(String::length).reversed())
    .toList();
```

