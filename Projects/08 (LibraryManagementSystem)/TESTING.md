# Testing the Library Management System

The project uses a small, dependency-free test harness: a custom assertion
helper (`TestSupport`) and a runner (`TestRunner`). No JUnit, Maven, Gradle, or
other external libraries are involved. Tests live in
`tests/librarymanagementsystem/` and share the source package, so they can
exercise package-private behaviour directly.

## What is covered

- `LoanStatusTest` — enum values.
- `BookTest` — validation, availability transitions, snapshot.
- `MemberTest` — validation, email rule, borrowed-set tracking, snapshot.
- `LoanRecordTest` — due dates, status/return transition, overdue logic, snapshot.
- `LibraryTest` — borrow/return/history workflows and failed-op state safety
  (the most important file), with a fixed `Clock`.
- `SearchTest` — catalogue search and available-book listing.
- `SnapshotTest` — unmodifiable results and proof that returned data cannot mutate
  internal book/member/loan state.
- `MainTest` — `Main.run` smoke tests, called in-process (no separate JVM).

## Commands

### A) Clean

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

### B) Strict compile: application

~~~
javac -Xlint:all -Werror -d out src/librarymanagementsystem/*.java
~~~

### C) Strict compile: tests

~~~
javac -Xlint:all -Werror -cp out -d test-out tests/librarymanagementsystem/*.java
~~~

### D) Run tests

Linux/macOS/Git Bash:

~~~
java -cp "out:test-out" librarymanagementsystem.TestRunner
~~~

Windows PowerShell:

~~~
java -cp "out;test-out" librarymanagementsystem.TestRunner
~~~

### E) Run CLI demos

~~~
java -cp out librarymanagementsystem.Main help
java -cp out librarymanagementsystem.Main demo
java -cp out librarymanagementsystem.Main borrow-demo
java -cp out librarymanagementsystem.Main return-demo
java -cp out librarymanagementsystem.Main search-demo
java -cp out librarymanagementsystem.Main overdue-demo
java -cp out librarymanagementsystem.Main history-demo
java -cp out librarymanagementsystem.Main validation-demo
~~~

### F) Scripts

Linux/macOS/Git Bash:

~~~
./scripts/test.sh
~~~

Windows PowerShell:

~~~
.\scripts\test.ps1
~~~

> Note: the JVM classpath separator differs by platform — `:` on Linux/macOS,
> `;` on Windows. `test.sh` uses `:` and `test.ps1` uses `;` accordingly.

### G) Cleanup

Linux/macOS/Git Bash:

~~~
rm -rf out test-out
~~~

Windows PowerShell:

~~~
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
~~~

## Manual testing checklist

- [ ] Compile strictly with `-Xlint:all -Werror`.
- [ ] Run `TestRunner` and confirm all cases pass.
- [ ] Verify a borrow updates book, member, and loan together.
- [ ] Verify a return reverses all three and keeps the loan in completed history.
- [ ] Verify failed borrow/return (unknown, unavailable, limit, wrong-member) leave state unchanged.
- [ ] Verify the borrow limit is enforced and returned loans do not count.
- [ ] Verify due date = borrow date + loan period, and overdue detection past due.
- [ ] Verify returned loans are never overdue.
- [ ] Verify case-insensitive title/author search and available-book listing.
- [ ] Verify returned lists are unmodifiable and snapshots cannot mutate state.
