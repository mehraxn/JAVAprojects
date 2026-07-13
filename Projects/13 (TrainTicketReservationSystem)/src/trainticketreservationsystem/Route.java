package trainticketreservationsystem;

import java.util.Objects;

/**
 * An immutable travel route from an origin station to a destination station.
 *
 * <p>A route carries a stable identifier plus its origin and destination. Origin
 * and destination must differ (ignoring case). Route matching is case-insensitive
 * but direction-sensitive: {@code Berlin -> Munich} does not match
 * {@code Munich -> Berlin}.
 */
public final class Route {
    private final String id;
    private final String origin;
    private final String destination;

    public Route(String id, String origin, String destination) {
        this.id = requireText(id, "Route ID");
        this.origin = requireText(origin, "Origin");
        this.destination = requireText(destination, "Destination");
        if (this.origin.equalsIgnoreCase(this.destination)) {
            throw new IllegalArgumentException("Origin and destination must be different");
        }
    }

    public String getId() { return id; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }

    /**
     * Returns {@code true} when this route runs from {@code origin} to
     * {@code destination}, comparing case-insensitively but preserving direction.
     */
    public boolean matches(String origin, String destination) {
        return this.origin.equalsIgnoreCase(requireText(origin, "Origin"))
                && this.destination.equalsIgnoreCase(requireText(destination, "Destination"));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Route)) {
            return false;
        }
        Route route = (Route) other;
        return id.equals(route.id)
                && origin.equalsIgnoreCase(route.origin)
                && destination.equalsIgnoreCase(route.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                origin.toLowerCase(java.util.Locale.ROOT),
                destination.toLowerCase(java.util.Locale.ROOT));
    }

    @Override
    public String toString() {
        return id + " (" + origin + " -> " + destination + ")";
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
