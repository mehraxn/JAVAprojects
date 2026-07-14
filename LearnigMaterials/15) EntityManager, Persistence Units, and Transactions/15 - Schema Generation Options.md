# Schema Generation Options

## Learning goals

- Understand common schema generation options.
- Know which options are safer for tests.
- Avoid dangerous production assumptions.

## Common options

| Option | Meaning |
|---|---|
| `none` | Do not manage schema |
| `validate` | Check schema matches mappings |
| `update` | Try to update schema |
| `create` | Drop and create schema at startup |
| `create-drop` | Create at startup and drop at shutdown |

## Test-friendly option

`create-drop` is useful for tests because each run can start clean.

```xml
<property name="hibernate.hbm2ddl.auto" value="create-drop"/>
```

## Development option

`update` can be convenient during learning, but it is not a complete migration strategy.

## Safer real-application idea

Important applications usually use migration tools and `validate` to confirm schema compatibility.

## Common mistakes

- Using `create-drop` on important data.
- Assuming `update` handles every schema change safely.
- Forgetting that schema generation settings differ by provider.
- Ignoring validation errors.

## Mini exercises

1. Choose a schema option for unit tests.
2. Choose a schema option for a database with important data.
3. Explain why `update` is convenient but limited.

## Quick summary

Schema generation is useful for learning and tests, but important data needs controlled migration practices.
