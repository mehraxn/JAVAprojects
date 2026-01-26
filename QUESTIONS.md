# 📘 OOP / Java / Tools – Quiz Questions & Correct Answers (README)

This file lists the questions (as provided) and the **correct answers according to the quiz correction**, plus short notes where the quiz answer is **technically questionable in real Java**.

---

## Question 1

**Which strategy does Git use to resolve conflicts?**

a. Lock–Unlock–Modify
b. Check-out / Check-in
c. Copy–Modify–Merge
d. Lock–Modify–Unlock
e. Check-out / Commit

✅ **Correct answer (quiz):** **c** — Copy–Modify–Merge

---

## Question 2

**Which of the following sentences applies to Java interfaces?**

a. An interface must have at least one abstract method.
b. An interface can be empty.
c. An interface can have methods defined using the `abstract` keyword.
d. An interface can have static methods.
e. An interface can have non-static attributes.

✅ **Correct answer (quiz):** **b, c, d**

---

## Question 3

**In UML notation, what is reported in the middle block of a class?**

a. The implementation of the class
b. The name of the class
c. Its methods
d. Its attributes
e. An interface

✅ **Correct answer (quiz):** **d** — Its attributes

---

## Question 4

**What is the output of the following code?**

```java
List<String> words = Arrays.asList(
    "apple", "banana", "cherry", "avocado", "blueberry"
);

Map<Integer, List<String>> lengthToWords = words.stream()
    .collect(Collectors.groupingBy(String::length));

System.out.println(lengthToWords.get(6));
```

a. `[banana, cherry]`
b. `[banana, cherry, avocado, blueberry]`
c. `banana`
d. `{6=[cherry, banana], 7=[avocado], 5=[apple], 9=[blueberry]}`
e. `[apple, avocado]`

✅ **Correct answer (quiz):** **a** — `[banana, cherry]`

---

## Question 5

**An error (with respect to a failure) in a JUnit test:**

a. may correspond to a runtime error which is verified during the execution of the tested code
b. may correspond to the method `fail()`
c. may correspond to a wrong assertion
d. will never correspond to a line with an assert statement in the test
e. may correspond to a `throw` instruction in the tested code

✅ **Correct answer (quiz):** **a, e**

---

## Question 6

The following attribute maintains the word frequency count:

```java
private Map<String, Long> wordFrequency;
```

**How is it possible to update the frequency given a new word `word`?**

a. `wordFrequency.computeIfAbsent(word, w -> 1)++`
b. `wordFrequency.put(word, c++)`
c. `wordFrequency.compute(word, (w,c) -> c++)`
d. `wordFrequency.computeIfAbsent(word, w -> c++)`
e. `wordFrequency.compute(word, (w,c) -> c==null ? 1 : c++)`

✅ **Correct answer (quiz):** **e**

⚠️ **Note (real Java):** using `c++` returns the **old** value, so the stored value would not increment as intended. A practically correct version is:

```java
wordFrequency.compute(word, (w, c) -> c == null ? 1L : c + 1L);
// or: wordFrequency.merge(word, 1L, Long::sum);
```

---

## Question 7

The following code snippet counts the number of comment lines:

```java
String line;
long comments = 0;

while ((line = r.readLine()) != null) {
    if (line.matches("^\\s*//.*")) {
        comments++;
    }
}
```

**What type must `r` have to work correctly?**

a. It doesn’t work because the regular expression is wrong
b. `FileReader`
c. `InputStreamReader`
d. `BufferedReader`
e. `Reader`

✅ **Correct answer (quiz):** **d** — `BufferedReader`

---

## Question 8

**How is it necessary to annotate the attribute to make persistent the relationship that binds a student to his thesis (after turning it in)?**

```java
@Entity
class Student {
    Thesis thesis;
}
```

a. `@OneToMany`
b. `@ManyToMany`
c. `@Optional`
d. `@OneToOne`
e. `@ManyToOne`

✅ **Correct answer (quiz):** **d** — `@OneToOne`

---

## Question 9

In the context of Lab 3, given:

```java
static double max(
    Stream<NutritionalElement> elements,
    ToDoubleFunction<NutritionalElement> extractor
) {
    return elements
        .mapToDouble(extractor)
        .max()
        .orElse(0.0);
}
```

**How should the method be invoked to get the maximum calories in a list of nutrients?**

a. `max(elements.stream(), NutritionalElement::getCalories)`
b. `max(elements, NutritionalElement::getCalories)`
c. `max(elements.stream(), NutritionalElement::getCarbs)`
d. `max(elements.stream(), NutritionalElement.getCarbs())`
e. `max(elements.stream(), NutritionalElement.getCalories())`

✅ **Correct answer (quiz):** **a**

---

## Question 10

Consider the following code:

```java
Stream.of("There", "must", "be", "some", "way", "out", "of", "here")
    .sorted(comparing(s -> s.length()).reversed())
    .toList();
```

**What does this code do?**

a. Returns the list sorted by decreasing word length
b. Returns the list sorted by word length
c. is incorrect because the compiler is unable to make the inference of type
d. returns the list of word lengths
e. is incorrect because the `reversed` method is not defined in the `Comparator` interface

✅ **Correct answer (quiz):** **c**

✅ A robust, compiling version is:

```java
Stream.of("There", "must", "be", "some", "way", "out", "of", "here")
    .sorted(java.util.Comparator.comparingInt(String::length).reversed())
    .toList();
```

---

### Notes

* If your platform marks something as “correct” that looks odd in real Java (like `c++` inside `compute`), keep the **quiz answer** for the exam, but learn the **compiling/correct** version too.
