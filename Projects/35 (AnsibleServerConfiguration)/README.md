# Ansible Server Configuration

Safe Ansible starter demonstrating inventory, playbooks, roles, defaults, handlers, and templates with documentation-only hosts.

## Structure

```text
ansible/ansible.cfg
ansible/inventory/example.ini
ansible/playbooks/site.yml
ansible/roles/java_app/defaults/main.yml
ansible/roles/java_app/tasks/main.yml
ansible/roles/java_app/handlers/main.yml
ansible/roles/java_app/templates/application.service.j2
docs/SAFETY.md
TESTING.md
```

## Safety

The inventory uses a reserved documentation address and the role performs only a debug placeholder task. No host was contacted and no playbook was run.

## Next implementation steps

- Confirm the target operating system and package manager.
- Confirm application user, paths, artifact source, and service manager.
- Add idempotent tasks one responsibility at a time.
