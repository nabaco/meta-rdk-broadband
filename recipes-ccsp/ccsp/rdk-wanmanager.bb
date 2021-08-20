SUMMARY = "RDK WAN Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library hal-cm dbus rdk-logger utopia hal-dhcpv4c libunpriv ccsp-misc"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'nanomsg', '', d)}"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkWanManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=WanManager"

SRCREV_WanManager = "${AUTOREV}"
SRCREV_FORMAT = "WanManager"

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
    "
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-DFEATURE_RDKB_WAN_MANAGER', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-lnanomsg', '', d)}"

LDFLAGS += " -lprivilege"


do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/wanmanager
    ln -sf ${bindir}/wanmanager ${D}${exec_prefix}/rdk/wanmanager/wanmanager 
    install -m 644 ${S}/config/RdkWanManager.xml ${D}/usr/rdk/wanmanager
}


FILES_${PN} = " \
   ${exec_prefix}/rdk/wanmanager/wanmanager \
   ${exec_prefix}/rdk/wanmanager/RdkWanManager.xml \
   ${bindir}/* \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/wanmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
