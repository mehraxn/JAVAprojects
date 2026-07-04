# Java Code Tracing Practice ("Predict the Output")

Read each snippet, write down what you think it prints, then check the answer.
All outputs were verified by careful static review. Assume the needed `import`s and a
`main` method are present.

---

## Numbers and operators

**Q1.**
```java
System.out.println(10 / 4);
System.out.println(10 % 4);
System.out.println(10.0 / 4);
```
**Answer:** `2`, `2`, `2.5`.
**Why:** `int / int` truncates toward zero; `%` is the remainder; one `double` operand makes it real division.

---

**Q2.**
```java
int i = 5;
System.out.println(i++);
System.out.println(i);
System.out.println(++i);
```
**Answer:** `5`, `6`, `7`.
**Why:** `i++` returns the old value then increments; `++i` increments then returns the new value.

---

**Q3.**
```java
System.out.println(1 + 2 + "3");
System.out.println("1" + 2 + 3);
```
**Answer:** `33`, `123`.
**Why:** `+` is left-to-right. `1+2=3` then `+"3"` → `"33"`. `"1"+2` → `"12"` then `+3` → `"123"`.

---

**Q4.**
```java
System.out.println(0.1 + 0.2);
```
**Answer:** `0.30000000000000004`.
**Why:** `double` uses binary floating point, which cannot represent 0.1/0.2 exactly.

---

**Q5.**
```java
char c = 'A';
System.out.println(c + 1);
System.out.println((char) (c + 1));
System.out.println('5' - '0');
```
**Answer:** `66`, `B`, `5`.
**Why:** `char` promotes to `int` in arithmetic (`'A'` = 65). Casting back to `char` gives `'B'`. `'5'`-`'0'` = 53-48 = 5.

---

## Strings

**Q6.**
```java
String x = "hi";
String y = "hi";
String z = new String("hi");
System.out.println(x == y);
System.out.println(x == z);
System.out.println(x.equals(z));
```
**Answer:** `true`, `false`, `true`.
**Why:** String literals share one pooled object (`x == y`). `new String` makes a separate object (`x != z`). `equals` compares text.

---

**Q7.**
```java
String s = "hello";
s.toUpperCase();
System.out.println(s);
```
**Answer:** `hello`.
**Why:** Strings are **immutable**; `toUpperCase()` returns a new string that is ignored here.

---

## Wrappers / autoboxing

**Q8.**
```java
Integer a = 127, b = 127;
Integer c = 128, d = 128;
System.out.println(a == b);
System.out.println(c == d);
System.out.println(c.equals(d));
```
**Answer:** `true`, `false`, `true`.
**Why:** Java caches `Integer` values from -128 to 127, so `a` and `b` are the same object. 128 is outside the cache, so `c` and `d` are different objects; use `equals` for value comparison.

---

## Inheritance and polymorphism

**Q9.**
```java
class Animal { String sound() { return "..."; } }
class Dog extends Animal { String sound() { return "Woof"; } }

Animal a = new Dog();
System.out.println(a.sound());
```
**Answer:** `Woof`.
**Why:** Instance methods use **dynamic dispatch** — the real object (`Dog`) decides.

---

**Q10.** (Fields are NOT polymorphic)
```java
class A { int x = 1; }
class B extends A { int x = 2; }

A ref = new B();
System.out.println(ref.x);
```
**Answer:** `1`.
**Why:** Fields are resolved by the **declared type** (`A`), not the runtime object.

---

**Q11.** (Static methods are hidden, not overridden)
```java
class Parent { static String who() { return "Parent"; } }
class Child extends Parent { static String who() { return "Child"; } }

Parent p = new Child();
System.out.println(p.who());
```
**Answer:** `Parent`.
**Why:** `static` methods are not polymorphic; the call is resolved by the reference type.

---

**Q12.** (Overloading is chosen at compile time)
```java
static void print(Object o) { System.out.println("Object"); }
static void print(String s) { System.out.println("String"); }

Object obj = "hello";
print(obj);
```
**Answer:** `Object`.
**Why:** Overload resolution uses the **declared type** (`Object`), decided at compile time — even though the value is a `String`.

---

## equals / hashCode

**Q13.**
```java
class Point { int x; Point(int x) { this.x = x; } }

Point p1 = new Point(1);
Point p2 = new Point(1);
System.out.println(p1.equals(p2));
```
**Answer:** `false`.
**Why:** `Point` does not override `equals`, so the default reference comparison is used.

---

## Exceptions

