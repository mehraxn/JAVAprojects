# Contacts REST API

## Status

Model, repository, service, manual JSON, and built-in HttpServer skeleton created.

## Planned features

- Create, read, update, and delete contacts.
- Search contacts.
- Apply offset/limit pagination.
- Validate identifiers and contact fields.
- Return manually generated JSON responses.
- Expose endpoints with Java HttpServer.

## Current classes

- Contact: contact model.
- InMemoryContactRepository: temporary storage boundary.
- ContactService: application and validation logic.
- JsonUtil: small manual JSON utility.
- ContactHttpServer: HTTP adapter.
- Main: safe runner that does not start the server automatically.

## Constraints

No JSON library is used. JsonUtil will support only the documented contact request and response shapes.

## Source layout

Source files are under src/contactsrestapi.
