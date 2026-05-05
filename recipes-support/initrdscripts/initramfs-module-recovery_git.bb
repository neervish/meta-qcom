SUMMARY = "initramfs module: platform recovery (EFI + rootfs repair, policy-controlled)"
DESCRIPTION = "A modular recovery flow for initramfs that attempts EFI and rootfs repair \
and falls back to an interactive shell based on policy. No switch-root is performed."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=223037c4be0bfc6cf757035432adf983"

inherit allarch

SRC_URI = "git://github.com/qualcomm-linux/initramfs-recovery-scripts.git;branch=main;protocol=ssh"
SRCREV = "8df1405c1ab9c965044bff7be5c88858f69840e2"


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

    cat > ${D}/etc/recovery/efi.conf <<'EOF'
# Runtime EFI health-check configuration
# Normal OS ESP partition label to verify from recovery flow.
# Override if your platform uses another partlabel.
ESP_PARTLABEL=efi
# Expected filesystem type for ESP.
ESP_FSTYPE=vfat
BOOT_FALLBACK_PATH=EFI/BOOT/bootaa64.efi
NORMAL_UKI_GLOB=EFI/Linux/linux-*.efi
EOF

    cat > ${D}/etc/recovery/rootfs.conf <<'EOF'
# Runtime rootfs repair configuration
# Preferred explicit target device (overrides ROOTFS_PARTLABEL when set):
# ROOTFS_DEVICE=/dev/sda3
ROOTFS_DEVICE=
# Normal OS rootfs partition label to verify/repair.
ROOTFS_PARTLABEL=rootfs
# Expected filesystem type.
ROOTFS_FSTYPE=ext4
# If set to 1, skip rootfs repair when EFI check is broken.
SKIP_IF_EFI_BROKEN=1
EOF

    install -m 0755 ${S}/scripts/40-policy         ${D}/init.d/40-policy
    install -m 0755 ${S}/scripts/60-efi-repair     ${D}/init.d/60-efi-repair
    install -m 0755 ${S}/scripts/70-rootfs-repair  ${D}/init.d/70-rootfs-repair
    install -m 0755 ${S}/scripts/98-recovery-shell ${D}/init.d/98-recovery-shell
}

# Package contents
FILES:${PN} = " \
  /init.d/40-policy \
  /init.d/60-efi-repair \
  /init.d/70-rootfs-repair \
  /init.d/98-recovery-shell \
  /etc/recovery/policy.conf \
  /etc/recovery/efi.conf \
  /etc/recovery/rootfs.conf \
  /run \
  ${libdir}/recovery \
"
