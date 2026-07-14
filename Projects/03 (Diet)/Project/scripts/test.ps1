$ErrorActionPreference = "Stop"

$ProjectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $ProjectRoot

if (Test-Path ".\mvnw.cmd") {
    & ".\mvnw.cmd" clean test
} else {
    & mvn clean test
}

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}
