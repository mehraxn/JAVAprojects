# Hotel Room Booking System

An in-memory Java application for room availability and date-range bookings.

## Implemented features

- Add rooms with unique numbers, types, and non-negative nightly rates.
- Search available rooms for a valid check-in/check-out range.
- Book rooms and generate simple booking IDs.
- Prevent overlapping bookings for the same room.
- Allow adjacent stays where one booking starts on another booking's check-out date.
- Cancel bookings and make rooms available again.
- Calculate nights, total price, and occupancy percentage.

Date ranges use check-in inclusive and check-out exclusive semantics.

## Structure

- `Room` stores room details and price.
- `Guest` stores basic guest identity.
- `Booking` owns stay dates, overlap rules, and total-price calculation.
- `Hotel` manages rooms, availability, bookings, cancellation, and occupancy.
- `Main` demonstrates the complete booking lifecycle.

Source files are under `src/hotelroombookingsystem` and use only standard Java.

## Run

```powershell
javac -d out src\hotelroombookingsystem\*.java
java -cp out hotelroombookingsystem.Main
```

See `TESTING.md` for manual test cases.
