SUMMARY = "CCSP Tr069Pa component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspPsm"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus openssl hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi util-linux utopia cjson telemetry libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

require ccsp_common.inc

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspTr069Pa;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspTr069Pa"


SRCREV_CcspTr069Pa = "${AUTOREV}"
SRCREV_FORMAT = "CcspTr069Pa"
PV = "${RDK_RELEASE}"

S = "${WORKDIR}/git"

inherit autotools

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CFLAGS_append = " -Wall -Werror -Wextra -Wno-ignored-qualifiers "

CFLAGS_append_dunfell = " -Wno-deprecated-declarations "

LDFLAGS +=" -lsyscfg"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

CFLAGS_append = " \
    -I=${includedir}/dbus-1.0 \
    -I=${libdir}/dbus-1.0/include \
    -I=${includedir}/ccsp \
    -I=${includedir}/syscfg \
    -I${STAGING_INCDIR}/syscfg \
    "

LDFLAGS_append = " \
    -ldbus-1 \
    -lm \
    -ltelemetry_msgsender \
    "

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/tr069pa
    install -m 644 ${S}/config/custom_mapper.xml ${D}/usr/ccsp/tr069pa/custom_mapper.xml
}

PACKAGES += "${PN}-ccsp"
PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspTr069PaSsp_gtest.bin', '', d)} \
"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/tr069pa/ccsp_tr069_pa_certificate_cfg.xml \
    ${prefix}/ccsp/tr069pa/ccsp_tr069_pa_cfg.xml \
    ${prefix}/ccsp/tr069pa/ccsp_tr069_pa_mapper.xml \
    ${prefix}/ccsp/tr069pa/sdm.xml \
    ${prefix}/ccsp/tr069pa/sharedkey \
    ${prefix}/ccsp/tr069pa/custom_mapper.xml \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/tr069pa/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspTr069PaSsp', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
