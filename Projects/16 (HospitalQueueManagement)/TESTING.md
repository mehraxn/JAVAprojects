# Testing Hospital Queue Management

Run commands from this project directory.

## Clean

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`

## Strict compile

Application:

```text
javac -Xlint:all -Werror -d out src/hospitalqueuemanagement/*.java
```

Tests:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/hospitalqueuemanagement/*.java
```

## Run automated tests

Linux/macOS/Git Bash: `java -cp "out:test-out" hospitalqueuemanagement.TestRunner`

Windows PowerShell: `java -cp "out;test-out" hospitalqueuemanagement.TestRunner`

## Run CLI demos

```text
java -cp out hospitalqueuemanagement.Main help
java -cp out hospitalqueuemanagement.Main demo
java -cp out hospitalqueuemanagement.Main queue-demo
java -cp out hospitalqueuemanagement.Main emergency-demo
java -cp out hospitalqueuemanagement.Main status-demo
java -cp out hospitalqueuemanagement.Main statistics-demo
java -cp out hospitalqueuemanagement.Main validation-demo
```

## One-command scripts

Linux/macOS/Git Bash: `./scripts/test.sh`

Windows PowerShell: `.\scripts\test.ps1`

Both scripts clean generated output when they finish.

## Manual cleanup

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`
