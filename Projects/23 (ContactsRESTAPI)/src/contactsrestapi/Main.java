package contactsrestapi;

public class Main {
    public static void main(String[] args) {
        ContactService service = new ContactService(new InMemoryContactRepository());

        if (args.length > 0 && "server".equalsIgnoreCase(args[0])) {
            startServer(service, args);
            return;
        }

        Contact first = service.createContact(
                "Ada Lovelace", "ada@example.com", "+49 123 4567", "Programming contact");
        service.createContact(
                "Grace Hopper", "grace@example.com", "+49 987 6543", "Compiler specialist");
        service.updateContact(first.getId(), first.getName(), first.getEmail(),
                first.getPhone(), "Updated notes");

        System.out.println("All contacts:");
        for (Contact contact : service.listContacts()) {
            System.out.println(contact);
        }
        System.out.println("Search for 'compiler': " + service.search("compiler", 0, 10));
        System.out.println("Run with 'server [port]' to start the REST-style interface.");
    }

    private static void startServer(ContactService service, String[] args) {
        int port = 8081;
        try {
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
            ContactHttpServer server = new ContactHttpServer(service);
            server.start(port);
            System.out.println("Contacts API listening on http://localhost:" + port + "/contacts");
            System.out.println("Stop the process with Ctrl+C.");
        } catch (NumberFormatException exception) {
            System.err.println("Port must be a number.");
        } catch (Exception exception) {
            System.err.println("Could not start server: " + exception.getMessage());
        }
    }
}
