# Testing Hotel Room Booking System

## Clean

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`

## Strict compile application

`javac -Xlint:all -Werror -d out src/hotelroombookingsystem/*.java`

## Strict compile tests

`javac -Xlint:all -Werror -cp out -d test-out tests/hotelroombookingsystem/*.java`

## Run tests

Linux/macOS/Git Bash: `java -cp "out:test-out" hotelroombookingsystem.TestRunner`

Windows PowerShell: `java -cp "out;test-out" hotelroombookingsystem.TestRunner`

## Run CLI demos

```text
java -cp out hotelroombookingsystem.Main help
java -cp out hotelroombookingsystem.Main demo
java -cp out hotelroombookingsystem.Main availability-demo
java -cp out hotelroombookingsystem.Main overlap-demo
java -cp out hotelroombookingsystem.Main cancellation-demo
java -cp out hotelroombookingsystem.Main occupancy-demo
java -cp out hotelroombookingsystem.Main validation-demo
```

## Scripts

Linux/macOS/Git Bash: `./scripts/test.sh`

Windows PowerShell: `.\scripts\test.ps1`

## Cleanup

Linux/macOS/Git Bash: `rm -rf out test-out`

Windows PowerShell: `Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue`
