# State and Secrets

Terraform state can contain sensitive values even when outputs are hidden. Ansible inventory and variable files can expose hosts and credentials. Neither state nor real credentials belongs in this starter.

TODO: document encrypted state, locking, access, backup, secret injection, rotation, and audit ownership.
