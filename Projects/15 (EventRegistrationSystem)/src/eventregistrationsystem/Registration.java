package eventregistrationsystem;

import java.time.LocalDateTime;

public class Registration {
    private final String id;
    private final Attendee attendee;
    private final LocalDateTime registeredAt;

    public Registration(String id, Attendee attendee, LocalDateTime registeredAt) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration ID must not be blank");
        }
        if (attendee == null || registeredAt == null) {
            throw new IllegalArgumentException("Attendee and registration time must not be null");
        }
        this.id = id.trim();
        this.attendee = attendee;
        this.registeredAt = registeredAt;
    }

    public String getId() { return id; }
    public Attendee getAttendee() { return attendee; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
