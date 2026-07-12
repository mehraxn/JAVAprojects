package eventregistrationsystem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Event {
    private final String id;
    private final String name;
    private final LocalDate date;
    private final String category;
    private final int capacity;
    private final Map<String, Registration> registrationsByAttendee = new LinkedHashMap<>();
    private int nextRegistrationNumber = 1;

    public Event(String id, String name, LocalDate date, String category, int capacity) {
        this.id = requireText(id, "Event ID");
        this.name = requireText(name, "Event name");
        if (date == null) {
            throw new IllegalArgumentException("Event date must not be null");
        }
        this.date = date;
        this.category = requireText(category, "Event category");
        if (capacity <= 0) {
            throw new IllegalArgumentException("Event capacity must be greater than zero");
        }
        this.capacity = capacity;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public int getCapacity() { return capacity; }

    public Registration register(Attendee attendee) {
        return register(attendee, LocalDateTime.now());
    }

    Registration register(Attendee attendee, LocalDateTime registeredAt) {
        if (attendee == null) {
            throw new IllegalArgumentException("Attendee must not be null");
        }
        if (registrationsByAttendee.containsKey(attendee.getId())) {
            throw new IllegalArgumentException("Attendee is already registered: " + attendee.getId());
        }
        if (isFull()) {
            throw new IllegalStateException("Event is full: " + id);
        }

        String registrationId = id + "-R" + String.format("%03d", nextRegistrationNumber++);
        Registration registration = new Registration(
                registrationId, id, attendee, registeredAt);
        registrationsByAttendee.put(attendee.getId(), registration);
        return registration;
    }

    public void cancelRegistration(String attendeeId) {
        String validId = requireText(attendeeId, "Attendee ID");
        if (registrationsByAttendee.remove(validId) == null) {
            throw new IllegalArgumentException("Attendee is not registered: " + validId);
        }
    }

    public void cancelRegistrationByRegistrationId(String registrationId) {
        String validId = requireText(registrationId, "Registration ID");
        String attendeeId = null;
        for (Map.Entry<String, Registration> entry : registrationsByAttendee.entrySet()) {
            if (entry.getValue().getId().equals(validId)) {
                attendeeId = entry.getKey();
                break;
            }
        }
        if (attendeeId == null) {
            throw new IllegalArgumentException("Registration does not exist: " + validId);
        }
        registrationsByAttendee.remove(attendeeId);
    }

    public boolean isFull() {
        return registrationsByAttendee.size() >= capacity;
    }

    public int getAvailablePlaces() {
        return capacity - registrationsByAttendee.size();
    }

    public int getRegisteredCount() {
        return registrationsByAttendee.size();
    }

    public List<Attendee> getParticipants() {
        List<Attendee> participants = new ArrayList<>();
        for (Registration registration : registrationsByAttendee.values()) {
            participants.add(registration.getAttendee());
        }
        return Collections.unmodifiableList(participants);
    }

    public List<Registration> getRegistrations() {
        return Collections.unmodifiableList(
                new ArrayList<>(registrationsByAttendee.values()));
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
