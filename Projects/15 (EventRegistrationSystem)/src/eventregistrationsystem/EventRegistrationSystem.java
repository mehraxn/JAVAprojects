package eventregistrationsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventRegistrationSystem {
    private final Map<String, Event> events = new LinkedHashMap<>();

    public void createEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (events.containsKey(event.getId())) {
            throw new IllegalArgumentException("Event ID already exists: " + event.getId());
        }
        for (Event existingEvent : events.values()) {
            if (existingEvent.getName().equalsIgnoreCase(event.getName())
                    && existingEvent.getDate().equals(event.getDate())
                    && existingEvent.getCategory().equalsIgnoreCase(event.getCategory())) {
                throw new IllegalArgumentException(
                        "An event with the same name, date, and category already exists");
            }
        }
        events.put(event.getId(), event);
    }

    public Event findEvent(String eventId) {
        String validId = requireText(eventId, "Event ID");
        Event event = events.get(validId);
        if (event == null) {
            throw new IllegalArgumentException("Unknown event ID: " + validId);
        }
        return event;
    }

    public Registration registerParticipant(String eventId, Attendee attendee) {
        return findEvent(eventId).register(attendee);
    }

    public void cancelRegistration(String eventId, String attendeeId) {
        findEvent(eventId).cancelRegistration(attendeeId);
    }

    public List<Attendee> getParticipants(String eventId) {
        return findEvent(eventId).getParticipants();
    }

    public List<Event> searchEvents(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        List<Event> matches = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.getName().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(event);
            }
        }
        sortEvents(matches);
        return Collections.unmodifiableList(matches);
    }

    public List<Event> searchEventsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Search date must not be null");
        }
        List<Event> matches = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.getDate().equals(date)) {
                matches.add(event);
            }
        }
        sortEvents(matches);
        return Collections.unmodifiableList(matches);
    }

    public List<Event> searchEventsByCategory(String category) {
        String query = requireText(category, "Category").toLowerCase(Locale.ROOT);
        List<Event> matches = new ArrayList<>();
        for (Event event : events.values()) {
            if (event.getCategory().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(event);
            }
        }
        sortEvents(matches);
        return Collections.unmodifiableList(matches);
    }

    public List<Event> listEvents() {
        List<Event> eventList = new ArrayList<>(events.values());
        sortEvents(eventList);
        return Collections.unmodifiableList(eventList);
    }

    private static void sortEvents(List<Event> eventList) {
        eventList.sort(Comparator.comparing(Event::getDate)
                .thenComparing(Event::getName, String.CASE_INSENSITIVE_ORDER));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
