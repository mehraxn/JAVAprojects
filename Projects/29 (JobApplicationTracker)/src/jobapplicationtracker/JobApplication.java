package jobapplicationtracker;

import java.time.LocalDate;

public class JobApplication {
    public enum Stage {
        SAVED,
        APPLIED,
        INTERVIEW,
        OFFER,
        REJECTED,
        WITHDRAWN
    }

    private final long id;
    private String company;
    private String role;
    private LocalDate appliedDate;
    private LocalDate reminderDate;
    private Stage stage;

    public JobApplication(long id, String company, String role,
            LocalDate appliedDate, LocalDate reminderDate, Stage stage) {
        this.id = id;
        this.company = company;
        this.role = role;
        this.appliedDate = appliedDate;
        this.reminderDate = reminderDate;
        this.stage = stage;
    }

    public long getId() { return id; }
    public String getCompany() { return company; }
    public String getRole() { return role; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public LocalDate getReminderDate() { return reminderDate; }
    public Stage getStage() { return stage; }

    public void updateDetails(String company, String role,
            LocalDate appliedDate, LocalDate reminderDate) {
        // TODO: Validate all values before changing application details.
        throw new UnsupportedOperationException("TODO: update application details");
    }

    public void setStage(Stage stage) {
        // TODO: Validate and apply a legal stage transition.
        throw new UnsupportedOperationException("TODO: update application stage");
    }
}
