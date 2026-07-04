# Testing Movie Ticket Booking System

## Testing approach

Build small showtimes with known seat maps. Check both returned bookings and individual Seat state after every operation.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add movie | Register a unique Movie | Movie appears in listMovies |
| Add showtime | Add registered movie with seats | Showtime appears in listShowtimes |
| Single booking | Book one available label | Seat becomes booked |
| Group booking | Book several distinct labels | One Booking contains every label |
| Price | Book three seats | Total is 36.00 |
| Cancel | Cancel an existing booking | Every booked seat becomes available |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Empty selection | Submit an empty label list | IllegalArgumentException |
| Duplicate request | Repeat a label in one request | IllegalArgumentException and no seats change |
| Mixed request | Include valid and unknown labels | Request fails and valid seats remain available |
| Empty showtime | Add a showtime without seats | IllegalArgumentException |
| Registered identity | Use another Movie object with the same ID | IllegalArgumentException |
| Read-only booking | Modify returned seat-label list | UnsupportedOperationException |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Double booking | Book an occupied seat | IllegalStateException |
| Invalid seat | Use zero/negative coordinates or unknown label | IllegalArgumentException |
| Invalid movie | Use blank values or non-positive duration | IllegalArgumentException |
| Duplicate IDs | Reuse movie, showtime, or seat identity | IllegalArgumentException |
| Invalid Booking | Construct with blank/duplicate labels or invalid price | IllegalArgumentException |
| Unknown cancellation | Cancel a missing booking ID | IllegalArgumentException |

## Expected results

Booking is all-or-nothing: either every requested seat is reserved and stored, or no requested seat changes. Cancellation must restore every stored seat.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Verify available-seat count before and after booking.
- [ ] Verify multi-seat booking is all-or-nothing.
- [ ] Verify duplicate labels are rejected case-insensitively.
- [ ] Verify cancellation releases every stored seat.
- [ ] Verify price equals seat count multiplied by 12.00.
- [ ] Verify returned booking data cannot be modified.
