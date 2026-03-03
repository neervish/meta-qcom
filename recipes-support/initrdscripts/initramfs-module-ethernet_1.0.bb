SUMMARY = "Ethernet bring-up module"
LICENSE = "MIT"                                                                    
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "file://20-ethernet"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${S}/20-ethernet ${D}/init.d/20-ethernet
}

FILES:${PN} = "/init.d/"

