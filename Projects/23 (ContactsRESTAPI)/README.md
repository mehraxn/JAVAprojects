# Contacts REST API

A beginner/intermediate contact manager with in-memory storage and an optional REST-style interface using Java's built-in `HttpServer`. It uses no framework, database, JSON library, or external dependency.

## Features

- Create contacts with name, email, phone, and notes.
- Assign sequential contact IDs.
- List and retrieve contacts.
- Search names, emails, phone numbers, and notes.
- Apply simple offset/limit pagination.
- Update and delete contacts.
- Validate required names, optional email/phone formats, IDs, and pagination.
- Return manually generated JSON responses with safe escaping.
- Accept URL-encoded form bodies for create and update operations.

## Main classes

- `Contact` — validated contact model.
- `InMemoryContactRepository` — synchronized `LinkedHashMap` storage and defensive copies.
- `ContactService` — ID generation, CRUD operations, search, and pagination.
- `JsonUtil` — small response-only JSON serializer and escaper.
- `ContactHttpServer` — request routing and HTTP response handling.
- `Main` — console demonstration or explicit server launcher.

## In-memory storage

Contacts are keyed by ID in a `LinkedHashMap`, preserving creation order. The repository copies contacts when saving and reading so callers cannot mutate stored records directly. Repository methods are synchronized for HTTP use. Data exists only while the process runs; restarting the application starts with an empty contact list.

## HTTP endpoints

Start on the default port 8081:

```text
java -cp out contactsrestapi.Main server
```

Or choose a port:

```text
java -cp out contactsrestapi.Main server 8091
```

| Method and path | Behavior |
|---|---|
| `POST /contacts` | Create a contact; returns 201 and a `Location` header |
| `GET /contacts` | List contacts |
| `GET /contacts?q=text&offset=0&limit=20` | Search and paginate contacts |
| `GET /contacts/{id}` | Retrieve one contact |
| `PUT /contacts/{id}` | Replace the contact's editable fields |
| `DELETE /contacts/{id}` | Delete a contact; returns 204 |

POST and PUT bodies use `application/x-www-form-urlencoded` fields: `name`, `email`, `phone`, and `notes`. `name` is required. Email and phone may be empty, but nonempty values must pass their basic format checks. Responses use manually generated JSON.

Example:

```text
curl -i -X POST -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+123&notes=Developer" http://localhost:8081/contacts
curl "http://localhost:8081/contacts?q=ada&offset=0&limit=10"
curl -i -X PUT -d "name=Ada+Lovelace&email=ada%40example.com&phone=%2B49+456&notes=Updated" http://localhost:8081/contacts/C-1
curl -i -X DELETE http://localhost:8081/contacts/C-1
```

## Compile and run

```text
javac -d out src/contactsrestapi/*.java
java -cp out contactsrestapi.Main
```

The normal command runs a short service demonstration and does not start a server.

## Java concepts practiced

- Classes, encapsulation, collections, and defensive copies
- CRUD service and repository responsibilities
- Case-insensitive searching and pagination
- Synchronization for shared in-memory data
- Built-in HTTP routing, headers, methods, and status codes
- URL decoding and manual JSON generation

## Possible future improvements

- File persistence
- Partial updates with `PATCH`
- Stronger international phone/email validation
- Sorting and additional search fields
- Request JSON parsing
- Automated service and HTTP tests
