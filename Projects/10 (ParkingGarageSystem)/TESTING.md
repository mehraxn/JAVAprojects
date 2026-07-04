# Testing Parking Garage System

The project has no external test dependencies. Use explicit times with `parkVehicle(vehicle, arrival)` and `exitVehicle(plate, departure)` for repeatable checks.

## Manual test cases

1. Add a level with motorcycle, car, and truck spots; verify total and per-type availability.
2. Park each supported vehicle type and verify it receives a matching spot.
3. Verify available counts decrease on entry and increase on exit.
4. Park for exactly one hour; expect a fee of `5.00`.
5. Park for one hour and one second; expect a fee of `10.00`.
6. Park for zero elapsed time; expect the one-hour minimum fee of `5.00`.
7. Try departing before arrival; expect `IllegalArgumentException` and verify the vehicle remains parked.
8. Fill every car spot and try parking another car; expect `IllegalStateException`.
9. Try parking the same license plate twice with different letter case; expect `IllegalStateException`.
10. Try removing an unknown license plate; expect `IllegalArgumentException`.
11. Assign a car directly to a motorcycle spot; expect `IllegalArgumentException`.
12. Add duplicate level numbers or duplicate spot IDs on one level; expect `IllegalArgumentException`.
13. Verify a returned `ParkingReceipt` contains the vehicle, spot ID, arrival, departure, and fee.
