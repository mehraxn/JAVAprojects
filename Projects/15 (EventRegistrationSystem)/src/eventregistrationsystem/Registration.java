package eventregistrationsystem;

import java.time.LocalDateTime;

public class Registration {
    private final String id;
    private final Attendee attendee;
    private final LocalDateTime registeredAt;

    public Registration(String id, Attendee attendee, LocalDateTime registeredAt) {
        this.id = id;
        this.attendee = attendee;
        this.registeredAt = registeredAt;
    }

    public String getId() { return id; }
    public Attendee getAttendee() { return attendee; }
}
