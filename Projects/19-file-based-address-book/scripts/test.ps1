# Validates the File-Based Address Book project: strict compile, tests, CLI demos.
# Run from the project root: .\scripts\test.ps1
$ErrorActionPreference = "Stop"

Set-Location (Join-Path $PSScriptRoot "..")

function Invoke-Step {
    param([string]$Name, [scriptblock]$Body)
    Write-Host "== $Name =="
    & $Body
    if ($LASTEXITCODE -ne 0) {
        Write-Error "$Name failed with exit code $LASTEXITCODE"
        exit 1
    }
}

Write-Host "== Clean =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Invoke-Step "Strict compile: application" {
    javac -Xlint:all -Werror -d out (Get-ChildItem src/filebasedaddressbook/*.java).FullName
}
Invoke-Step "Strict compile: tests" {
    javac -Xlint:all -Werror -cp out -d test-out (Get-ChildItem tests/filebasedaddressbook/*.java).FullName
}
Invoke-Step "Automated tests" {
    java -cp "out;test-out" filebasedaddressbook.TestRunner
}
Invoke-Step "CLI demo" {
    java -cp out filebasedaddressbook.Main demo
}
Invoke-Step "CLI file-demo" {
    java -cp out filebasedaddressbook.Main file-demo
}
Invoke-Step "CLI import-demo" {
    java -cp out filebasedaddressbook.Main import-demo
}
Invoke-Step "CLI validation-demo" {
    java -cp out filebasedaddressbook.Main validation-demo
}

Write-Host "== Clean up generated files =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "ALL CHECKS PASSED"
