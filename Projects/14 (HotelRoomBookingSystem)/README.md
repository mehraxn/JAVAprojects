# Hotel Room Booking System

## Description

Hotel Room Booking System is an in-memory Java project for room availability, date-range booking, cancellation, pricing, and occupancy.

## Features

- Add rooms with unique numbers and nightly rates.
- Search available rooms for a date range.
- Book rooms using generated booking IDs.
- Prevent overlapping bookings for the same room.
- Allow adjacent stays on a checkout/check-in boundary.
- Cancel bookings.
- Calculate nights, total price, and occupancy percentage.

## Java concepts practiced

- LocalDate and date-range logic
- BigDecimal price calculations
- Map and List collections
- Object composition
- Validation, overlap detection, and defensive lists

## Main classes

- Room: stores room number, type, and nightly rate.
- Guest: stores basic guest identity.
- Booking: owns dates, overlap checks, and price calculation.
- Hotel: manages rooms, availability, bookings, and occupancy.
- Main: demonstrates booking and cancellation.

## How the program works

Hotel stores Room objects by number and Booking objects by generated ID. Date ranges are check-in inclusive and check-out exclusive. A room is available only when none of its existing bookings overlaps the requested range.

## Example usage

~~~powershell
javac -d out src\hotelroombookingsystem\*.java
java -cp out hotelroombookingsystem.Main
~~~

The demo books a double room for three nights, prints price and occupancy, then cancels the booking.

## Possible future improvements

- Add room amenities and capacity.
- Add guest registration and booking history.
- Add seasonal pricing.
- Search by room type or price range.
- Save reservations to a file.
