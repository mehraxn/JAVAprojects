# Testing Parking Garage System

## Testing approach

Use explicit arrival and departure times for repeatable fee tests. Inspect both Garage counts and ParkingSpot state.

## Normal test cases

| Test | Action | Expected result |
|---|---|---|
| Configure garage | Add levels and typed spots | Counts match configured spots |
| Park vehicle | Park a compatible type | Matching spot becomes occupied |
| Exit vehicle | Exit a parked plate | Spot becomes available and receipt is returned |
| Count availability | Park and remove vehicles | Counts decrease and increase correctly |
| One-hour fee | Stay exactly one hour | Fee is 5.00 |
| Partial-hour fee | Stay 90 minutes | Fee is 10.00 |

## Edge-case test cases

| Test | Action | Expected result |
|---|---|---|
| Zero duration | Exit at arrival time | Minimum fee is 5.00 |
| Full type | Fill every car spot | Further car entry is rejected |
| No levels | Park before configuration | IllegalStateException |
| Plate case | Reuse plate with different letter case | Duplicate entry is rejected |
| Global spot ID | Reuse spot ID on another level | IllegalArgumentException |
| Invalid exit | Use departure before arrival | Vehicle remains parked |

## Invalid input test cases

| Test | Action | Expected result |
|---|---|---|
| Invalid level/spot | Use negative level or blank spot ID | IllegalArgumentException |
| Wrong vehicle type | Assign car to motorcycle spot | IllegalArgumentException |
| Unknown exit | Remove a plate not parked | IllegalArgumentException |
| Null values | Use null vehicle, type, or time | IllegalArgumentException |
| Invalid receipt | Use blank spot, negative fee, or reversed dates | IllegalArgumentException |
| Duplicate configuration | Reuse level number or spot ID | IllegalArgumentException |

## Manual testing checklist

- [ ] Compile and run Main.
- [ ] Test every vehicle type.
- [ ] Verify availability before entry, after entry, and after exit.
- [ ] Test fee boundaries at 0, 1 hour, and 1 hour plus 1 second.
- [ ] Verify invalid exit times preserve active parking.
- [ ] Verify duplicate plates and spot IDs are rejected.
- [ ] Verify receipt fields match the completed stay.
