# Development vs Test Persistence Units

## Learning goals

- Separate development and test persistence units.
- Understand why names must match code.
- Use create-drop safely for tests.

## Why separate persistence units?

Development data and test data should not mix. Tests should be repeatable and safe to reset.

Example names:

```text
appDevPU
appTestPU
```

The exact names are your choice, but the string in code must match the XML.

## Where persistence.xml lives

In Maven:

```text
src/main/resources/META-INF/persistence.xml
```

For tests, you can also use:

```text
src/test/resources/META-INF/persistence.xml
```

## Example test settings

```xml
<persistence-unit name="appTestPU">
    <properties>
        <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"/>
        <property name="hibernate.hbm2ddl.auto" value="create-drop"/>
    </properties>
</persistence-unit>
```

## Why names matter

```java
Persistence.createEntityManagerFactory("appTestPU");
```

If the name does not match, JPA cannot find the persistence unit.

## Common mistakes

- Running tests against development data.
- Copying a persistence unit name incorrectly.
- Using `create-drop` for important data.
- Hiding real credentials in learning material.

## Mini exercises

1. Create two persistence unit names.
2. Explain why test configuration should reset schema.
3. Find the bug in code that requests the wrong persistence unit name.

## Quick summary

Use separate persistence units for development and tests. Test units should be safe to reset.
