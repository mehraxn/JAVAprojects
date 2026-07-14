# Testing Student Grade Manager

Run these commands from the `05-student-grade-manager` project folder.

## Clean generated files

Linux/macOS/Git Bash:

```bash
rm -rf out test-out
```

Windows PowerShell:

```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## Compile the application

```bash
javac -Xlint:all -Werror -d out src/studentgrademanager/*.java
```

## Compile the tests

```bash
javac -Xlint:all -Werror -cp out -d test-out tests/studentgrademanager/*.java
```

## Run automated tests

Linux/macOS/Git Bash:

```bash
java -cp "out:test-out" studentgrademanager.TestRunner
```

Windows PowerShell:

```powershell
java -cp "out;test-out" studentgrademanager.TestRunner
```

## Run CLI demos

```bash
java -cp out studentgrademanager.Main help
java -cp out studentgrademanager.Main demo
java -cp out studentgrademanager.Main grade-demo
java -cp out studentgrademanager.Main report-demo
java -cp out studentgrademanager.Main ranking-demo
java -cp out studentgrademanager.Main search-demo
java -cp out studentgrademanager.Main validation-demo
```

## Run everything with scripts

Linux/macOS/Git Bash:

```bash
bash scripts/test.sh
```

Windows PowerShell:

```powershell
.\scripts\test.ps1
```

The scripts remove generated build folders after they finish.
