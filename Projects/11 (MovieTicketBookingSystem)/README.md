# Movie Ticket Booking System

## Description

Movie Ticket Booking System is an in-memory Java project for configuring movies and showtimes, reserving seats, and cancelling bookings.

## Features

- Add movies with unique IDs.
- Add showtimes for registered Movie objects.
- Configure unique row-number seat labels.
- Book one or several seats together.
- Reject duplicate selections and already-booked seats.
- Cancel bookings and release their seats.
- Show available seats.
- Calculate a fixed 12.00 price per seat.

## Java concepts practiced

- Composition between movies, showtimes, seats, and bookings
- Map, List, and Set collections
- BigDecimal price calculation
- Pre-validation before state changes
- Defensive copies and unmodifiable lists

## Main classes

- Movie: stores title and duration.
- Seat: owns booked or available state.
- Showtime: connects a movie, start time, and seat map.
- Booking: immutable booking details.
- BookingSystem: manages movies, showtimes, booking, and cancellation.
- Main: demonstrates the complete booking lifecycle.

## How the program works

Register a Movie, create a Showtime with seats, and add it to BookingSystem. A booking request validates every label and seat before reserving anything. Cancellation validates the stored booking and releases all of its seats.

## Example usage

~~~powershell
javac -d out src\movieticketbookingsystem\*.java
java -cp out movieticketbookingsystem.Main
~~~

The demo books seats 1-1 and 1-2, prints availability, cancels the booking, and prints restored availability.

## Possible future improvements

- Add different ticket categories and prices.
- Add customer details to bookings.
- Add auditorium layouts and accessibility information.
- Search showtimes by movie or date.
- Save booking history to a file.
