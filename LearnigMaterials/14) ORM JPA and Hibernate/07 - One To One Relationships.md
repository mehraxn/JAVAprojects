# One To One Relationships

## Learning goals

- Map one-to-one relationships.
- Use `@JoinColumn`.
- Decide whether one-to-one is the right model.

## Example: User and Profile

```java
@Entity
public class UserAccount {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}
```

```java
@Entity
public class UserProfile {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String displayName;
}
```

## When to use one-to-one

Use it when one entity truly has exactly one related entity and the related data deserves its own table.

## Alternatives

Sometimes the simpler design is to keep fields on the same entity. Do not split tables just because one-to-one exists.

## Common mistakes

- Using one-to-one when an embedded value object would be simpler.
- Forgetting which table owns the foreign key.
- Making the relationship eager without a reason.

## Mini exercises

1. Model `Employee` and `EmployeeProfile`.
2. Decide which side should hold the join column.
3. Explain when profile fields could remain inside `Employee`.

## Quick summary

One-to-one is useful for separate but tightly connected data, but it should not be used automatically.
