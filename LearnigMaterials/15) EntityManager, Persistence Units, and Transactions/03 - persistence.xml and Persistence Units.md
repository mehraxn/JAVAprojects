# persistence.xml and Persistence Units

## Learning goals

- Understand `persistence.xml`.
- Learn persistence unit names.
- Recognize common database properties.

## Where it lives

In a Maven project:

```text
src/main/resources/META-INF/persistence.xml
```

## Example

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
    <persistence-unit name="devPU">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <properties>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:demo"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="true"/>
        </properties>
    </persistence-unit>
</persistence>
```

## Persistence unit name

The name in XML must match code:

```java
Persistence.createEntityManagerFactory("devPU");
```

## Development and test units

It is common to have:

- one persistence unit for development;
- one persistence unit for tests;
- different database URLs for each.

## Common mistakes

- Misspelling the persistence unit name.
- Putting `persistence.xml` in the wrong folder.
- Using development database settings in tests.
- Storing real secrets in learning code.

## Mini exercise

Create two persistence unit names: one for development and one for tests. Explain why they should use separate database URLs.

## Quick summary

`persistence.xml` connects JPA code to database configuration through named persistence units.
