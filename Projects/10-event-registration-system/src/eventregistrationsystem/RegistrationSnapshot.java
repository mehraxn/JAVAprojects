package eventregistrationsystem;

import java.time.LocalDateTime;

/** Immutable public view of a registration. */
public final class RegistrationSnapshot {
    private final String id;
    private final String eventId;
    private final Attendee attendee;
    private final LocalDateTime registeredAt;

    RegistrationSnapshot(Registration registration) {
        this.id = registration.getId();
        this.eventId = registration.getEventId();
        Attendee source = registration.getAttendee();
        this.attendee = new Attendee(source.getId(), source.getName(), source.getEmail());
        this.registeredAt = registration.getRegisteredAt();
    }

    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public Attendee getAttendee() { return attendee; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
