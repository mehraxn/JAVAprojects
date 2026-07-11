$ErrorActionPreference = "Stop"
Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
javac -Xlint:all -Werror -d out src/miniecommercebackend/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
javac -Xlint:all -Werror -cp out -d test-out tests/miniecommercebackend/*.java
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp "out;test-out" miniecommercebackend.TestRunner
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out miniecommercebackend.Main demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out miniecommercebackend.Main checkout-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out miniecommercebackend.Main cancel-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
java -cp out miniecommercebackend.Main failure-demo
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
