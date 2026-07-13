# OOP Theory Questions (with Answers)

Theory-style questions a professor may ask orally or in writing. Covers constructors &
`super`, inheritance & polymorphism, overriding vs overloading, abstract classes vs
interfaces, and `equals`/`hashCode`. Cover the answers and explain each in your own words.

---

## Constructors and `super`

**Q1.** What is a constructor, and how is it different from a normal method?
**Answer:** A constructor initializes a new object. It has the **same name as the class** and
**no return type** (not even `void`). It runs automatically when you use `new`.

**Q2.** What happens if you write no constructor at all?
**Answer:** Java provides a **default no-argument constructor**. But if you declare *any*
constructor yourself, the default is no longer added automatically.

**Q3.** What do `this(...)` and `super(...)` do, and where must they appear?
**Answer:** `this(...)` calls another constructor in the **same** class; `super(...)` calls a
**parent** constructor. Either one, if used, must be the **first statement** in the constructor.

**Q4.** If a constructor does not call `super(...)`, what does Java do?
**Answer:** It inserts a hidden call to the parent's **no-argument** constructor, `super()`.
If the parent has no no-arg constructor, the child **must** call `super(args)` explicitly, or
it will not compile.

**Q5.** In what order are constructors executed in an inheritance chain?
**Answer:** From the top down — the **parent** constructor runs before the child's body.
(`Object` first, then each subclass in order.)

---

## Inheritance and polymorphism

**Q6.** What is inheritance and which keyword expresses it?
**Answer:** Inheritance lets a class reuse and extend another class's fields and methods.
A class uses `extends` for a superclass and `implements` for interfaces.

**Q7.** What is polymorphism?
**Answer:** The ability to treat objects of different subclasses through a common parent type,
so the same call can behave differently depending on the actual object.

**Q8.** What is dynamic (late) dispatch?
**Answer:** For overridden **instance methods**, Java decides which version to run based on
the **runtime object**, not the declared variable type.
```java
Animal a = new Dog();
a.sound(); // Dog's version runs
```

**Q9.** Are fields and static methods polymorphic?
**Answer:** No. **Fields** and **static methods** are resolved by the **declared type**, not
the runtime object. Only overridden instance methods use dynamic dispatch.

**Q10.** Can Java classes inherit from more than one class?
**Answer:** No — Java has single class inheritance. But a class can **implement multiple
interfaces**, which is how Java offers multiple "type" inheritance safely.

---

## Overriding vs overloading

**Q11.** Define overriding and overloading.
**Answer:** **Overriding**: a subclass provides a new implementation of a parent method with
the **same signature**. **Overloading**: several methods in the same class share a **name**
but have **different parameter lists**.

**Q12.** When is each resolved — compile time or runtime?
**Answer:** Overloading is resolved at **compile time** (by declared argument types).
Overriding is resolved at **runtime** (by the real object).

**Q13.** What does the `@Override` annotation give you?
**Answer:** It asks the compiler to verify the method really overrides a parent method. If the
signature is wrong (a typo, wrong parameters), you get a compile error instead of a silent bug.

**Q14.** Does changing only the return type create an overload?
**Answer:** No. Overloads must differ in **parameters**. Two methods with the same name and
parameters but different return types do not compile.

---

## Abstract classes vs interfaces

**Q15.** What is an abstract class?
**Answer:** A class that cannot be instantiated and may contain **abstract methods** (no body)
alongside concrete methods and fields. Subclasses must implement the abstract methods.

**Q16.** How does an interface differ from an abstract class?
**Answer:**
- A class can implement **many** interfaces but extend only **one** class.
- Interface fields are implicitly `public static final` **constants**; abstract classes can
  hold normal instance state.
- Interfaces have no constructors; abstract classes do.
- Interfaces allow `default` and `static` methods (with bodies) plus abstract methods.

**Q17.** When would you choose an interface over an abstract class?
**Answer:** Use an **interface** to express a capability that unrelated classes can share
(e.g. `Comparable`, `Runnable`). Use an **abstract class** when subclasses share real state
and a common base implementation (a true "is-a" hierarchy).

**Q18.** Can an interface be empty, and what is that called?
**Answer:** Yes. An empty interface is a **marker interface** (e.g. `Serializable`); it tags a
class with a capability without adding methods.

**Q19.** Can an abstract class have a constructor even though it cannot be instantiated?
**Answer:** Yes. Its constructor runs when a **subclass** object is created (via `super(...)`).

---

## `equals` and `hashCode`

**Q20.** What does the default `equals` (from `Object`) compare?
**Answer:** References — it is the same as `==`. You must override `equals` to compare field
values (content).

**Q21.** State the `equals`/`hashCode` contract.
**Answer:** If two objects are equal by `equals`, they **must** return the same `hashCode`.
(Unequal objects may share a hash code, but equal objects may not differ.)

**Q22.** Why must you override `hashCode` whenever you override `equals`?
**Answer:** Hash-based collections (`HashMap`, `HashSet`) first use `hashCode` to find a bucket,
then `equals` to confirm. If equal objects have different hash codes, they land in different
buckets and the collection behaves incorrectly (duplicates, lookups that miss).

**Q23.** Write a correct `hashCode` for a class with fields `name` and `age`.
**Answer:**
```java
@Override
public int hashCode() {
    return java.util.Objects.hash(name, age);
}
```

**Q24.** What three properties should `equals` satisfy?
**Answer:** It must be **reflexive** (`a.equals(a)`), **symmetric** (`a.equals(b)` ⇔
`b.equals(a)`), and **transitive** (if `a.equals(b)` and `b.equals(c)` then `a.equals(c)`).
It should also be consistent and return `false` for `null`.

---

## Rapid-fire recap

1. Constructor name = class name, no return type. ✔
2. Parent constructor runs before child. ✔
3. Overriding = runtime; overloading = compile time. ✔
4. Fields and statics are not polymorphic. ✔
5. One `extends`, many `implements`. ✔
6. Override `equals` → override `hashCode` too. ✔
