# Testing Centralized Logging Stack

## Static checks

- Confirm the log schema excludes secrets and personal data.
- Review collector paths, labels, retention, and volume ownership.
- Confirm credentials and hosts are placeholders.

## Deferred checks

- Compile the structured logger.
- Generate safe sample logs.
- Validate collector and store configuration.
- Start the local stack and verify ingestion and search.

No logging, container, or network command was executed.
