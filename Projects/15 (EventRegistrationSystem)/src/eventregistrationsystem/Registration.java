package eventregistrationsystem;

import java.time.LocalDateTime;

public final class Registration {
    private final String id;
    private final String eventId;
    private final Attendee attendee;
    private final LocalDateTime registeredAt;

    public Registration(String id, String eventId, Attendee attendee, LocalDateTime registeredAt) {
        this.id = requireText(id, "Registration ID");
        this.eventId = requireText(eventId, "Event ID");
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee must not be null");
        }
        if (registeredAt == null) {
            throw new IllegalArgumentException("Registration time must not be null");
        }
        this.attendee = attendee;
        this.registeredAt = registeredAt;
    }

    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public Attendee getAttendee() { return attendee; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
