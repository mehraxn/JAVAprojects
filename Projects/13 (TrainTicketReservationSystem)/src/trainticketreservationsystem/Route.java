package trainticketreservationsystem;

public class Route {
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

    public boolean matches(String origin, String destination) {
        return this.origin.equalsIgnoreCase(requireText(origin, "Origin"))
                && this.destination.equalsIgnoreCase(requireText(destination, "Destination"));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
