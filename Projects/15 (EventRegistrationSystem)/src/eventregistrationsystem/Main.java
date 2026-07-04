package eventregistrationsystem;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        EventRegistrationSystem system = new EventRegistrationSystem();
        Event workshop = new Event(
                "E001", "Java Collections Workshop",
                LocalDate.of(2026, 9, 15), "Technology", 2);
        system.createEvent(workshop);

        Registration registration = system.registerParticipant(
                "E001", new Attendee("A001", "Lea", "lea@example.com"));
        system.registerParticipant(
                "E001", new Attendee("A002", "Omar", "omar@example.com"));

        System.out.println("Created registration: " + registration.getId());
        System.out.println("Participants: " + system.getParticipants("E001").size());
        System.out.println("Technology events: "
                + system.searchEventsByCategory("tech").size());

        system.cancelRegistration("E001", "A001");
        System.out.println("Available places after cancellation: "
                + workshop.getAvailablePlaces());
    }
}
