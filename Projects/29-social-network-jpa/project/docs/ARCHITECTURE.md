# Architecture

## Layers

```mermaid
flowchart TD
    A[Social facade] --> B[Repositories]
    B --> C[JPAUtil - EntityManager]
    C --> D[Hibernate ORM]
    D --> E[H2 Database]
```

- **Facade (`Social`)** — the single public entry point. It validates input, coordinates
  transactions/contexts through `JPAUtil`, applies business rules (friendships, groups, posts,
  statistics), and returns defensive, deterministically-ordered collections.
- **Repositories** — `GenericRepository<E, I>` provides CRUD (`findById`, `findAll`, `save`,
  `update`, `delete`); `PersonRepository`, `GroupRepository`, and `PostRepository` specialise it.
  `PostRepository` adds JPQL pagination queries.
- **Persistence (`JPAUtil`)** — manages a single `EntityManagerFactory` and a thread-local
  `EntityManager`, and exposes `withEntityManager`, `transaction`, `executeInTransaction`, and
  `executeInContext` helpers. `setTestMode()` selects the in-memory test persistence unit.
- **Hibernate / H2** — the JPA provider and database.

## Entities

```mermaid
classDiagram
    class Person {
      String code (id)
      String name
      String surname
    }
    class Group {
      String name (id)
    }
    class Post {
      String id (id)
      String text
      long timestamp
    }
    Person "*" -- "*" Person : friends (self, symmetric)
    Person "*" -- "*" Group : members (PERSON_GROUPS)
    Person "1" -- "*" Post : author
```

- **Person** — id `code`; `name`/`surname` non-null. Self-referential many-to-many `friends`
  (join table `PERSON_FRIENDS`, kept symmetric by the facade). Many-to-many `groups` (owning side,
  join table `PERSON_GROUPS`). One-to-many `posts` (inverse of `Post.author`).
- **Group** — id `name`; members are the inverse side (`mappedBy = "groups"`). Table `SocialGroup`
  (`GROUP` is a reserved SQL word).
- **Post** — id (caller-supplied UUID hex); non-null `text`, `timestamp`; mandatory `author`
  (`@ManyToOne(optional=false)`). Indexed on `author_code`, `post_timestamp`, and the pair.

## Data flows

Friendship (transactional, symmetric):

```mermaid
flowchart TD
    A[addFriendship a,b] --> B[validate + reject self]
    B --> C[load a and b]
    C --> D[a.addFriend b and b.addFriend a]
    D --> E[update both -> PERSON_FRIENDS rows]
```

Post pagination (JPQL, database-side):

```mermaid
flowchart TD
    A[getPaginatedUserPosts author,pageNo,pageLength] --> B[validate pagination]
    B --> C[PostRepository JPQL: WHERE author.code = code ORDER BY timestamp DESC, id DESC]
    C --> D[setFirstResult / setMaxResults]
    D --> E[map to post ids]
```

The friends feed uses `WHERE p.author IN (SELECT f FROM Person pr JOIN pr.friends f WHERE pr.code = :code)`
and returns `authorCode:postId` entries.

## Testing strategy

The professor test (`test/example/TestExample`) validates the R1–R5 contract. Custom tests
(`test/custom`) reset the in-memory schema before each test and cover people/friendships, groups,
posts/pagination, statistics, and an end-to-end workflow. See [`TESTING.md`](TESTING.md).
