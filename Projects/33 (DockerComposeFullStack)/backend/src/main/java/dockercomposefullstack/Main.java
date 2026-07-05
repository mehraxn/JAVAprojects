package dockercomposefullstack;

public class Main {
    public static void main(String[] args) throws Exception {
        AppConfig config = AppConfig.fromEnvironment();
        NoteRepository repository = new NoteRepository(config);
        ApiServer server = new ApiServer(config.getPort(), repository);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
        System.out.println("Backend listening on port " + config.getPort());
    }
}
