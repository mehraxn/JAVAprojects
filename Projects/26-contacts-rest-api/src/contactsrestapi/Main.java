package contactsrestapi;

import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        int exitCode = run(args, System.out, System.err);
        System.exit(exitCode);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        String command = args.length == 0 ? "help" : args[0].toLowerCase(java.util.Locale.ROOT);
        switch (command) {
            case "help":
                printUsage(out);
                return 0;
            case "demo":
            case "service-demo":
                runDemo(out);
                return 0;
            case "http-demo":
                printHttpDemo(out);
                return 0;
            case "server":
                return runServer(args, out, err);
            default:
                err.println("Unknown command: " + args[0]);
                printUsage(err);
                return 1;
        }
    }

    private static void printUsage(PrintStream out) {
        out.println("Contacts REST API - educational Java HttpServer project");
        out.println();
        out.println("Usage: java -cp out contactsrestapi.Main <command>");
        out.println();
        out.println("Commands:");
        out.println("  help            Show this usage text.");
        out.println("  demo            Run the service-layer CRUD/search/pagination demo.");
        out.println("  service-demo    Alias for demo.");
        out.println("  http-demo       Print example curl commands for the HTTP API.");
        out.println("  server <port>   Start the HTTP server on the given port (e.g. 8082).");
    }

    private static void runDemo(PrintStream out) {
        ContactService service = new ContactService(new InMemoryContactRepository());

        out.println("== Create ==");
        Contact ada = service.createContact(
                "Ada Lovelace", "ada@example.com", "+49 123 4567", "Programming contact");
        Contact grace = service.createContact(
                "Grace Hopper", "grace@example.com", "+49 987 6543", "Compiler specialist");
        Contact alan = service.createContact(
                "Alan Turing", "alan@example.com", "+44 555 0000", "Cryptography");
        out.println("Created: " + ada);
        out.println("Created: " + grace);
        out.println("Created: " + alan);

        out.println("== List ==");
        for (Contact contact : service.listContacts()) {
            out.println(contact);
        }

        out.println("== Search 'compiler' ==");
        for (Contact contact : service.search("compiler", 0, 10)) {
            out.println(contact);
        }

        out.println("== Update " + ada.getId() + " ==");
        Contact updated = service.updateContact(ada.getId(), ada.getName(),
                ada.getEmail(), ada.getPhone(), "Updated notes");
        out.println("Updated: " + updated + " | notes: " + updated.getNotes());

        out.println("== Pagination (offset=1, limit=2) ==");
        List<Contact> page = service.search("", 1, 2);
        for (Contact contact : page) {
            out.println(contact);
        }

        out.println("== Delete " + alan.getId() + " ==");
        out.println("Deleted: " + service.deleteContact(alan.getId()));
        out.println("Remaining contacts: " + service.listContacts().size());
    }

    private static void printHttpDemo(PrintStream out) {
        out.println("Start the server first:");
        out.println("  java -cp out contactsrestapi.Main server 8082");
        out.println();
        out.println("Then try these curl commands:");
        out.println("  curl -i -X POST -d \"name=Ada+Lovelace&email=ada%40example.com"
                + "&phone=%2B49+123&notes=Developer\" http://localhost:8082/contacts");
        out.println("  curl -i http://localhost:8082/contacts");
        out.println("  curl -i \"http://localhost:8082/contacts?q=ada&offset=0&limit=10\"");
        out.println("  curl -i http://localhost:8082/contacts/C-1");
        out.println("  curl -i -X PUT -d \"name=Ada+Lovelace&email=ada%40example.com"
                + "&phone=%2B49+456&notes=Updated\" http://localhost:8082/contacts/C-1");
        out.println("  curl -i -X DELETE http://localhost:8082/contacts/C-1");
        out.println("  curl -i http://localhost:8082/unknown   (returns JSON 404)");
    }

    private static int runServer(String[] args, PrintStream out, PrintStream err) {
        if (args.length < 2) {
            err.println("Missing port. Usage: java -cp out contactsrestapi.Main server <port>");
            return 1;
        }
        int port;
        try {
            port = Integer.parseInt(args[1].trim());
        } catch (NumberFormatException exception) {
            err.println("Port must be a number, got: " + args[1]);
            return 1;
        }
        ContactService service = new ContactService(new InMemoryContactRepository());
        ContactHttpServer server = new ContactHttpServer(service);
        try {
            server.start(port);
        } catch (Exception exception) {
            err.println("Could not start server: " + exception.getMessage());
            return 1;
        }
        out.println("Contacts API listening on http://localhost:" + server.getPort() + "/contacts");
        out.println("Stop the process with Ctrl+C.");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            server.stop();
        }
        return 0;
    }
}
