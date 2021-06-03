DEPENDS = "ccsp-common-library rdk-logger avro-c trower-base64 hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi msgpackc dbus util-linux utopia wrp-c nanomsg libparodus "
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
require recipes-ccsp/ccsp/ccsp_common.inc

RDEPENDS_${PN} += "avro-c trower-base64 rdk-logger msgpackc util-linux utopia "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/generic/harvester;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=harvester"

SRCREV_harvester = "${AUTOREV}"
PV = "${RDK_RELEASE}+git${SRCPV}"

CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter -Wno-pointer-sign -Wno-sign-compare "

CFLAGS_append_dunfell = " -Wno-format-truncation "

CFLAGS_append = " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/cimplog \
    -I${STAGING_INCDIR}/libparodus \
    -DFEATURE_SUPPORT_RDKLOG \
    "

S = "${WORKDIR}/git"

inherit autotools

# generating minidumps symbols
inherit comcast-breakpad
DEPENDS += "breakpad breakpad-wrapper"
BREAKPAD_BIN_append = " harvester"

LDFLAGS_append = " \
    -ldbus-1 \
    -lrdkloggers \
    -llog4c \
    -lsysevent \
    -lsyscfg \
    -lutapi \
    -lutctx \
    -lnanomsg \
    -lcimplog \
    -lwrp-c \
    -llibparodus \
    "
DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " libseshat ", " ", d)}"
CFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -DENABLE_SESHAT ", " ", d)}"
LDFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -llibseshat ", " ", d)}"

LDFLAGS += "-lbreakpadwrapper -lpthread -lstdc++"

CFLAGS_append = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "seshat", "-I${STAGING_INCDIR}/libseshat ", " ", d)} \
"

inherit pythonnative
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

do_compile_prepend(){
	(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config-atom/Harvester.XML ${S}/source/HarvesterSsp/dm_pack_datamodel.c)
}
do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/harvester
    install -m 664 ${S}/config-atom/InterfaceDevicesWifi.avsc -t ${D}/usr/ccsp/harvester
    install -m 664 ${S}/config-atom/RadioInterfacesStatistics.avsc -t ${D}/usr/ccsp/harvester
    install -m 664 ${S}/config-atom/GatewayAccessPointNeighborScanReport.avsc -t ${D}/usr/ccsp/harvester
}

FILES_${PN} += " \
    ${exec_prefix}/ccsp/harvester/InterfaceDevicesWifi.avsc \
    ${exec_prefix}/ccsp/harvester/RadioInterfacesStatistics.avsc \
    ${exec_prefix}/ccsp/harvester/GatewayAccessPointNeighborScanReport.avsc \
    ${libdir}/libwifi.so* \
"

ERROR_QA_remove_morty = "la"

# generating minidumps
PACKAGECONFIG_append = " breakpad"
