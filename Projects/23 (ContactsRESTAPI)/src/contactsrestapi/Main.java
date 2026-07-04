package contactsrestapi;

public class Main {
    public static void main(String[] args) {
        ContactService service =
                new ContactService(new InMemoryContactRepository());
        ContactHttpServer httpServer = new ContactHttpServer(service);
        // TODO: Demonstrate service calls and optionally enable server mode.
        System.out.println("Contacts REST API skeleton ready.");
        System.out.println("HTTP server was not started.");
    }
}
