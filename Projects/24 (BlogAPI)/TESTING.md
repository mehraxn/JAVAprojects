# Testing

Run all commands from the project directory. No build tool or external test library is required.

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
javac -Xlint:all -Werror -d out src/blogapi/*.java
```

## C. Strict compile tests

```text
javac -Xlint:all -Werror -cp out -d test-out tests/blogapi/*.java
```

## D. Run tests

Linux/macOS/Git Bash:
```sh
java -cp "out:test-out" blogapi.TestRunner
```

Windows PowerShell:
```powershell
java -cp "out;test-out" blogapi.TestRunner
```

## E. Run CLI demos

```text
java -cp out blogapi.Main help
java -cp out blogapi.Main demo
java -cp out blogapi.Main service-demo
```

## F. Run server manually

```text
java -cp out blogapi.Main server 8082
```

In another terminal:

```sh
curl -i -X POST http://localhost:8082/users -d "name=alice"
curl -i -X POST http://localhost:8082/posts -d "authorId=U-1&title=Hello&content=First+post"
curl -i http://localhost:8082/posts
curl -i http://localhost:8082/unknown
```

Valid requests return JSON. `/unknown` returns a JSON 404 response.

## G. Scripts

Linux/macOS/Git Bash:
```sh
./scripts/test.sh
```

Windows PowerShell:
```powershell
.\scripts\test.ps1
```

Both scripts clean generated output when they finish.

## H. Cleanup

Linux/macOS/Git Bash:
```sh
rm -rf out test-out
```

Windows PowerShell:
```powershell
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
```
