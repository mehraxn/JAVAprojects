import java.util.HashMap;
import java.util.Map;

// ===============================
// Generic Interface
// ===============================
interface Repository<T> {
    void add(T item);
    T get(int id);
    void remove(int id);
}

// ===============================
// User Class
// ===============================
class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "'}";
    }
}

// ===============================
// Repository Implementation (specific for User)
// ===============================
class UserRepository implements Repository<User> {

    private Map<Integer, User> users = new HashMap<>();

    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User get(int id) {
        return users.get(id);
    }

    @Override
    public void remove(int id) {
        users.remove(id);
    }
}

// ===============================
// Fully Generic Repository Implementation
// ===============================
class GenericRepository<T> implements Repository<T> {

    private Map<Integer, T> storage = new HashMap<>();

    @Override
    public void add(T item) {
        storage.put(storage.size() + 1, item);
    }

    @Override
    public T get(int id) {
        return storage.get(id);
    }

    @Override
    public void remove(int id) {
        storage.remove(id);
    }
}

// ===============================
// Main Class - Test Everything
// ===============================
public class Main {
    public static void main(String[] args) {

        System.out.println("===== Testing UserRepository =====");

        Repository<User> userRepo = new UserRepository();
        userRepo.add(new User(1, "Alice"));
        userRepo.add(new User(2, "Bob"));

        System.out.println(userRepo.get(1));   // Alice
        System.out.println(userRepo.get(2));   // Bob

        userRepo.remove(2);
        System.out.println(userRepo.get(2));   // null


        System.out.println("\n===== Testing GenericRepository =====");

        Repository<String> stringRepo = new GenericRepository<>();
        stringRepo.add("Hello");
        stringRepo.add("World");
        System.out.println(stringRepo.get(1)); // Hello
        System.out.println(stringRepo.get(2)); // World

        Repository<Integer> intRepo = new GenericRepository<>();
        intRepo.add(100);
        intRepo.add(200);
        System.out.println(intRepo.get(1)); // 100
        System.out.println(intRepo.get(2)); // 200
    }
}
