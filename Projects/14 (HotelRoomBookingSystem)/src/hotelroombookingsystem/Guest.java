package hotelroombookingsystem;

public final class Guest {
    private final String id;
    private final String name;

    public Guest(String id, String name) {
        this.id = requireText(id, "Guest ID");
        this.name = requireText(name, "Guest name");
    }

    public String getId() { return id; }
    public String getName() { return name; }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value.trim();
    }
}
