SUMMARY = "RdkXdslManager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus rdk-logger utopia json-hal-lib avro-c hal-platform libparodus"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkXdslManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=xDSLManager"

SRCREV_xDSLManager = "${AUTOREV}"
SRCREV_FORMAT = "xDSLManager"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I ${STAGING_INCDIR}/syscfg \
    -I ${STAGING_INCDIR}/sysevent \
    -I${STAGING_INCDIR}/libparodus \
    "

DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " libseshat ", " ", d)}"
CFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -DENABLE_SESHAT ", " ", d)}"
LDFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -llibseshat ", " ", d)}"
CFLAGS_append = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "seshat", "-I${STAGING_INCDIR}/libseshat ", " ", d)} \
"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-DFEATURE_RDKB_WAN_MANAGER', '', d)}"
LDFLAGS_append_dunfell = " -lrt -lm"

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/rdk/xdslmanager
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/rdk/conf
    install -d ${D}${exec_prefix}/ccsp/harvester/
    install -m 644 ${S}/source/TR-181/integration_src.shared/XdslReport.avsc ${D}/usr/ccsp/harvester/
    install -m 644 ${S}/config/RdkXdslManager.xml ${D}/usr/rdk/xdslmanager
    install -m 644 ${S}/config/xdsl_manager_conf.json ${D}${sysconfdir}/rdk/conf

    #JSON schema file
    install -d ${D}/${sysconfdir}/rdk/schemas
    install -m 644 ${S}/hal_schema/xdsl_hal_schema.json ${D}/${sysconfdir}/rdk/schemas
}


FILES_${PN} = " \
   ${bindir}/xdslmanager \
   ${exec_prefix}/ccsp/harvester/XdslReport.avsc \
   ${prefix}/rdk/xdslmanager/RdkXdslManager.xml \
   ${sysconfdir}/rdk/conf/xdsl_manager_conf.json \
   ${sysconfdir}/rdk/schemas/xdsl_hal_schema.json \
"

FILES_${PN}-dbg = " \
    ${prefix}/rdk/xdslmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
