# Train Ticket Reservation System

## Description

Train Ticket Reservation System is an in-memory Java project for routes, trains, numbered seats, and passenger reservations.

## Features

- Add unique routes and reject duplicate station pairs.
- Add trains that reference registered routes.
- Configure unique positive seat numbers.
- Reserve a chosen seat or the first available seat.
- Prevent double reservation.
- Cancel reservations and release seats.
- Search trips by origin and destination.
- List available seats and active train reservations.

## Java concepts practiced

- Object composition and identity consistency
- Map and List collections
- Search and validation
- State transitions for seats
- Generated IDs and unmodifiable query results

## Main classes

- Route: stores route ID, origin, and destination.
- Seat: owns reservation state.
- Train: combines a route with numbered seats.
- Reservation: immutable passenger reservation details.
- ReservationSystem: manages routes, trains, and reservations.
- Main: demonstrates search, reservation, and cancellation.

## How the program works

Register a Route, build a Train using that same Route object, add seats, and register the train. ReservationSystem validates train, passenger, and seat before reserving. Cancellation finds the exact seat recorded by the reservation and releases it.

## Example usage

~~~powershell
javac -d out src\trainticketreservationsystem\*.java
java -cp out trainticketreservationsystem.Main
~~~

The demo reserves seat 3, performs a case-insensitive route search, then cancels the reservation.

## Possible future improvements

- Add departure and arrival times.
- Add fares and carriage numbers.
- Add passenger contact details.
- Search by travel date.
- Save schedules and reservations to files.
