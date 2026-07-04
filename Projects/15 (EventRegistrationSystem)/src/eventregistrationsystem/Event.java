package eventregistrationsystem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Event {
    private final String id;
    private final String name;
    private final int capacity;
    private final List<Registration> registrations = new ArrayList<>();
    private final Queue<Attendee> waitlist = new ArrayDeque<>();

    public Event(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public Registration register(Attendee attendee) {
        // TODO: Register the attendee or add them to the waitlist when full.
        throw new UnsupportedOperationException("TODO: register an attendee");
    }

    public void cancelRegistration(String attendeeId) {
        // TODO: Cancel the registration and promote the next waitlisted attendee.
        throw new UnsupportedOperationException("TODO: cancel a registration");
    }

    public int getAvailablePlaces() {
        // TODO: Calculate remaining capacity.
        throw new UnsupportedOperationException("TODO: calculate available places");
    }
}
