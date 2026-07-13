# Maven Profiles

## Learning goals

- Understand Maven profiles.
- Activate dev/test-style build settings.
- Avoid hiding important build behavior in profiles.

## What is a profile?

A Maven profile is a named set of build configuration that can be activated when needed.

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <app.environment>development</app.environment>
        </properties>
    </profile>
</profiles>
```

Activate it:

```bash
mvn test -Pdev
```

## When to use profiles

Profiles can help with:

- development vs test configuration;
- enabling integration tests;
- selecting resource files;
- setting build properties.

## Example with a test profile

```xml
<profile>
    <id>integration-tests</id>
    <properties>
        <skip.integration.tests>false</skip.integration.tests>
    </properties>
</profile>
```

## Common mistakes

- Putting essential compilation settings only in a profile.
- Forgetting to activate the profile in CI.
- Creating profiles for things that should be normal configuration.
- Using profile names that do not explain intent.

## Mini exercises

1. Create a `dev` profile with one property.
2. Create a `test` profile for test resources.
3. Explain why CI must activate the same profile you test locally.

## Quick summary

Profiles are useful for controlled build variations, but the default build should still be understandable.
