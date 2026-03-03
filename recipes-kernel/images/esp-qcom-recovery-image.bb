DESCRIPTION = "Recovery ESP image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"


PACKAGES = "${PN}"
PACKAGE_INSTALL = "systemd-boot"

inherit image uki uki-esp-image features_check

# Recovery UKI filename
UKI_FILENAME = "recovery-${MACHINE}.efi"

# Use Recovery initramfs
INITRAMFS_IMAGE = "${RECOVERY_INITRAMFS_IMAGE}"

# Recovery kernel cmdline
UKI_CMDLINE = "rdinit=/sbin/init recovery_mode=1 console=${KERNEL_CONSOLE}"
UKI_CMDLINE += "${@d.getVar('KERNEL_CMDLINE_EXTRA') or ''}"

# DTB handling same as normal ESP
KERNEL_DEVICETREE = ""

IMAGE_FSTYPES = "vfat"
IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

# Align image size with the expected partition size (512MB)
IMAGE_ROOTFS_SIZE = "524288"
IMAGE_ROOTFS_MAXSIZE = "524288"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

# Place recovery UKI in EFI/Linux
ESPFOLDER = ""

setup_efi_folder() {
    if [ -d ${IMAGE_ROOTFS}/boot/EFI ]; then
        mv ${IMAGE_ROOTFS}/boot/EFI/* ${IMAGE_ROOTFS}/EFI
    fi

    # Keep ONLY EFI folder, delete everything else
    find ${IMAGE_ROOTFS} -mindepth 1 ! -path "${IMAGE_ROOTFS}/EFI*" -exec rm -rf {} +
}
IMAGE_PREPROCESS_COMMAND:append = " setup_efi_folder"

do_uki[depends] += " ${RECOVERY_INITRAMFS_IMAGE}:do_image"

REQUIRED_MACHINE_FEATURES = "efi"
