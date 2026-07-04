# Parking Garage System

## Description

Parking Garage System is an in-memory Java project that assigns typed parking spots, tracks active vehicles, and calculates an exit fee.

## Features

- Configure multiple parking levels.
- Add globally unique parking spots.
- Support motorcycle, car, and truck types.
- Assign the first available compatible spot.
- Prevent duplicate vehicle entry.
- Track total and per-type availability.
- Calculate 5.00 per started hour with a one-hour minimum.
- Return a validated receipt when a vehicle exits.

## Java concepts practiced

- Classes, enums, and composition
- List and Map collections
- Date/time calculations with Duration and LocalDateTime
- BigDecimal monetary calculations
- State validation and exception handling

## Main classes

- Vehicle and VehicleType: describe an entering vehicle.
- ParkingSpot: owns compatibility and occupancy.
- ParkingLevel: groups spots and reports availability.
- Garage: assigns spots, tracks entries, and calculates fees.
- ParkingReceipt: describes a completed stay.
- Main: demonstrates entry, availability, exit, and receipt output.

## How the program works

Levels and spots are configured first. Garage searches levels for a compatible free spot and records the arrival time. On exit, it validates departure time, rounds duration up to started hours, releases the spot, and returns a ParkingReceipt.

## Example usage

~~~powershell
javac -d out src\parkinggaragesystem\*.java
java -cp out parkinggaragesystem.Main
~~~

The demo parks a car for 90 minutes and reports a two-hour fee.

## Possible future improvements

- Add configurable rates per vehicle type.
- Add lost-ticket handling.
- Add reserved or accessible spots.
- Add daily maximum charges.
- Save completed parking records to a file.
