package trainticketreservationsystem;

public class Route {
    private final String origin;
    private final String destination;

    public Route(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
}
