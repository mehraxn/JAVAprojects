package eventregistrationsystem;

import java.lang.reflect.Modifier;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

final class EventRegistrationSystemTest {
    private EventRegistrationSystemTest() { }
    static void run() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-02T03:04:05Z"), ZoneOffset.UTC);
        EventRegistrationSystem system = new EventRegistrationSystem(clock);
        Event external = new Event("E2","Zulu",LocalDate.of(2026,5,2),"Community",2);
        system.createEvent(external); system.createEvent(new Event("E1","Alpha",LocalDate.of(2026,5,1),"Technology",1));
        Assertions.assertTrue(Modifier.isFinal(EventRegistrationSystem.class.getModifiers()), "service final");
        Assertions.assertEquals(2, system.listEvents().size(), "events listed"); Assertions.assertEquals("E1", system.listEvents().get(0).getId(), "sorted");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> system.listEvents().clear(), "list immutable");
        Assertions.assertThrows(IllegalArgumentException.class, () -> system.createEvent(new Event("E1","Other",LocalDate.now(),"X",1)), "duplicate ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> system.createEvent(new Event("E3","alpha",LocalDate.of(2026,5,1),"TECHNOLOGY",2)), "duplicate definition");
        RegistrationSnapshot first = system.registerParticipant("E1", new Attendee("A","Person","a@b.com"));
        Assertions.assertEquals("E1-R001", first.getId(), "generated ID");
        Assertions.assertEquals(LocalDateTime.of(2026,1,2,3,4,5), first.getRegisteredAt(), "fixed clock");
        Assertions.assertThrows(IllegalArgumentException.class, () -> system.registerParticipant("E1", new Attendee("A","Person","a@b.com")), "duplicate");
        Assertions.assertThrows(IllegalStateException.class, () -> system.registerParticipant("E1", new Attendee("B","B","b@b.com")), "full");
        Assertions.assertEquals(1, system.findEvent("E1").getRegisteredCount(), "failure unchanged");
        system.registerParticipant("E2", new Attendee("A","Person","a@b.com")); Assertions.assertEquals(1, system.findEvent("E2").getRegisteredCount(), "same attendee other event");
        external.register(new Attendee("X","External","x@b.com")); Assertions.assertEquals(1, system.findEvent("E2").getRegisteredCount(), "input event copied");
        Assertions.assertEquals(1, system.searchEvents("ALP").size(), "name case insensitive"); Assertions.assertEquals(1, system.searchEventsByCategory("TECH").size(), "category search");
        Assertions.assertEquals(1, system.searchEventsByDate(LocalDate.of(2026,5,1)).size(), "date search");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> system.findEvent("E1").getRegistrations().clear(), "snapshot registrations immutable");
        system.cancelRegistrationByRegistrationId("E1", first.getId()); Assertions.assertEquals(1, system.findEvent("E1").getAvailableSpots(), "cancel by ID");
        Assertions.assertThrows(IllegalArgumentException.class, () -> system.cancelRegistrationByRegistrationId("E1", "bad"), "missing registration");
        Assertions.assertThrows(IllegalArgumentException.class, () -> system.findEvent("bad"), "missing event");
        Assertions.assertThrows(NullPointerException.class, () -> new EventRegistrationSystem(null), "null clock");
    }
}
