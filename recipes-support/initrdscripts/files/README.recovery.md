# Initramfs Recovery Scripts

This module provides a simple recovery flow for initramfs-framework.

Scripts:

- `40-policy`: decides whether recovery mode is active and writes runtime state.
- `60-efi-repair`: validates the normal EFI partition content.
- `70-rootfs-repair`: runs non-interactive fsck on the configured rootfs target.
- `98-recovery-shell`: opens an interactive shell for debugging when allowed.

The scripts are shell-based and intentionally config-driven so they can be reused
across boards and projects.

## Runtime Config Files

### `/etc/recovery/policy.conf`

- `MODE=recovery|always`
  - `recovery`: run recovery flow only when recovery mode is detected.
  - `always`: force recovery flow in every boot.
- `ALLOW_SHELL=0|1`
  - `1` opens interactive recovery shell at the end.

### `/etc/recovery/efi.conf`

- `ESP_PARTLABEL=<label>` (default: `efi`)
- `ESP_FSTYPE=<fstype>` (default: `vfat`)
- `BOOT_FALLBACK_PATH=<path>` (default: `EFI/BOOT/bootaa64.efi`)
- `NORMAL_UKI_GLOB=<glob>` (default: `EFI/Linux/linux-*.efi`)

EFI check is considered healthy only when mount succeeds and both fallback
bootloader path and UKI glob are present.

### `/etc/recovery/rootfs.conf`

- `ROOTFS_DEVICE=<device>` (optional explicit path, e.g. `/dev/sda3`)
- `ROOTFS_PARTLABEL=<label>` (default: `rootfs`)
- `ROOTFS_FSTYPE=<fstype>` (default: `ext4`)
- `SKIP_IF_EFI_BROKEN=0|1` (default: `1`)

If `ROOTFS_DEVICE` is set, it takes precedence over `ROOTFS_PARTLABEL`.

## Typical Boot Flow

1. `40-policy` sets `RECOVERY_MODE` and policy snapshot in `/run`.
2. `60-efi-repair` checks configured EFI partition.
3. `70-rootfs-repair` fsck checks configured rootfs partition.
4. `98-recovery-shell` opens shell if policy allows.

## Manual Debugging from Recovery Shell

```sh
# Re-run EFI check after editing /etc/recovery/efi.conf
. /init.d/60-efi-repair
efi_run
cat /run/recovery.efi

# Re-run rootfs repair after editing /etc/recovery/rootfs.conf
. /init.d/70-rootfs-repair
rootfs_run
```

## Notes for Integrators

- Keep runtime values in config files; avoid baking machine-specific values into scripts.
- Use partlabels to make scripts portable across storage device naming differences.
- Keep repair actions conservative; current rootfs repair only runs ext fsck.
