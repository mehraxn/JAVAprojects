package eventregistrationsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Immutable public view that prevents callers from changing an event's registrations. */
public final class EventSnapshot {
    private final String id;
    private final String name;
    private final LocalDate date;
    private final String category;
    private final int capacity;
    private final List<RegistrationSnapshot> registrations;

    EventSnapshot(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.date = event.getDate();
        this.category = event.getCategory();
        this.capacity = event.getCapacity();
        List<RegistrationSnapshot> copies = new ArrayList<>();
        for (Registration registration : event.getRegistrations()) {
            copies.add(new RegistrationSnapshot(registration));
        }
        this.registrations = Collections.unmodifiableList(copies);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public int getCapacity() { return capacity; }
    public int getRegisteredCount() { return registrations.size(); }
    public int getAvailableSpots() { return capacity - registrations.size(); }
    public List<RegistrationSnapshot> getRegistrations() { return registrations; }
}
