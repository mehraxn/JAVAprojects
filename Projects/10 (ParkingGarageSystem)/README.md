# Parking Garage System

An in-memory Java application that assigns vehicles to typed parking spots and calculates exit fees.

## Implemented features

- Configure multiple parking levels and uniquely identified spots.
- Support motorcycle, car, and truck spot types.
- Park a vehicle in the first available compatible spot.
- Prevent duplicate entry for the same normalized license plate.
- Remove vehicles and return a receipt containing times, spot, and fee.
- Track total and per-type available spots.
- Reject unknown vehicles, incompatible spots, full-garage entry, and invalid times.

## Fee policy

- Rate: `5.00` per started hour.
- Minimum charge: one hour.
- Partial hours round up.
- A departure before arrival is invalid.

## Structure

- `Vehicle` and `VehicleType` represent incoming vehicles.
- `ParkingSpot` owns occupancy and compatibility state.
- `ParkingLevel` groups spots and reports availability.
- `Garage` assigns spots, tracks active parking, and calculates fees.
- `ParkingReceipt` describes a completed parking stay.
- `Main` runs a deterministic demonstration.

Source files are under `src/parkinggaragesystem` and use only standard Java.

## Run

```powershell
javac -d out src\parkinggaragesystem\*.java
java -cp out parkinggaragesystem.Main
```

See `TESTING.md` for manual test cases.
