SUMMARY = "RDK PPP Manager component"

LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus rdk-logger utopia halinterface"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkPppManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=PppManager"

SRCREV_PppManager = "${AUTOREV}"
SRCREV_FORMAT = "PppManager"

PV = "${RDK_RELEASE}+git"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I ${STAGING_INCDIR}/syscfg \
    -I ${STAGING_INCDIR}/sysevent \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    "
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' -fPIC -I${STAGING_INCDIR}/libsafec', '-fPIC', d)}"
LDFLAGS_append_dunfell = " -ldbus-1"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/pppmanager
    ln -sf ${bindir}/pppmanager ${D}${exec_prefix}/rdk/pppmanager/pppmanager
    install -m 644 ${S}/config/RdkPppManager.xml ${D}/usr/rdk/pppmanager/
}


FILES_${PN} = " \
   ${exec_prefix}/rdk/pppmanager/pppmanager \
   ${exec_prefix}/rdk/pppmanager/RdkPppManager.xml \
   ${bindir}/* \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/ccsp/pppmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
