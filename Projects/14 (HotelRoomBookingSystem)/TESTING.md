# Testing Hotel Room Booking System

## Testing approach

Use fixed LocalDate values. Check availability before booking, after booking, and after cancellation.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add rooms | Add unique room numbers | Rooms appear in listRooms |
| Search | Search an unbooked range | All rooms are available |
| Book room | Book one available room | Booking is stored |
| Price | Book three nights at 125.00 | Total is 375.00 |
| Occupancy | Book one of two rooms | Occupancy is 50% during stay |
| Cancel | Cancel booking | Room becomes available |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Adjacent stay | Start on previous checkout date | Booking succeeds |
| Checkout date | Calculate occupancy on checkout | Room is not occupied |
| Empty hotel | Search valid range | Empty unmodifiable list |
| Empty occupancy | Calculate with no rooms | 0.0 |
| Zero rate | Add free room | Room is accepted |
| Failed overlap | Attempt conflicting booking | Booking count and availability remain unchanged |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid range | Equal dates or checkout before check-in | IllegalArgumentException |
| Null date | Search or book with null date | IllegalArgumentException |
| Overlap | Book same room over occupied dates | IllegalStateException |
| Duplicate room | Reuse room number | IllegalArgumentException |
| Invalid rate | Use negative or null money value | IllegalArgumentException |
| Unknown room/booking | Use missing identifiers | IllegalArgumentException |

## Expected results

Availability, booking lists, price, and occupancy must follow check-in-inclusive/check-out-exclusive date rules. Rejected overlaps must not create bookings.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Test overlaps at the beginning, middle, and end of a stay.
- [ ] Test valid adjacent bookings.
- [ ] Verify invalid ranges fail even when the hotel is empty.
- [ ] Verify failed bookings do not change state.
- [ ] Verify price uses the exact number of nights.
- [ ] Verify returned lists cannot be modified.
