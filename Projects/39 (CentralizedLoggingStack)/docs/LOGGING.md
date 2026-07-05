# Logging Contract

Initial fields:

- `timestamp`
- `level`
- `event`
- `message`

Rules before implementation:

- Never log passwords, tokens, secrets, or connection strings.
- Avoid personal data unless explicitly reviewed.
- Keep label cardinality bounded.
- Define retention and deletion behavior.
- Record parsing failures without exposing raw sensitive content.
