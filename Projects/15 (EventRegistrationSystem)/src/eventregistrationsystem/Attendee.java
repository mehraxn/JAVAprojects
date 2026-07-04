package eventregistrationsystem;

public class Attendee {
    private final String id;
    private final String name;
    private final String email;

    public Attendee(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
