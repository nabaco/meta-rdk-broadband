SUMMARY = "CCSP CcspCrSsp component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspCr"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus telemetry utopia libunpriv rbus libxml2"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

require ccsp_common.inc

CFLAGS += " -Wall -Werror -Wextra"

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspCr;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspCr"

SRCREV_CcspCr = "${AUTOREV}"
SRCREV_FORMAT = "CCspCr"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-notify', '', d)}"

CFLAGS_append = " \
    -I=${includedir}/dbus-1.0 \
    -I=${libdir}/dbus-1.0/include \
    -I=${includedir}/ccsp \
    -I${STAGING_INCDIR}/syscfg \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/rbus \
    -I${STAGING_INCDIR}/rtmessage \
    -I${STAGING_INCDIR}/libxml2 \
    "

LDFLAGS += "-ldbus-1 -ltelemetry_msgsender -lprivilege -lutapi -lutctx -lsyscfg -lcjson -lmsgpackc"

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp
    ln -sf /usr/bin/CcspCrSsp ${D}/usr/ccsp/CcspCrSsp
    install -m 644 ${S}/source/cr-ethwan-deviceprofile.xml ${D}/usr/ccsp/cr-ethwan-deviceprofile.xml
    install -m 644 ${S}/config/cr-deviceprofile_embedded.xml ${D}/usr/ccsp/cr-deviceprofile.xml
}

PACKAGES += "${PN}-ccsp"

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspCrSsp_gtest.bin', '', d)} \
"

FILES_${PN} = "\
    ${bindir}/CcspCrSsp \
"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/CcspCrSsp \
    ${prefix}/ccsp/cr-deviceprofile.xml \
    ${prefix}/ccsp/cr-ethwan-deviceprofile.xml \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspCrSsp', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
