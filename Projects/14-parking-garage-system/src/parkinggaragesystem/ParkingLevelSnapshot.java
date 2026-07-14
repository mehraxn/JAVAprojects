package parkinggaragesystem;

import java.util.Collections;
import java.util.List;

/**
 * Immutable, read-only view of a {@link ParkingLevel}, including a snapshot of
 * every spot. The spot list is unmodifiable and its elements are immutable.
 */
public final class ParkingLevelSnapshot {
    private final int levelNumber;
    private final List<ParkingSpotSnapshot> spots;
    private final int totalSpots;
    private final int availableSpots;

    ParkingLevelSnapshot(int levelNumber, List<ParkingSpotSnapshot> spots,
                         int totalSpots, int availableSpots) {
        this.levelNumber = levelNumber;
        this.spots = Collections.unmodifiableList(spots);
        this.totalSpots = totalSpots;
        this.availableSpots = availableSpots;
    }

    public int getLevelNumber() { return levelNumber; }
    public List<ParkingSpotSnapshot> getSpots() { return spots; }
    public int getTotalSpots() { return totalSpots; }
    public int getAvailableSpots() { return availableSpots; }

    public boolean isFull() { return availableSpots == 0; }

    @Override
    public String toString() {
        return "Level " + levelNumber + " (" + availableSpots + "/" + totalSpots + " free)";
    }
}
