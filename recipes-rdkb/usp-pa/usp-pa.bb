#
# Yocto recipe to install obuspa open source project
#

SUMMARY = "USP Pa component"
DESCRIPTION = "Agent for USP protocol"
DEPENDS = "openssl sqlite3 curl zlib ccsp-common-library"
LICENSE = "BSD-3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b4b2a405a0e92a1292adf5b60afb3a0b"

require recipes-ccsp/ccsp/ccsp_common.inc


# OBUSPA is the reference USP agent codebase
OBUSPA_REL="3.0.0"
SRC_URI = "https://github.com/BroadbandForum/obuspa/archive/v${OBUSPA_REL}-master.tar.gz;name=obuspa"
SRC_URI[obuspa.md5sum] = "7ffdc4184198f3fb2535700010d3423f"

# USPPA is the RDK specializations
SRC_URI += "git://github.com/rdkcentral/usp-pa-vendor-rdk;protocol=http;branch=main;name=usppa"
SRCREV_usppa = "5c98036d5a0554ef5e73b26e70be6e9cf34cff2b"

# Patches for OBUSPA
SRC_URI += "file://patches/no_hardcoded_cflags.patch"
SRC_URI += "file://patches/no_mqtt.patch"

# Configuration files for target
SRC_URI += "file://conf/usp_factory_reset.conf"
SRC_URI += "file://conf/usp_dm_comps.conf"
SRC_URI += "file://conf/usp_dm_objs.conf"
SRC_URI += "file://conf/usp_dm_params.conf"
SRC_URI += "file://conf/usp_truststore.pem"
SRC_URI += "file://usp-pa.service"


# Make sure our source directory (for the build) matches the directory structure in the tarball
S = "${WORKDIR}/obuspa-${OBUSPA_REL}-master"

# Specify the rules to use to build and install this package
inherit autotools pkgconfig systemd


CFLAGS += " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
"

LDFLAGS += "-ldbus-1 -lccsp_common"

# Specialize the OBUSPA release by copying across the RDK specific source files to the source directory
do_configure_prepend() {
    cp ${WORKDIR}/git/src/vendor/* ${S}/src/vendor
}

# Copy files to staging area
do_install() {
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}
    install -d ${D}${sysconfdir}/usp-pa
    install -d ${D}${systemd_system_unitdir}
    
    install -m 0777 ${B}/obuspa ${D}${bindir}/UspPa
    install -m 0644 ${WORKDIR}/conf/usp_factory_reset.conf ${D}${sysconfdir}/usp-pa
    install -m 0644 ${WORKDIR}/conf/usp_dm_comps.conf ${D}${sysconfdir}/usp-pa
    install -m 0644 ${WORKDIR}/conf/usp_dm_objs.conf ${D}${sysconfdir}/usp-pa
    install -m 0644 ${WORKDIR}/conf/usp_dm_params.conf ${D}${sysconfdir}/usp-pa
    install -m 0644 ${WORKDIR}/conf/usp_truststore.pem ${D}${sysconfdir}/usp-pa
    install -m 0644 ${WORKDIR}/usp-pa.service ${D}${systemd_system_unitdir}
}

# Files in staging area to copy to system image
FILES_${PN} += "${bindir}/UspPa"
FILES_${PN} += "${systemd_unitdir}/system/usp-pa.service"
FILES_${PN} += "${sysconfdir}/usp-pa/usp_factory_reset.conf"
FILES_${PN} += "${sysconfdir}/usp-pa/usp_dm_comps.conf"
FILES_${PN} += "${sysconfdir}/usp-pa/usp_dm_objs.conf"
FILES_${PN} += "${sysconfdir}/usp-pa/usp_dm_params.conf"
FILES_${PN} += "${sysconfdir}/usp-pa/usp_truststore.pem"

# Signal that a system-d service must be provisioned
SYSTEMD_SERVICE_${PN} = "usp-pa.service"




