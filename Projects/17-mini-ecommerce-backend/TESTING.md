# Testing

Run from the project directory. Only `javac` and `java` are required.

## A. Clean

Linux/macOS/Git Bash:
```sh
rm -rf out test-out
```

Windows PowerShell:
```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```

## B. Strict compile application

```text
javac -Xlint:all -Werror -d out src/miniecommercebackend/*.java
```

## C. Strict compile tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/miniecommercebackend/*.java
```

## D. Run tests

Linux/macOS/Git Bash:
```sh
java -cp "out:test-out" miniecommercebackend.TestRunner
```

Windows PowerShell:
```powershell
java -cp "out;test-out" miniecommercebackend.TestRunner
```

## E. Run CLI demos

```text
java -cp out miniecommercebackend.Main help
java -cp out miniecommercebackend.Main demo
java -cp out miniecommercebackend.Main catalog-demo
java -cp out miniecommercebackend.Main checkout-demo
java -cp out miniecommercebackend.Main cancel-demo
java -cp out miniecommercebackend.Main failure-demo
```

## F. Scripts

Linux/macOS/Git Bash:
```sh
./scripts/test.sh
```

Windows PowerShell:
```powershell
.\scripts\test.ps1
```

## G. Cleanup

Linux/macOS/Git Bash:
```sh
rm -rf out test-out
```

Windows PowerShell:
```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```
