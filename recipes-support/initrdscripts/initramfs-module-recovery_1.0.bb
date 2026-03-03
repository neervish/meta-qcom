SUMMARY = "initramfs module: platform recovery (EFI + rootfs repair, policy-controlled)"
DESCRIPTION = "A modular recovery flow for initramfs that attempts EFI and rootfs repair \
and falls back to an interactive shell based on policy. No switch-root is performed."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch

# --- Local sources today (kept in files/) ---
SRC_URI = " \
  file://40-policy \
  file://60-efi-repair \
  file://70-rootfs-repair \
  file://98-recovery-shell \
"

# --- Future GitHub source (commented until repo exists) ---
# SRC_URI += "git://github.com/<org>/<repo>.git;branch=main;protocol=https"
# SRCREV = "<pin-a-commit>"

S = "${UNPACKDIR}"

# Runtime dependencies needed by the recovery scripts
RDEPENDS:${PN} = "\
    ${VIRTUAL-RUNTIME_base-utils} \
    busybox \
    udev \
    efivar efibootmgr \
    util-linux-blkid util-linux-sfdisk \
    parted \
    e2fsprogs e2fsprogs-mke2fs e2fsprogs-e2fsck \
    dosfstools \
"

# Optional (enable later via policy):
# RDEPENDS:${PN} += "iproute2 iputils-ping dhcpcd"

do_install() {
    install -d ${D}/init.d
    install -d ${D}/etc/recovery
    install -d ${D}/run
    install -d ${D}${libdir}/recovery

    # Default policy: act only in recovery mode, allow shell fallback.
    cat > ${D}/etc/recovery/policy.conf <<'EOF'
# Recovery policy (simple key=value; sourced by /bin/sh)
# MODE: "recovery"  -> act only when UEFI signals recovery
#       "always"    -> run recovery sequence unconditionally
MODE=recovery
# Allow interactive shell fallback if repairs fail or on demand
ALLOW_SHELL=1
EOF

    install -m 0755 ${S}/40-policy         ${D}/init.d/40-policy
    install -m 0755 ${S}/60-efi-repair     ${D}/init.d/60-efi-repair
    install -m 0755 ${S}/70-rootfs-repair  ${D}/init.d/70-rootfs-repair
    install -m 0755 ${S}/98-recovery-shell ${D}/init.d/98-recovery-shell
}

# Package contents
FILES:${PN} = " \
  /init.d/40-policy \
  /init.d/60-efi-repair \
  /init.d/70-rootfs-repair \
  /init.d/98-recovery-shell \
  /etc/recovery/policy.conf \
  /run \
  ${libdir}/recovery \
"

