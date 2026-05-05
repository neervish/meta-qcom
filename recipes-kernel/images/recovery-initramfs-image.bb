SUMMARY = "Platform recovery initramfs (no switch-root)"
DESCRIPTION = "A minimal initramfs image that runs policy-driven EFI/rootfs recovery \
and falls back to an interactive shell. It intentionally does not include modules \
that perform switch-root to a persistent root filesystem."

LICENSE = "MIT"

inherit core-image

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

# Only the essentials: framework base, udev, minimal fs tools
INITRAMFS_SCRIPTS ?= "\
    initramfs-framework-base \
    initramfs-module-udev \
    initramfs-module-e2fs \
"

# Prevent rootfs mounting and switch-root logic
BAD_RECOMMENDATIONS += "initramfs-module-rootfs"

PACKAGE_INSTALL = "\
    ${INITRAMFS_SCRIPTS} \
    initramfs-module-recovery \
    ${VIRTUAL-RUNTIME_base-utils} \
    udev base-passwd \
    ${ROOTFS_BOOTSTRAP_INSTALL} \
"


# Ensure we DO NOT pull anything that would switch-root
IMAGE_FEATURES = ""
IMAGE_NAME_SUFFIX ?= ""
PACKAGE_EXCLUDE = "kernel-image-*"

IMAGE_ROOTFS_SIZE = "12288"
IMAGE_ROOTFS_EXTRA_SPACE = "0"


# Remove switch_root logic for this recovery image

IMAGE_PREPROCESS_COMMAND += "remove_switchroot_finish;"

python remove_switchroot_finish() {
    import os
    d = d.getVar('IMAGE_ROOTFS')
    finish = os.path.join(d, "init.d", "99-finish")
    if os.path.exists(finish):
        os.remove(finish)
        bb.note("Removed 99-finish (switch_root) from recovery initramfs")
}

