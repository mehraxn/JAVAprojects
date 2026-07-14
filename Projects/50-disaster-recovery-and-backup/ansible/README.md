# Ansible extension point

Ansible is not required by the local Docker Compose lab. A future implementation
could prepare a dedicated recovery host, install PostgreSQL client tools,
retrieve credentials from an external secret manager, schedule restore drills,
and report validation results.

No remote host is configured in this repository. The executable local workflow
is in `../scripts/`.
