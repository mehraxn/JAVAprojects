# Testing Ansible Server Configuration

No Ansible command was run and no host was contacted. All command-based and remote checks remain unverified.

## Static safety checks

- [x] The tracked inventory uses only `server.example.invalid`.
- [x] The SSH username and key path are obvious placeholders.
- [x] No password, private key, token, or real host address is stored.
- [x] The real `inventory.ini`, private-key extensions, and vault-password files are ignored.
- [x] The application user is non-root and has a non-login shell.
- [x] File ownership and modes are explicit.
- [x] The sample service does not bind a network port.

These checks are source inspection, not Ansible validation.

## Deferred local checks

After Ansible is deliberately installed, from the `ansible/` directory:

- [ ] Copy `inventory.ini.example` to ignored `inventory.ini` and use only an approved disposable host.
- [ ] Run `ansible-inventory --graph` and confirm only the intended lab host appears.
- [ ] Run `ansible-playbook playbook.yml --syntax-check`.
- [ ] Run `ansible-playbook playbook.yml --list-hosts`.
- [ ] Run `ansible-playbook playbook.yml --list-tasks`.
- [ ] Review every resolved variable and task before allowing a connection.

None of these commands were executed during implementation.

## Deferred check-mode cases

| Case | Expected result |
|---|---|
| Documentation inventory unchanged | Connection fails safely because `example.invalid` cannot resolve |
| Approved Linux systemd lab | Platform assertion passes |
| Non-Linux or non-systemd target | Playbook stops at the platform assertion |
| `app_user` set to `root` | Common role validation fails |
| Empty package list | Common role validation fails |
| Missing service/path variable | App role validation fails |
| First check-mode review | Proposed package, account, directory, file, and service changes are visible |

Check mode is predictive and may not fully simulate package installation, user creation, or service startup.

## Deferred execution checks

- [ ] Confirm packages are present without requesting newer versions unnecessarily.
- [ ] Confirm the system user cannot log in interactively.
- [ ] Confirm all directories have the documented owner, group, and mode.
- [ ] Confirm `application.conf` is mode `0640`.
- [ ] Confirm the copied demo process is executable but not writable by the service user.
- [ ] Confirm the systemd unit is enabled and active.
- [ ] Confirm changing app configuration notifies exactly one restart handler.
- [ ] Confirm unchanged app files do not trigger a restart.

## Idempotency test

On an approved disposable host only:

1. Review check mode and then run the playbook once.
2. Record the play recap.
3. Run the same playbook a second time without changing variables or files.
4. Expect `changed=0` on the second run.
5. Investigate any repeatedly changed task rather than accepting it as normal.

This two-run test was not performed, so idempotency is designed but not proven.

## Current status

YAML parsing, inventory resolution, SSH access, privilege escalation, task behavior, handler behavior, service startup, and idempotency were not executed or verified.