**Q14.**
```java
static int test() {
    try {
        return 1;
    } finally {
        System.out.println("finally runs");
    }
}
// called from main: System.out.println(test());
```
**Answer:** prints `finally runs` then `1`.
**Why:** `finally` runs before the method actually returns.

---

**Q15.**
```java
static int weird() {
    try {
        return 1;
    } finally {
        return 2;
    }
}
```
**Answer:** returns `2`.
**Why:** A `return` inside `finally` overrides the `return` in `try` (a known trap — avoid returning from `finally`).

---

**Q16.**
```java
int[] a = null;
if (a != null && a.length > 0) {
    System.out.println("has items");
} else {
    System.out.println("empty or null");
}
```
**Answer:** `empty or null`.
**Why:** `&&` short-circuits: `a != null` is false, so `a.length` is never evaluated (no `NullPointerException`).

---

## Arrays

**Q17.**
```java
int[] a = new int[3];
boolean[] b = new boolean[2];
String[] s = new String[2];
System.out.println(Arrays.toString(a));
System.out.println(Arrays.toString(b));
System.out.println(Arrays.toString(s));
```
**Answer:** `[0, 0, 0]`, `[false, false]`, `[null, null]`.
**Why:** New arrays get default values: `0` for numeric, `false` for boolean, `null` for objects.

---

**Q18.**
```java
int x = 2;
switch (x) {
    case 1: System.out.println("one");
    case 2: System.out.println("two");
    case 3: System.out.println("three"); break;
    default: System.out.println("other");
}
```
**Answer:** `two` then `three`.
**Why:** Classic `switch` **falls through** until a `break`. Matching `case 2` runs "two" and "three", then breaks.

---

## Collections

**Q19.**
```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
for (int n : list) {
    if (n == 2) list.remove(Integer.valueOf(2));
}
```
**Answer:** Throws `ConcurrentModificationException`.
**Why:** You cannot structurally modify a list while iterating it with a for-each loop. Use an `Iterator` and `it.remove()`, or `list.removeIf(n -> n == 2)`.

---

**Q20.**
```java
Map<String, Integer> counts = new HashMap<>();
counts.put("a", 1);
counts.merge("a", 1, Integer::sum);
counts.merge("b", 1, Integer::sum);
System.out.println(counts.get("a"));
System.out.println(counts.get("b"));
```
**Answer:** `2`, `1`.
**Why:** `merge` adds to the existing value for `"a"` (1+1), and inserts the default `1` for the new key `"b"`.

---

## Streams and Optional

**Q21.**
```java
Stream<Integer> s = Stream.of(1, 2, 3);
System.out.println(s.count());
System.out.println(s.count());
```
**Answer:** `3`, then `IllegalStateException`.
**Why:** A stream is **single-use**; the second terminal operation fails.

---

**Q22.**
```java
Stream.of(1, 2, 3)
      .filter(n -> {
          System.out.println("checking " + n);
          return n > 1;
      });
```
**Answer:** prints **nothing**.
**Why:** No terminal operation, so the lazy `filter` never runs.

---

**Q23.**
```java
List<String> names = List.of("Sara", "Tom", "Jonathan");
List<String> out = names.stream()
        .filter(n -> n.length() > 3)
        .map(String::toUpperCase)
        .toList();
System.out.println(out);
```
**Answer:** `[SARA, JONATHAN]`.
**Why:** Keep length > 3 → "Sara", "Jonathan" (Tom has length 3), then uppercase.

---

**Q24.**
```java
Optional<String> o1 = Optional.ofNullable(null);
System.out.println(o1.isPresent());
System.out.println(o1.orElse("default"));
```
**Answer:** `false`, `default`.
**Why:** `ofNullable(null)` gives an empty Optional; `orElse` supplies the fallback.
(Note: `Optional.of(null)` would instead throw `NullPointerException`.)

---

## Comparable

**Q25.**
```java
System.out.println("apple".compareTo("banana") < 0);
System.out.println(Integer.compare(5, 3));
```
**Answer:** `true`, `1`.
**Why:** `"apple"` comes before `"banana"`, so `compareTo` returns a negative number.
`Integer.compare(5, 3)` returns a positive number (`1`) because 5 > 3.

---

## Scoring guide

- 22–25 correct: exam-ready.
- 16–21: review the topics you missed in folders `1)`–`13)`.
- Below 16: re-read `Java_Final_Cheat_Sheet.md` and `Mistakes_To_Avoid.md`, then retry.
