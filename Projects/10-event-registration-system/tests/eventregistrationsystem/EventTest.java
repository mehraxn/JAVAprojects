package eventregistrationsystem;

import java.lang.reflect.Modifier;
import java.time.LocalDate;

final class EventTest {
    private EventTest() { }
    static void run() {
        LocalDate date = LocalDate.of(2026, 2, 3);
        Event event = new Event("E", "Name", date, "Tech", 1);
        Assertions.assertEquals("E", event.getId(), "event ID"); Assertions.assertEquals("Name", event.getName(), "name");
        Assertions.assertEquals(date, event.getDate(), "date"); Assertions.assertEquals("Tech", event.getCategory(), "category");
        Assertions.assertEquals(1, event.getCapacity(), "capacity"); Assertions.assertEquals(1, event.getAvailablePlaces(), "available");
        Assertions.assertTrue(Modifier.isFinal(Event.class.getModifiers()), "Event final");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event(null,"N",date,"C",1), "null ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event(" ","N",date,"C",1), "blank ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event("E"," ",date,"C",1), "blank name");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event("E","N",null,"C",1), "null date");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event("E","N",date," ",1), "blank category");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event("E","N",date,"C",0), "zero capacity");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Event("E","N",date,"C",-1), "negative capacity");
        Attendee attendee = new Attendee("A", "Person", "a@b.com"); Registration registration = event.register(attendee);
        Assertions.assertNotNull(registration, "registered"); Assertions.assertEquals(0, event.getAvailablePlaces(), "full"); Assertions.assertTrue(event.isFull(), "is full");
        Assertions.assertThrows(IllegalArgumentException.class, () -> event.register(attendee), "duplicate before full check");
        Assertions.assertThrows(IllegalStateException.class, () -> event.register(new Attendee("B","B","b@b.com")), "full rejected");
        Assertions.assertEquals(1, event.getRegisteredCount(), "failed registration unchanged");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> event.getRegistrations().clear(), "registrations unmodifiable");
        event.cancelRegistrationByRegistrationId(registration.getId()); Assertions.assertEquals(1, event.getAvailablePlaces(), "ID cancellation restores");
        Assertions.assertThrows(IllegalArgumentException.class, () -> event.cancelRegistration("missing"), "missing cancellation");
    }
}
