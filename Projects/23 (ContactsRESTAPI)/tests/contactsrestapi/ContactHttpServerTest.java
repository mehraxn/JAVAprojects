package contactsrestapi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class ContactHttpServerTest {
    private ContactHttpServerTest() {
    }

    static void run(Assert t) throws Exception {
        t.assertThrows(IllegalArgumentException.class,
                () -> new ContactHttpServer(null), "null service rejected");

        ContactHttpServer invalid = new ContactHttpServer(
                new ContactService(new InMemoryContactRepository()));
        t.assertThrows(IllegalArgumentException.class,
                () -> invalid.start(-1), "negative port rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> invalid.start(70_000), "port above 65535 rejected");
        t.assertThrows(IllegalStateException.class,
                invalid::getPort, "getPort before start rejected");

        ContactService service = new ContactService(new InMemoryContactRepository());
        ContactHttpServer server = new ContactHttpServer(service);
        server.start(0);
        try {
            int port = server.getPort();
            t.assertTrue(port > 0, "server picked a free port");
            t.assertThrows(IllegalStateException.class,
                    () -> server.start(0), "second start on running server rejected");

            HttpClient client = HttpClient.newHttpClient();
            String base = "http://127.0.0.1:" + port;

            // POST /contacts -> 201 + Location
            HttpResponse<String> created = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+123&notes=Dev"))
                    .build());
            t.assertEquals(201, created.statusCode(), "POST /contacts returns 201");
            t.assertContains(contentType(created), "application/json",
                    "POST response is JSON");
            t.assertContains(created.body(), "\"id\":\"C-1\"", "created contact has ID C-1");
            t.assertContains(created.body(), "\"name\":\"Ada Lovelace\"",
                    "created contact echoes the decoded name");
            t.assertEquals("/contacts/C-1",
                    created.headers().firstValue("Location").orElse(""),
                    "POST sets a Location header");

            // GET /contacts -> 200 array
            HttpResponse<String> list = send(client, get(base + "/contacts"));
            t.assertEquals(200, list.statusCode(), "GET /contacts returns 200");
            t.assertContains(contentType(list), "application/json", "list response is JSON");
            t.assertTrue(list.body().startsWith("[") && list.body().endsWith("]"),
                    "list response is a JSON array");

            // GET /contacts/{id}
            HttpResponse<String> one = send(client, get(base + "/contacts/C-1"));
            t.assertEquals(200, one.statusCode(), "GET /contacts/C-1 returns 200");
            t.assertContains(one.body(), "ada@example.com", "single contact JSON has email");
            HttpResponse<String> missing = send(client, get(base + "/contacts/C-99"));
            t.assertEquals(404, missing.statusCode(), "GET missing contact returns 404");
            t.assertContains(missing.body(), "\"error\":\"Contact not found.\"",
                    "missing contact returns error JSON");

            // Search and pagination
            send(client, HttpRequest.newBuilder(URI.create(base + "/contacts"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "name=Grace+Hopper&email=grace%40example.com&notes=Compiler"))
                    .build());
            HttpResponse<String> search = send(client,
                    get(base + "/contacts?q=compiler&offset=0&limit=10"));
            t.assertEquals(200, search.statusCode(), "search returns 200");
            t.assertContains(search.body(), "Grace Hopper", "search finds matching contact");
            t.assertFalse(search.body().contains("Ada"), "search filters non-matches");

            // Invalid pagination -> 400 JSON
            HttpResponse<String> badLimit = send(client, get(base + "/contacts?limit=abc"));
            t.assertEquals(400, badLimit.statusCode(), "non-numeric limit returns 400");
            t.assertContains(badLimit.body(), "\"error\":", "pagination error is JSON");
            t.assertEquals(400, send(client, get(base + "/contacts?limit=0")).statusCode(),
                    "zero limit returns 400");
            t.assertEquals(400, send(client, get(base + "/contacts?offset=-1")).statusCode(),
                    "negative offset returns 400");

            // PUT /contacts/{id} -> 200
            HttpResponse<String> updated = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts/C-1"))
                    .PUT(HttpRequest.BodyPublishers.ofString(
                            "name=Ada+Lovelace&email=ada%40example.com&notes=Updated"))
                    .build());
            t.assertEquals(200, updated.statusCode(), "PUT /contacts/C-1 returns 200");
            t.assertContains(updated.body(), "\"notes\":\"Updated\"", "PUT stores new notes");
            t.assertEquals(404, send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts/C-99"))
                    .PUT(HttpRequest.BodyPublishers.ofString("name=Ghost"))
                    .build()).statusCode(), "PUT missing contact returns 404");

            // Validation errors -> 400 JSON
            HttpResponse<String> noName = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts"))
                    .POST(HttpRequest.BodyPublishers.ofString("email=x%40example.com"))
                    .build());
            t.assertEquals(400, noName.statusCode(), "POST without name returns 400");
            t.assertContains(noName.body(), "\"error\":", "validation error is JSON");
            HttpResponse<String> duplicateField = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts"))
                    .POST(HttpRequest.BodyPublishers.ofString("name=A&name=B"))
                    .build());
            t.assertEquals(400, duplicateField.statusCode(),
                    "duplicate form field returns 400");
            t.assertContains(duplicateField.body(), "Duplicate request field",
                    "duplicate field error is explicit");

            // Oversized request -> 413 JSON
            byte[] big = new byte[70_000];
            java.util.Arrays.fill(big, (byte) 'a');
            HttpResponse<String> tooLarge = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts"))
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "name=" + new String(big, StandardCharsets.UTF_8)))
                    .build());
            t.assertEquals(413, tooLarge.statusCode(), "oversized request returns 413");
            t.assertContains(tooLarge.body(), "\"error\":\"Request body is too large.\"",
                    "oversized request error is JSON");

            // Method not allowed -> 405 JSON with Allow header
            HttpResponse<String> patch = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts"))
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build());
            t.assertEquals(405, patch.statusCode(), "PATCH /contacts returns 405");
            t.assertContains(patch.body(), "\"error\":\"Method not allowed.\"",
                    "405 body is JSON");
            t.assertEquals("GET, POST", patch.headers().firstValue("Allow").orElse(""),
                    "collection 405 sets Allow header");
            HttpResponse<String> postItem = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts/C-1"))
                    .POST(HttpRequest.BodyPublishers.ofString("name=X"))
                    .build());
            t.assertEquals(405, postItem.statusCode(), "POST /contacts/C-1 returns 405");
            t.assertEquals("GET, PUT, DELETE",
                    postItem.headers().firstValue("Allow").orElse(""),
                    "item 405 sets Allow header");

            // Unknown routes -> JSON 404, never the default HTML page
            for (String path : new String[] {
                    "/unknown", "/", "/api/contacts", "/contacts-extra", "/contacts/C-1/extra"}) {
                HttpResponse<String> unknown = send(client, get(base + path));
                t.assertEquals(404, unknown.statusCode(), "GET " + path + " returns 404");
                t.assertContains(contentType(unknown), "application/json",
                        "GET " + path + " 404 is JSON, not HTML");
                t.assertContains(unknown.body(), "\"error\":\"Endpoint not found.\"",
                        "GET " + path + " returns endpoint-not-found JSON");
            }

            // DELETE /contacts/{id} -> 204, then GET -> 404
            HttpResponse<String> deleted = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts/C-1")).DELETE().build());
            t.assertEquals(204, deleted.statusCode(), "DELETE /contacts/C-1 returns 204");
            t.assertEquals("", deleted.body(), "204 response has no body");
            t.assertEquals(404, send(client, get(base + "/contacts/C-1")).statusCode(),
                    "GET after delete returns 404");
            t.assertEquals(404, send(client, HttpRequest
                    .newBuilder(URI.create(base + "/contacts/C-1")).DELETE().build())
                    .statusCode(), "second DELETE returns 404");
        } finally {
            server.stop();
        }
        server.stop();
        t.assertTrue(true, "stop twice is a safe no-op");
    }

    private static HttpRequest get(String url) {
        return HttpRequest.newBuilder(URI.create(url)).GET().build();
    }

    private static HttpResponse<String> send(HttpClient client, HttpRequest request)
            throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String contentType(HttpResponse<String> response) {
        return response.headers().firstValue("Content-Type").orElse("");
    }
}
