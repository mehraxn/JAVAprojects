# Reading Maven Error Messages

## Learning goals

- Identify common Maven failure types.
- Find the first real error.
- Avoid being distracted by long stack traces.

## Dependency resolution failure

Typical signs:

```text
Could not resolve dependencies
Could not find artifact
```

Check group ID, artifact ID, version, and internet access.

## Compilation failure

Typical signs:

```text
COMPILATION ERROR
cannot find symbol
release version 21 not supported
```

Check imports, package names, Java version, and compiler release.

## Test failure

Typical signs:

```text
There are test failures.
```

Look at the first failing test and assertion message.

## Plugin failure

Typical signs:

```text
Failed to execute goal
```

Find the plugin name and goal. Then read the first cause after that line.

## Practical reading strategy

1. Scroll to the first `ERROR`.
2. Identify the phase: compile, test, package, plugin.
3. Read the first real cause.
4. Fix one problem and rerun.

## Common mistakes

- Reading only the last line.
- Fixing random files without understanding the phase.
- Ignoring Java version mismatch.
- Hiding useful test assertion messages.

## Mini exercises

1. Classify an error as dependency, compilation, test, or plugin failure.
2. Explain what `cannot find symbol` usually means.
3. Explain how to investigate `release version not supported`.

## Quick summary

Maven output is long, but the first real error usually tells you what category failed.
