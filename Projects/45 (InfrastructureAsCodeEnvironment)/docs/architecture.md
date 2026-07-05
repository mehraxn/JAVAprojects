# Architecture

Planned flow: Terraform produces reviewed infrastructure metadata, then an explicit handoff supplies non-sensitive host information to Ansible. State and credentials remain outside source control.

TODO: define target, modules, remote-state controls, inventory generation, approval gates, teardown, and recovery.
