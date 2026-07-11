$ErrorActionPreference = "Stop"
try {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
    javac -Xlint:all -Werror -d out src/blogapi/*.java
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    javac -Xlint:all -Werror -cp out -d test-out tests/blogapi/*.java
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    java -cp "out;test-out" blogapi.TestRunner
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    java -cp out blogapi.Main demo
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    java -cp out blogapi.Main service-demo
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
} finally {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
}
