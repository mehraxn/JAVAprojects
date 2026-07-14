package jobapplicationtracker;

import java.time.LocalDate;

/**
 * Validated job application record. The class is final so the constructor may
 * safely reuse the setter validation logic without a 'this' escape warning
 * under {@code javac -Xlint:all -Werror}.
 */
public final class JobApplication {
    public enum Status {
        APPLIED,
        SCREENING,
        INTERVIEW,
        OFFER,
        REJECTED,
        WITHDRAWN
    }

    private final long id;
    private final String company;
    private final String role;
    private final LocalDate applicationDate;
    private Status status;
    private String notes;

    public JobApplication(long id, String company, String role,
            LocalDate applicationDate, Status status, String notes) {
        if (id <= 0) {
            throw new IllegalArgumentException("Application ID must be positive.");
        }
        this.id = id;
        this.company = requireText(company, "Company");
        this.role = requireText(role, "Role");
        if (applicationDate == null) {
            throw new IllegalArgumentException("Application date cannot be null.");
        }
        if (applicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Application date cannot be in the future.");
        }
        this.applicationDate = applicationDate;
        setStatus(status);
        setNotes(notes);
    }

    public long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public Status getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setStatus(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Application status cannot be null.");
        }
        this.status = status;
    }

    public void setNotes(String notes) {
        String safeNotes = notes == null ? "" : notes.trim();
        if (safeNotes.contains("\n") || safeNotes.contains("\r")) {
            throw new IllegalArgumentException("Notes cannot contain line breaks.");
        }
        this.notes = safeNotes;
    }

    public JobApplication copy() {
        return new JobApplication(id, company, role, applicationDate, status, notes);
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        String result = value.trim();
        if (result.contains("\n") || result.contains("\r")) {
            throw new IllegalArgumentException(fieldName + " cannot contain line breaks.");
        }
        return result;
    }

    @Override
    public String toString() {
        return "#" + id + " " + company + " - " + role + " [" + status + ", " + applicationDate + "]";
    }
}
