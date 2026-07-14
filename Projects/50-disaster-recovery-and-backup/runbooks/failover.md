# Failover Runbook

This project does not automate application traffic failover. The local Compose
lab verifies data recovery only.

For a future production runbook, define named decision owners and require these
gates: freeze unsafe writes, determine the failure domain, prevent split brain,
restore and validate data, approve traffic changes, monitor the recovered path,
retain a rollback option, plan failback, and preserve an incident timeline.

DNS, load-balancer, replication, and application commands must be specific to
the deployed environment; generic commands here would be unsafe.
