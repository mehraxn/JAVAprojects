package eventregistrationsystem;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Service layer for event and registration workflows. */
public final class EventRegistrationSystem {
    private final Map<String, Event> events = new LinkedHashMap<>();
    private final Clock clock;

    public EventRegistrationSystem() {
        this(Clock.systemDefaultZone());
    }

    public EventRegistrationSystem(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "Clock must not be null");
    }

    public void createEvent(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (events.containsKey(event.getId())) {
            throw new IllegalArgumentException("Event ID already exists: " + event.getId());
        }
        for (Event existing : events.values()) {
            if (existing.getName().equalsIgnoreCase(event.getName())
                    && existing.getDate().equals(event.getDate())
                    && existing.getCategory().equalsIgnoreCase(event.getCategory())) {
                throw new IllegalArgumentException(
                        "An event with the same name, date, and category already exists");
            }
        }
        if (event.getRegisteredCount() != 0) {
            throw new IllegalArgumentException("A new event must not contain registrations");
        }
        events.put(event.getId(), new Event(event.getId(), event.getName(), event.getDate(),
                event.getCategory(), event.getCapacity()));
    }

    public EventSnapshot findEvent(String eventId) {
        return new EventSnapshot(requireEvent(eventId));
    }

    public RegistrationSnapshot registerParticipant(String eventId, Attendee attendee) {
        Registration registration = requireEvent(eventId).register(attendee, LocalDateTime.now(clock));
        return new RegistrationSnapshot(registration);
    }

    public void cancelRegistration(String eventId, String attendeeId) {
        requireEvent(eventId).cancelRegistration(attendeeId);
    }

    public void cancelRegistrationByRegistrationId(String eventId, String registrationId) {
        requireEvent(eventId).cancelRegistrationByRegistrationId(registrationId);
    }

    public List<Attendee> getParticipants(String eventId) {
        List<Attendee> copies = new ArrayList<>();
        for (Attendee attendee : requireEvent(eventId).getParticipants()) {
            copies.add(new Attendee(attendee.getId(), attendee.getName(), attendee.getEmail()));
        }
        return Collections.unmodifiableList(copies);
    }

    public List<RegistrationSnapshot> getRegistrations(String eventId) {
        return findEvent(eventId).getRegistrations();
    }

    public List<EventSnapshot> searchEvents(String searchText) {
        if (searchText == null) {
            throw new IllegalArgumentException("Search text must not be null");
        }
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        return matching(event -> event.getName().toLowerCase(Locale.ROOT).contains(query));
    }

    public List<EventSnapshot> searchEventsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Search date must not be null");
        }
        return matching(event -> event.getDate().equals(date));
    }

    public List<EventSnapshot> searchEventsByCategory(String category) {
        String query = requireText(category, "Category").toLowerCase(Locale.ROOT);
        return matching(event -> event.getCategory().toLowerCase(Locale.ROOT).contains(query));
    }

    public List<EventSnapshot> listEvents() {
        return matching(event -> true);
    }

    private List<EventSnapshot> matching(EventPredicate predicate) {
        List<Event> matches = new ArrayList<>();
        for (Event event : events.values()) {
            if (predicate.matches(event)) {
                matches.add(event);
            }
        }
        matches.sort(Comparator.comparing(Event::getDate)
                .thenComparing(Event::getName, String.CASE_INSENSITIVE_ORDER));
        List<EventSnapshot> snapshots = new ArrayList<>();
        for (Event event : matches) {
            snapshots.add(new EventSnapshot(event));
        }
        return Collections.unmodifiableList(snapshots);
    }

    private Event requireEvent(String eventId) {
        String validId = requireText(eventId, "Event ID");
        Event event = events.get(validId);
        if (event == null) {
            throw new IllegalArgumentException("Unknown event ID: " + validId);
        }
        return event;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }

    @FunctionalInterface
    private interface EventPredicate {
        boolean matches(Event event);
    }
}
