SUMMARY = "CCSP miscellaneous tools."
HOMEPAGE = "http://github.com/belvedere-yocto/CcspMisc"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library hal-platform utopia libunpriv"
RDEPENDS_${PN} = " trower-base64 "
DEPENDS += " trower-base64"

require ccsp_common.inc
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspMisc;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspMisc"

SRCREV_CcspMisc = "${AUTOREV}"
SRCREV_FORMAT = "CcspMisc"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

CFLAGS += " -Wall -Werror -Wextra "

CFLAGS_append = " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/trower-base64 \
    "
EXTRA_OECONF += "${@bb.utils.contains("DISTRO_FEATURES", "notifylease", " --enable-notifylease ", " ", d)}"
EXTRA_OECONF_append_puma7 += "${@bb.utils.contains("DISTRO_FEATURES", "setLED", " --enable-setLED=yes", " ", d)}"
EXTRA_OECONF_append_bcm3390 += "${@bb.utils.contains("DISTRO_FEATURES", "setLED", " --enable-setLED=yes", " ", d)}"

EXTRA_OECONF += "${@bb.utils.contains("DISTRO_FEATURES", "multipartUtility", " --enable-multipartUtilEnable=yes ", " ", d)}"

EXTRA_OECONF += "${@bb.utils.contains("DISTRO_FEATURES", "wbCfgTestApp", " --enable-wbCfgTestAppEnable ", " ", d)}"

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp
    install -d ${D}/etc/
    ln -sf /usr/bin/psmcli ${D}/usr/ccsp/psmcli
    install -d ${D}${includedir}/ccsp
    install -m 644 ${S}/source/TimeConv/time_conversion.h ${D}${includedir}/ccsp
    install -m 755 ${S}/source/bridge_utils/scripts/migration_to_psm.sh ${D}/etc/
}

PACKAGES += "${PN}-ccsp"

FILES_${PN}-ccsp = " \
    /usr/ccsp/psmcli \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
