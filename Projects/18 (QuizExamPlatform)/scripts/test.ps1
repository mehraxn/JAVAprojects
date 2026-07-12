# Validates the Quiz / Exam Platform project: strict compile, tests, CLI demos.
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
    javac -Xlint:all -Werror -d out (Get-ChildItem src/quizexamplatform/*.java).FullName
}
Invoke-Step "Strict compile: tests" {
    javac -Xlint:all -Werror -cp out -d test-out (Get-ChildItem tests/quizexamplatform/*.java).FullName
}
Invoke-Step "Automated tests" {
    java -cp "out;test-out" quizexamplatform.TestRunner
}
Invoke-Step "CLI demo" {
    java -cp out quizexamplatform.Main demo
}
Invoke-Step "CLI attempt-demo" {
    java -cp out quizexamplatform.Main attempt-demo
}
Invoke-Step "CLI validation-demo" {
    java -cp out quizexamplatform.Main validation-demo
}
Invoke-Step "CLI scoreboard-demo" {
    java -cp out quizexamplatform.Main scoreboard-demo
}

Write-Host "== Clean up generated files =="
Remove-Item -Recurse -Force out, test-out -ErrorAction SilentlyContinue

Write-Host "ALL CHECKS PASSED"
