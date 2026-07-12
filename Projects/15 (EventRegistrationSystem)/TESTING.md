# Testing Event Registration System

## Clean

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`

## Strict compile

Application:

```text
javac -Xlint:all -Werror -d out src/eventregistrationsystem/*.java
```

Tests:

```text
javac -Xlint:all -Werror -cp out -d test-out tests/eventregistrationsystem/*.java
```

## Run tests

Linux/macOS/Git Bash: `java -cp "out:test-out" eventregistrationsystem.TestRunner`

Windows PowerShell: `java -cp "out;test-out" eventregistrationsystem.TestRunner`

## Run CLI demos

```text
java -cp out eventregistrationsystem.Main help
java -cp out eventregistrationsystem.Main demo
java -cp out eventregistrationsystem.Main registration-demo
java -cp out eventregistrationsystem.Main capacity-demo
java -cp out eventregistrationsystem.Main cancellation-demo
java -cp out eventregistrationsystem.Main search-demo
java -cp out eventregistrationsystem.Main validation-demo
```

## Scripts

Linux/macOS/Git Bash: `./scripts/test.sh`

Windows PowerShell: `.\scripts\test.ps1`

## Cleanup

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`
