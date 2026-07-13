# Java Version and Compiler Release

## Learning goals

- Understand Java source, target, and release settings.
- Learn why Java 17 or Java 21 is commonly chosen.
- Avoid mismatched local and CI versions.

## Java versions

Modern Java applications often use long-term support versions such as Java 17 or Java 21. Very new versions can be useful, but libraries, CI images, and deployment environments may not support them immediately.

## Compiler release

Prefer:

```xml
<properties>
    <maven.compiler.release>21</maven.compiler.release>
</properties>
```

Or with the compiler plugin:

```xml
<configuration>
    <release>21</release>
</configuration>
```

The `release` option tells the compiler which Java API level to target.

## Source and target

Older configurations sometimes use:

```xml
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
```

For most modern builds, `release` is safer.

## Common mistakes

- Developing with Java 21 but CI uses Java 17.
- Using language features not supported by the configured release.
- Assuming the newest Java version is always the safest choice.

## Mini exercise

Check your local Java version with:

```bash
java -version
javac -version
```

Then configure a Maven project to compile with Java 21.

## Quick summary

Set the Java version explicitly so local builds and CI builds behave the same way.
