package trainticketreservationsystem;

/**
 * Immutable, read-only view of a {@link Route} handed to outside callers.
 * Holding one can never change system state.
 */
public final class RouteSnapshot {
    private final String id;
    private final String origin;
    private final String destination;

    RouteSnapshot(Route route) {
        this.id = route.getId();
        this.origin = route.getOrigin();
        this.destination = route.getDestination();
    }

    public String getId() { return id; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }

    @Override
    public String toString() {
        return id + " (" + origin + " -> " + destination + ")";
    }
}
