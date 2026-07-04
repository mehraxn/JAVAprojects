# Testing Train Ticket Reservation System

## Testing approach

Build routes and trains with small known seat sets. Inspect seat availability and reservation lists after each operation.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Add route | Register a unique station pair | Route appears in listRoutes |
| Add train | Add train with registered route and seats | Train appears in listTrains |
| Specific seat | Reserve an available number | Seat becomes reserved |
| Automatic seat | Reserve without a number | First available seat is selected |
| Search route | Search matching stations | Matching trains are returned |
| Cancel | Cancel an active reservation | Exact seat becomes available |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Full train | Reserve after every seat is used | IllegalStateException |
| Search case | Change station letter case | Same trains are returned |
| Reverse route | Search destination-to-origin | No match |
| Empty system | List routes and trains | Empty unmodifiable lists |
| Passenger validation | Use blank passenger on full train | Passenger error occurs before capacity error |
| Duplicate route pair | Use another ID for same station pair | IllegalArgumentException |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Double reservation | Reserve an occupied seat | IllegalStateException |
| Invalid seat | Use non-positive or unknown number | IllegalArgumentException |
| Invalid stations | Use blank or identical stations | IllegalArgumentException |
| Duplicate IDs | Reuse route, train, or seat identity | IllegalArgumentException |
| Unregistered route | Add train with unknown/different Route object | IllegalArgumentException |
| Empty train | Add train without seats | IllegalArgumentException |
| Unknown cancellation | Cancel missing reservation | IllegalArgumentException |

## Expected results

Each active reservation must correspond to one reserved seat. Failed reservations and cancellations must leave seat state unchanged.

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Test specific and automatic seat selection.
- [ ] Fill a train and verify capacity handling.
- [ ] Verify search is case-insensitive but direction-sensitive.
- [ ] Verify failed reservations leave seats available.
- [ ] Verify cancellation releases the recorded seat.
- [ ] Verify returned lists cannot be modified.
