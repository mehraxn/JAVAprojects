package parkinggaragesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One level (floor) of the garage: a numbered collection of {@link ParkingSpot}s
 * with unique IDs.
 *
 * <p>The level number is non-negative and fixed at construction; the spot set is
 * fixed too (built from the constructor's list). Outside callers receive
 * {@link ParkingSpotSnapshot}/{@link ParkingLevelSnapshot} views, never the live
 * spots. {@link Garage} uses the package-private accessors to assign vehicles.
 */
public final class ParkingLevel {
    private final int number;
    // Keyed by spot ID; preserves insertion order.
    private final Map<String, ParkingSpot> spotsById = new LinkedHashMap<>();

    public ParkingLevel(int number, List<ParkingSpot> spots) {
        if (number < 0) {
            throw new IllegalArgumentException("Level number must not be negative");
        }
        if (spots == null) {
            throw new IllegalArgumentException("Spot list must not be null");
        }
        if (spots.isEmpty()) {
            throw new IllegalArgumentException("Level must have at least one spot");
        }
        this.number = number;
        for (ParkingSpot spot : spots) {
            if (spot == null) {
                throw new IllegalArgumentException("Spot list must not contain null");
            }
            if (spotsById.containsKey(spot.getId())) {
                throw new IllegalArgumentException(
                        "Parking spot ID already exists on level: " + spot.getId());
            }
            spotsById.put(spot.getId(), spot);
        }
    }

    public int getNumber() {
        return number;
    }

    public int getTotalSpotCount() {
        return spotsById.size();
    }

    /** Live compatible free spot, or {@code null} if none — package-private. */
    ParkingSpot findAvailableSpot(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        for (ParkingSpot spot : spotsById.values()) {
            if (spot.isAvailable() && spot.supports(type)) {
                return spot;
            }
        }
        return null;
    }

    /** Live spot by ID, or {@code null} — package-private for {@link Garage}. */
    ParkingSpot spotById(String spotId) {
        return spotsById.get(spotId);
    }

    /** All spot IDs on this level — package-private for duplicate detection. */
    java.util.Set<String> spotIds() {
        return spotsById.keySet();
    }

    public int getAvailableSpotCount() {
        int count = 0;
        for (ParkingSpot spot : spotsById.values()) {
            if (spot.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getAvailableSpotCount(VehicleType type) {
        if (type == null) {
            throw new IllegalArgumentException("Vehicle type must not be null");
        }
        int count = 0;
        for (ParkingSpot spot : spotsById.values()) {
            if (spot.isAvailable() && spot.supports(type)) {
                count++;
            }
        }
        return count;
    }

    public int getOccupiedSpotCount() {
        return getTotalSpotCount() - getAvailableSpotCount();
    }

    public boolean isFull() {
        return getAvailableSpotCount() == 0;
    }

    /** Unmodifiable list of immutable snapshots of every spot, in order. */
    public List<ParkingSpotSnapshot> getSpotSnapshots() {
        List<ParkingSpotSnapshot> views = new ArrayList<>();
        for (ParkingSpot spot : spotsById.values()) {
            views.add(spot.toSnapshot());
        }
        return Collections.unmodifiableList(views);
    }

    /** Immutable, read-only view of this level and its seat map. */
    public ParkingLevelSnapshot toSnapshot() {
        return new ParkingLevelSnapshot(number, getSpotSnapshots(),
                getTotalSpotCount(), getAvailableSpotCount());
    }
}
