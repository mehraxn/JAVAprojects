# URL Shortener Backend

## Status

Java service, file-storage, and built-in HttpServer skeleton created. Core logic is not implemented.

## Planned features

- Validate and shorten URLs.
- Generate collision-safe short codes.
- Resolve codes and count hits.
- Save and load mappings from a file.
- Expose local HTTP endpoints with Java HttpServer.

## Current classes

- UrlEntry: URL mapping model.
- CodeGenerator: short-code generation boundary.
- ShortenerService: in-memory application logic.
- FileUrlStore: planned standard-Java persistence.
- UrlShortenerHttpServer: optional HTTP adapter.
- Main: safe runner that does not start a server automatically.

## Constraints

Only standard Java is planned. HTTP uses com.sun.net.httpserver.HttpServer and response bodies will use manually created strings.

## Source layout

Source files are under src/urlshortenerbackend.
