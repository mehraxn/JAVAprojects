package urlshortenerbackend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class UrlShortenerHttpServerTest {
    private UrlShortenerHttpServerTest() {
    }

    static void run(Assert t) throws Exception {
        t.assertThrows(IllegalArgumentException.class,
                () -> new UrlShortenerHttpServer(null), "null service rejected");

        UrlShortenerHttpServer invalid = new UrlShortenerHttpServer(
                new ShortenerService(new CodeGenerator()));
        t.assertThrows(IllegalArgumentException.class,
                () -> invalid.start(-1), "negative port rejected");
        t.assertThrows(IllegalArgumentException.class,
                () -> invalid.start(70_000), "port above 65535 rejected");
        t.assertThrows(IllegalStateException.class,
                invalid::getPort, "getPort before start rejected");

        ShortenerService service = new ShortenerService(new CodeGenerator());
        UrlShortenerHttpServer server = new UrlShortenerHttpServer(service);
        server.start(0);
        try {
            int port = server.getPort();
            t.assertTrue(port > 0, "server picked a free port");
            t.assertThrows(IllegalStateException.class,
                    () -> server.start(0), "second start on running server rejected");

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NEVER)
                    .build();
            String base = "http://127.0.0.1:" + port;

            // POST /links (generated code) -> 201 JSON
            HttpResponse<String> created = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fexample.com%2Fdocs%2Fjava"));
            t.assertEquals(201, created.statusCode(), "POST /links returns 201");
            t.assertContains(contentType(created), "application/json",
                    "POST response is JSON");
            t.assertContains(created.body(), "\"originalUrl\":\"https://example.com/docs/java\"",
                    "created link echoes the decoded URL");
            t.assertContains(created.body(), "\"hitCount\":0", "new link starts with 0 hits");

            // POST /links with custom code -> 201
            HttpResponse<String> custom = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fopenjdk.org%2F&code=openjdk"));
            t.assertEquals(201, custom.statusCode(), "POST with custom code returns 201");
            t.assertContains(custom.body(), "\"shortCode\":\"openjdk\"",
                    "custom code is preserved");

            // Duplicate custom code -> 409 JSON
            HttpResponse<String> duplicate = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fexample.com%2Fother&code=openjdk"));
            t.assertEquals(409, duplicate.statusCode(), "duplicate custom code returns 409");
            t.assertContains(duplicate.body(), "\"error\":\"Custom code already exists.\"",
                    "duplicate code error is JSON");

            // Invalid input -> 400 JSON
            HttpResponse<String> badUrl = send(client, post(base + "/links",
                    "url=ftp%3A%2F%2Fexample.com%2Ffile"));
            t.assertEquals(400, badUrl.statusCode(), "ftp URL returns 400");
            t.assertContains(badUrl.body(), "\"error\":", "invalid URL error is JSON");
            t.assertEquals(400, send(client, post(base + "/links", "code=abc")).statusCode(),
                    "missing url field returns 400");
            t.assertEquals(400, send(client, post(base + "/links",
                    "url=https%3A%2F%2Fexample.com&code=a")).statusCode(),
                    "invalid custom code returns 400");
            HttpResponse<String> duplicateField = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fa.com&url=https%3A%2F%2Fb.com"));
            t.assertEquals(400, duplicateField.statusCode(),
                    "duplicate form field returns 400");
            t.assertContains(duplicateField.body(), "Duplicate form field",
                    "duplicate field error is explicit");

            // Oversized request -> 413 JSON
            byte[] big = new byte[70_000];
            java.util.Arrays.fill(big, (byte) 'a');
            HttpResponse<String> tooLarge = send(client, post(base + "/links",
                    "url=" + new String(big, StandardCharsets.UTF_8)));
            t.assertEquals(413, tooLarge.statusCode(), "oversized request returns 413");
            t.assertContains(tooLarge.body(), "\"error\":\"Request body is too large.\"",
                    "oversized request error is JSON");

            // GET /links -> 200 JSON array
            HttpResponse<String> list = send(client, get(base + "/links"));
            t.assertEquals(200, list.statusCode(), "GET /links returns 200");
            t.assertContains(contentType(list), "application/json", "list response is JSON");
            t.assertTrue(list.body().startsWith("[") && list.body().endsWith("]"),
                    "list response is a JSON array");
            t.assertContains(list.body(), "\"shortCode\":\"openjdk\"",
                    "list contains the custom link");

            // GET /r/{code} -> 302 redirect + hit count increments
            HttpResponse<String> redirect = send(client, get(base + "/r/openjdk"));
            t.assertEquals(302, redirect.statusCode(), "GET /r/openjdk returns 302");
            t.assertEquals("https://openjdk.org/",
                    redirect.headers().firstValue("Location").orElse(""),
                    "redirect Location header holds the original URL");
            send(client, get(base + "/r/openjdk"));
            t.assertEquals(2L, service.find("openjdk").getHitCount(),
                    "each redirect increments the hit count");

            // Failed redirects -> JSON 404/400, no hit count changes
            HttpResponse<String> missing = send(client, get(base + "/r/nosuch"));
            t.assertEquals(404, missing.statusCode(), "GET /r/nosuch returns 404");
            t.assertContains(contentType(missing), "application/json",
                    "missing code 404 is JSON");
            t.assertEquals(404, send(client, get(base + "/r/")).statusCode(),
                    "GET /r/ without code returns 404");
            t.assertEquals(400, send(client, get(base + "/r/ab")).statusCode(),
                    "GET /r/{too-short-code} returns 400");
            t.assertEquals(2L, service.find("openjdk").getHitCount(),
                    "failed redirects do not change hit counts");

            // Method not allowed -> 405 JSON with Allow header
            HttpResponse<String> putLinks = send(client, HttpRequest
                    .newBuilder(URI.create(base + "/links"))
                    .PUT(HttpRequest.BodyPublishers.ofString("url=https%3A%2F%2Fx.com"))
                    .build());
            t.assertEquals(405, putLinks.statusCode(), "PUT /links returns 405");
            t.assertContains(putLinks.body(), "\"error\":\"Method not allowed.\"",
                    "405 body is JSON");
            t.assertEquals("GET, POST", putLinks.headers().firstValue("Allow").orElse(""),
                    "/links 405 sets Allow header");
            t.assertEquals(405, send(client, HttpRequest
                    .newBuilder(URI.create(base + "/links")).DELETE().build()).statusCode(),
                    "DELETE /links returns 405");
            HttpResponse<String> postRedirect = send(client, post(base + "/r/openjdk", ""));
            t.assertEquals(405, postRedirect.statusCode(), "POST /r/{code} returns 405");
            t.assertEquals("GET", postRedirect.headers().firstValue("Allow").orElse(""),
                    "/r 405 sets Allow header");

            // Unknown routes -> JSON 404, never the default HTML page
            for (String path : new String[] {
                    "/unknown", "/", "/api/links", "/links-extra",
                    "/links/openjdk", "/r-extra", "/r/abc/extra"}) {
                HttpResponse<String> unknown = send(client, get(base + path));
                t.assertEquals(404, unknown.statusCode(), "GET " + path + " returns 404");
                t.assertContains(contentType(unknown), "application/json",
                        "GET " + path + " 404 is JSON, not HTML");
                t.assertContains(unknown.body(), "\"error\":\"Endpoint not found.\"",
                        "GET " + path + " returns endpoint-not-found JSON");
            }

            // Raw quotes are not legal in URLs and are rejected with JSON 400 ...
            HttpResponse<String> rawQuote = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fexample.com%2Fsearch%3Fq%3D%22quoted%22"));
            t.assertEquals(400, rawQuote.statusCode(),
                    "URL with raw quotes is rejected as malformed");
            t.assertContains(rawQuote.body(), "\"error\":", "malformed URL error is JSON");
            // ... while the percent-encoded form is accepted and round-trips literally
            HttpResponse<String> encoded = send(client, post(base + "/links",
                    "url=https%3A%2F%2Fexample.com%2Fsearch%3Fq%3D%2522quoted%2522&code=quoted1"));
            t.assertEquals(201, encoded.statusCode(), "percent-encoded URL is accepted");
            t.assertContains(encoded.body(), "q=%22quoted%22",
                    "percent-encoded characters are preserved in JSON output");
        } finally {
            server.stop();
        }
        server.stop();
        t.assertTrue(true, "stop twice is a safe no-op");
    }

    private static HttpRequest get(String url) {
        return HttpRequest.newBuilder(URI.create(url)).GET().build();
    }

    private static HttpRequest post(String url, String body) {
        return HttpRequest.newBuilder(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static HttpResponse<String> send(HttpClient client, HttpRequest request)
            throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String contentType(HttpResponse<String> response) {
        return response.headers().firstValue("Content-Type").orElse("");
    }
}
