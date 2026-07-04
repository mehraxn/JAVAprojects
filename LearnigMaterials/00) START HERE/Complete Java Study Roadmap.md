# Complete Java Study Roadmap

This roadmap gives you a **step-by-step study path**: what to read first, what to practice after
each section, and when to use the final revision pack. Follow the numbered folders `1)`–`13)`,
practice with `15) Exercises`, and finish with `14) Final Revision Pack`.

> Tip: all code examples were checked by static review and are meant for study — run them
> yourself if you have a JDK.

---

## How topics depend on each other

Some topics only make sense after earlier ones:

- Collections are easier **after** generics.
- Streams need lambdas and functional interfaces first.
- ORM/JPA is easier **after** OOP, collections, and a little SQL/JDBC.

So the order below is deliberate — do not skip ahead.

---

## Week-by-week plan (8 weeks)

You can go faster or slower; each "week" is really "a study block". After each block, do the
matching file in `15) Exercises`.

### Week 1 — Core Java syntax
**Read:** `1) Java Fundamentals` (a → e): JVM/JDK/JRE, variables & types, operators, strings,
`if`/`switch`/loops, methods & pass-by-value, arrays.
**Practice:** `15) Exercises/01_Java_Basics_Exercises.md` and `02_Methods_Arrays_Exercises.md`.
**Goal:** write small programs with loops, methods, and arrays without help.

### Week 2 — Object-Oriented core
**Read:** `2) Basics OOP Core` (a → f): classes & encapsulation, access modifiers / static /
final, `equals`/`hashCode`/`toString`, enums & records, inner classes, Comparator vs comparator.
**Practice:** `15) Exercises/03_Classes_Objects_Constructors_Exercises.md`.
**Goal:** design a small class with private fields, a constructor, and getters.

### Week 3 — Inheritance and polymorphism
**Read:** `3) Inheritance and Polymorphism` (a → i): constructors & `super`, the `Object` root,
abstract vs concrete, anonymous classes, `Comparable`, polymorphism & dynamic dispatch,
overloading vs overriding, interface default/static/marker methods, UML basics.
**Practice:** `15) Exercises/04_Inheritance_Polymorphism_Exercises.md`,
`05_Abstract_Classes_Interfaces_Exercises.md`, and `13_UML_Class_Diagram_Exercises.md`.
**Goal:** explain why `Animal a = new Dog(); a.sound();` runs the Dog version.

### Week 4 — Generics and collections
**Read:** `4) Generics` (a → f) then `5) Collections`.
**Practice:** `15) Exercises/07_Generics_Exercises.md` and `06_Collections_Exercises.md`.
**Goal:** state the PECS rule and pick the right collection for a problem.

### Week 5 — Functional Java and streams
**Read:** `6) Streams` (a → k): stream intro, intermediate operations, filtering, map/flatMap,
collect & collectors, lambdas, method references, `Optional`, grouping/partitioning/primitive
streams, functional interfaces, summarizing collectors.
**Practice:** `15) Exercises/09_Lambdas_Streams_Optional_Exercises.md` and the
`15) Exercises/Streams Lab - Mountain Huts (Stream API).md`.
**Goal:** write a `filter → map → collect` pipeline and explain lazy evaluation.

### Week 6 — Exceptions, IO, and date/time
**Read:** `7) Exceptions`, `8) Java IO`, `13) Date and Time`.
**Practice:** `15) Exercises/08_Exceptions_Exercises.md` and `10_File_IO_DateTime_Exercises.md`.
**Goal:** use try-with-resources; explain `Duration` vs `Period`.

### Week 7 — Testing and tools
**Read:** `9) Testing with JUnit`, `10) Git and Build Tools`.
**Practice:** `15) Exercises/11_JUnit_Exercises.md`.
**Goal:** write a simple JUnit test; explain a merge conflict and the Maven lifecycle.

### Week 8 — Databases and ORM
**Read:** `11) JDBC and Databases`, `12) ORM JPA and Hibernate`.
**Practice:** `15) Exercises/12_JDBC_JPA_ORM_Theory_Exercises.md`.
**Goal:** write a `PreparedStatement`; map a `@ManyToOne`; explain EAGER vs LAZY.

---

## What to read first (if you only have a little time)

1. `00) START HERE/Java Exam Cheat Sheet.md` — the big picture on one page.
2. `1) Java Fundamentals` — you cannot skip the grammar of the language.
3. `2) Basics OOP Core` and `3) Inheritance and Polymorphism` — the heart of Java.

---

## What to practice after each section

- After **every** folder, do the matching file in `15) Exercises`.
- For each exercise: attempt it first, then reveal the **Solution** heading.
- Pay special attention to the **"predict the output"** and **"fix the bug"** questions —
  they are exactly the style professors use.

---

## When to use the Final Revision Pack

Use `14) Final Revision Pack` in the **last week** before the exam, in this order:

1. `Java_Final_Cheat_Sheet.md` — refresh everything quickly.
2. `Mistakes_To_Avoid.md` and `Common Java Exam Traps.md` — the traps that lose marks.
3. Topic question sets: `OOP_Theory_Questions.md`,
   `Collections_Generics_Streams_Questions.md`, `Exceptions_IO_DateTime_Questions.md`,
   `JDBC_JPA_ORM_Questions.md`.
4. `Java_Code_Tracing_Practice.md` — do it **under time pressure** to simulate the exam.
5. `Common_Exam_Questions.md`, `MCQ Set 1 (Questions 1).md`, `Mini MCQ Practice Set 2.md` —
   final mixed practice.

---

## Final study loop (per topic)

1. Read the explanation.
2. Run (or trace) the code example.
3. Change one line and predict the result.
4. Study the common mistakes.
5. Do the exercises.
6. Revise with the Final Revision Pack.

That is the fastest way to move from "I read it" to "I can answer exam questions about it".
See `All Markdown Files Index.md` for a one-line description of every file.
