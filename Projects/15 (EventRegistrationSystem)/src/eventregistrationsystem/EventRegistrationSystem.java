package eventregistrationsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRegistrationSystem {
    private final Map<String, Event> events = new HashMap<>();

    public void createEvent(Event event) {
        // TODO: Validate and store an event with a unique identifier.
        throw new UnsupportedOperationException("TODO: create an event");
    }

    public Event findEvent(String eventId) {
        // TODO: Return the event or report that it does not exist.
        throw new UnsupportedOperationException("TODO: find an event");
    }

    public List<Event> searchEvents(String searchText) {
        // TODO: Search events by name.
        throw new UnsupportedOperationException("TODO: search events");
    }
}
