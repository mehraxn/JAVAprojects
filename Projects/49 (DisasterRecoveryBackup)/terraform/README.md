# Terraform extension point

Terraform is not required by the local Docker Compose lab. In a production-style
extension, this directory could provision versioned and encrypted object storage,
retention or object-lock policies, least-privilege identities, and backup-age
monitoring.

No cloud resources are currently declared because a useful implementation needs
a chosen provider, account, region, retention policy, and cost review. Adding a
fake resource would not demonstrate a working recovery path.
