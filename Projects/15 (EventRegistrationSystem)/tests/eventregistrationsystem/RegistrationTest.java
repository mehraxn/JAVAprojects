package eventregistrationsystem;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

final class RegistrationTest {
    private RegistrationTest() { }
    static void run() {
        Attendee attendee = new Attendee("A", "Name", "a@b.com");
        LocalDateTime time = LocalDateTime.of(2026, 1, 2, 3, 4);
        Registration registration = new Registration("R1", "E1", attendee, time);
        Assertions.assertEquals("R1", registration.getId(), "registration ID");
        Assertions.assertEquals("E1", registration.getEventId(), "event ID");
        Assertions.assertEquals(attendee, registration.getAttendee(), "attendee");
        Assertions.assertEquals(time, registration.getRegisteredAt(), "time");
        Assertions.assertTrue(Modifier.isFinal(Registration.class.getModifiers()), "Registration final");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Registration(null, "E", attendee, time), "null ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Registration(" ", "E", attendee, time), "blank ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Registration("R", " ", attendee, time), "blank event");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Registration("R", "E", null, time), "null attendee");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Registration("R", "E", attendee, null), "null time");
    }
}
