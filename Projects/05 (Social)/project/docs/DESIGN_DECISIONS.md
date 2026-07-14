# Design Decisions

## Maven and Java version

Targets **Java 21 (LTS)** (`maven.compiler.release=21`). The original code targeted Java 25 and used
the Java-25 `IO.println` API, so it could not compile on the available JDK 21; those calls were
removed and the target lowered to 21. A Maven wrapper pins the Maven version.

## JPA / Hibernate + H2

Hibernate ORM 6 over H2 keeps the project self-contained: file-based H2 for local use (`socialPU`) and
in-memory H2 for tests (`socialPUTest`, `create-drop`). No external database or deployment profile.

## Facade + repository patterns

`Social` is the only public entry point (kept API-compatible with the lab spec). Data access goes
through `GenericRepository` and its `Person`/`Group`/`Post` specialisations, so the facade contains
business logic, not JPA boilerplate.

## Entities in separate files

`Group` and `Post` were originally declared at the bottom of `Social.java` (a lab workaround). They
are now first-class files (`Group.java`, `Post.java`) alongside `Person.java`, with explicit column
constraints, join-table names, and post indexes.

## JPQL pagination

Post feeds previously loaded **all** posts and filtered/sorted/paginated in Java. They now use
`PostRepository` JPQL with `setFirstResult`/`setMaxResults` and a deterministic
`ORDER BY timestamp DESC, id DESC`. Page numbers are **1-based** (matching the spec and the professor
test); `pageNo < 1` or `pageLength <= 0` throw `IllegalArgumentException`.

## Friendship rules

Friendship is bidirectional and stored on both sides. Self-friendship throws
`IllegalArgumentException`; a repeated friendship is **idempotent** (the underlying `Set` prevents
duplicates); a missing person throws `NoSuchCodeException`. `listOfFriends` is sorted by code.

## Group rename and deletion

Because a group's name is its primary key, **rename** creates a new group, moves the members over
(updating each person, who owns the membership join), and deletes the old group. **Deletion** first
detaches the group from every member (removing `PERSON_GROUPS` rows) and then removes the group, so no
dangling join rows remain. Duplicate group names throw `GroupExistsException`; duplicate membership is
idempotent.

## Statistics and tie-breaking

The spec says ties need not be handled; for determinism (and stable tests) ties are broken by the
lexicographically smallest code/name. Empty data returns `null`.

## Validation

`ValidationUtils` centralises null/blank and pagination checks, throwing `IllegalArgumentException`.
Domain conditions keep the spec's checked exceptions (`PersonExistsException`, `GroupExistsException`,
`NoSuchCodeException`).

## Return-value safety

Facade query methods return defensive `ArrayList` copies with deterministic ordering; internal JPA
collections are never exposed directly.

## Known trade-offs

- H2-only, no production datastore; friendships are modelled with a self-referential many-to-many kept
  symmetric in code rather than via a single canonical edge.
- Statistics iterate entities in Java (dataset is small); they could be pushed into JPQL later.
- The post id is a random UUID hex string (spec: "digits and letters only"), not a DB-generated key.
