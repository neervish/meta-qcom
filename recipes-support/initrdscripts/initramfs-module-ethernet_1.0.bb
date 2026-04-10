SUMMARY = "Initramfs Ethernet bring-up module"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "file://20-ethernet"

S = "${UNPACKDIR}"
INITRAMFS_ETH_FW_FSTYPE ?= "ext4"
RDEPENDS:${PN} = "${VIRTUAL-RUNTIME_base-utils} busybox"

do_install() {
    install -d ${D}/init.d ${D}/etc/initramfs
    install -m 0755 ${S}/20-ethernet ${D}/init.d/20-ethernet

    cat > ${D}/etc/initramfs/ethernet.conf <<EOF
# Runtime initramfs Ethernet configuration
# Accepts package names (kernel-module-foo) or raw module names.
ETH_MODULES="${INITRAMFS_ETH_MODULES}"
ETH_FW_PARTLABEL="${INITRAMFS_ETH_FW_PARTLABEL}"
ETH_FW_FILES="${INITRAMFS_ETH_FW_FILES}"
ETH_FW_FSTYPE="${INITRAMFS_ETH_FW_FSTYPE}"
EOF
}

FILES:${PN} = " \
    /init.d/20-ethernet \
    /etc/initramfs/ethernet.conf \
"
