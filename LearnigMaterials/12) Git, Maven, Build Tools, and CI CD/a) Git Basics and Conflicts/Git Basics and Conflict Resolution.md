# Git Basics and Conflict Resolution - Complete Guide

## Overview
Git is a distributed version control system. It tracks changes in your code and allows multiple developers to work together.

---

## 1. Basic Git workflow

```text
working directory -> staging area -> commit history
```

Commands:

```bash
git status
git add Main.java
git commit -m "Add Main class"
```

---

## 2. Clone, modify, commit, push

```bash
git clone <repository-url>
cd project
git status
git add .
git commit -m "Implement feature"
git push
```

---

## 3. Branches

List all branches (the current one is marked with `*`):

```bash
git branch
```

Create a new branch (without switching to it):

```bash
git branch feature-login
```

Create **and** switch to a new branch in one step:

```bash
git switch -c feature-login
```

Switch to an existing branch:

```bash
git switch main
```

Delete a branch after it is merged:

```bash
git branch -d feature-login
```

Merge another branch into the current one:

```bash
git merge feature-login
```

---

## 3b. `git checkout` vs `git switch`

Older Git used `git checkout` for many things, which confused beginners because the same
command switched branches **and** restored files. Newer Git split it into two clearer commands.

| Task | Newer (recommended) | Older (still works) |
|---|---|---|
| Switch branch | `git switch main` | `git checkout main` |
| Create + switch | `git switch -c feature` | `git checkout -b feature` |
| Restore a file | `git restore file.java` | `git checkout -- file.java` |

You will see `git checkout` a lot in tutorials, so recognise it — but prefer
`git switch` / `git restore` in new work.

---

## 4. Git conflict strategy

Git generally uses:

```text
Copy-Modify-Merge
```

Meaning:

1. Developers copy/clone the repository.
2. They modify files independently.
3. Git merges changes later.
4. If two people changed the same lines, Git reports a conflict.

This is different from locking systems where only one person can edit at a time.

---

## 5. What a conflict looks like

```text
<<<<<<< HEAD
System.out.println("Hello from main");
=======
System.out.println("Hello from feature");
>>>>>>> feature-login
```

You manually choose the final content:

```java
System.out.println("Hello from feature");
```

Then:

```bash
git add Main.java
git commit
```

---

## 6. Useful commands

```bash
git status        # see current changes
git log --oneline # see commit history
git diff          # see unstaged changes
git add .         # stage changes
git commit -m "message"
git pull          # fetch and merge remote changes
git push          # upload commits
```

---

## 7. `.gitignore`

`.gitignore` tells Git which files not to track.

Example for Java:

```gitignore
target/
*.class
*.jar
.idea/
.vscode/
```

Do not commit generated build files or IDE-specific files unless required.

---

## Common mistakes

### Mistake 1: committing compiled `.class` files
Usually do not commit them.

### Mistake 2: ignoring conflicts instead of resolving them
Conflict markers must be removed before commit.

### Mistake 3: using unclear commit messages
Use messages that explain the change.

---

## 8. Viewing history with `git log`

```bash
git log                 # full history: commit hash, author, date, message
git log --oneline       # one short line per commit (easiest to read)
git log --oneline --graph   # shows branches/merges as a text graph
git log -3              # only the last 3 commits
```

Example `--oneline` output:

```text
a1b2c3d Add login validation
9f8e7d6 Create User class
1234abc Initial commit
```

Each line starts with a short **commit hash** you can use in other commands.

---

## 9. Merge conflict — full walkthrough

Imagine two branches changed the **same line** of `Main.java`.

**Step 1 — you try to merge:**

```bash
git switch main
git merge feature-login
```

Git stops and reports a conflict:

```text
Auto-merging Main.java
CONFLICT (content): Merge conflict in Main.java
Automatic merge failed; fix conflicts and then commit the result.
```

**Step 2 — open the file. Git inserted conflict markers:**

```text
<<<<<<< HEAD
System.out.println("Hello from main");
=======
System.out.println("Hello from feature");
>>>>>>> feature-login
```

- `<<<<<<< HEAD` … `=======` is the version already on your current branch (`main`).
- `=======` … `>>>>>>> feature-login` is the version from the branch you are merging.

**Step 3 — edit the file to the final content you want, and delete ALL three markers**
(`<<<<<<<`, `=======`, `>>>>>>>`):

```java
System.out.println("Hello from feature");
```

**Step 4 — check status, stage the fixed file, and finish the merge:**

```bash
git status          # shows the file as "unmerged" until you add it
git add Main.java
git commit          # completes the merge (Git suggests a merge message)
```

**Step 5 — confirm it worked:**

```bash
git log --oneline --graph
```

Key rule: the file is **not resolved** until every conflict marker is removed and the
file is `git add`-ed.

---

## Exam Notes

- Git workflow: **working directory → staging area (`git add`) → commit history (`git commit`)**.
- Git uses the **Copy-Modify-Merge** model, not Lock-Modify-Unlock.
- `git switch` changes branch; `git branch` lists/creates/deletes; `git merge` combines.
- Conflict markers are `<<<<<<<`, `=======`, `>>>>>>>`; all must be removed before committing.
- A conflicted file stays "unmerged" until you `git add` it.
- `git log --oneline` is the quick way to read history; the first token is the commit hash.

---

## Mini quiz

### Q1. Which strategy does Git use for conflicts?
Answer: Copy-Modify-Merge.

### Q2. What command stages files?
Answer: `git add`.

### Q3. What file excludes generated files from Git?
Answer: `.gitignore`.

---

## More Practice Questions

1. What command lists all local branches and shows which one you are on?

2. Write the single command that creates a branch called `bugfix` **and** switches to it.

3. You see `<<<<<<< HEAD` in a file. What does this marker mean, and what must you do
   before you can commit?

4. After editing a conflicted file, which two commands finish the merge?
   (Answer: `git add <file>` then `git commit`.)

5. What is the difference between `git switch main` and `git restore file.java`?

6. Which command shows a compact one-line-per-commit history? (Answer: `git log --oneline`.)

7. True or false: a conflicted file is resolved as soon as you delete the markers, even
   without `git add`. Explain. (Answer: false — you must stage it with `git add`.)
