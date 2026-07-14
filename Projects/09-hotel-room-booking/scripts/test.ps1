$ErrorActionPreference = "Stop"
try {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
    javac -Xlint:all -Werror -d out src/hotelroombookingsystem/*.java
    javac -Xlint:all -Werror -cp out -d test-out tests/hotelroombookingsystem/*.java
    java -cp "out;test-out" hotelroombookingsystem.TestRunner
    java -cp out hotelroombookingsystem.Main demo
    java -cp out hotelroombookingsystem.Main availability-demo
    java -cp out hotelroombookingsystem.Main overlap-demo
    java -cp out hotelroombookingsystem.Main cancellation-demo
    java -cp out hotelroombookingsystem.Main occupancy-demo
    java -cp out hotelroombookingsystem.Main validation-demo
} finally {
    Remove-Item -Recurse -Force out,test-out -ErrorAction SilentlyContinue
}
