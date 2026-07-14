$ErrorActionPreference = 'Stop'
try {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
    javac -Xlint:all -Werror -d out src/hospitalqueuemanagement/*.java
    if ($LASTEXITCODE -ne 0) { throw 'Application compilation failed.' }
    javac -Xlint:all -Werror -cp out -d test-out tests/hospitalqueuemanagement/*.java
    if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
    java -cp 'out;test-out' hospitalqueuemanagement.TestRunner
    if ($LASTEXITCODE -ne 0) { throw 'Automated tests failed.' }
    foreach ($command in 'demo','queue-demo','emergency-demo','status-demo','statistics-demo','validation-demo') {
        java -cp out hospitalqueuemanagement.Main $command
        if ($LASTEXITCODE -ne 0) { throw "CLI command failed: $command" }
    }
} finally {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
}
