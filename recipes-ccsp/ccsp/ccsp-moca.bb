SUMMARY = "CCSP MoCA component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library webconfig-framework utopia hal-moca curl trower-base64 msgpack-c libunpriv"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

require ccsp_common.inc

CFLAGS += " -Wall -Werror -Wextra -Wno-address "


SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspMoCA;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspMoCA"

SRCREV_CcspMoCA = "${AUTOREV}"
SRCREV_FORMAT = "CcspMoCA"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig pythonnative

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/syscfg \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/msgpackc \
    "

EXTRA_OECONF_append_mips = " --enable-notify"

CFLAGS_append += "-DCONFIG_VENDOR_CUSTOMER_COMCAST -DCONFIG_CISCO_HOTSPOT"

LDFLAGS_append = " \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    -lmsgpackc \
    -ltrower-base64 \
    -lprivilege \
    "

LDFLAGS_append_dunfell = " -lsyscfg -lsysevent"
do_compile_prepend () {
	(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/TR181-MoCA.XML ${S}/source/MoCASsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/moca
    install -m 644 ${S}/config/CcspMoCA.cfg ${D}/usr/ccsp/moca/CcspMoCA.cfg
    install -m 644 ${S}/config/CcspMoCADM.cfg ${D}/usr/ccsp/moca/CcspMoCADM.cfg
    install -m 0755 ${S}/scripts/MoCA_isolation.sh ${D}/usr/ccsp/moca/MoCA_isolation.sh
    install -m 0755 ${S}/scripts/moca_whitelist_ctl.sh ${D}/usr/ccsp/moca/moca_whitelist_ctl.sh
}

PACKAGES += "${PN}-ccsp"
PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspMoCA_gtest.bin', '', d)} \
"

FILES_${PN} = "\
    ${bindir}/CcspMoCA \
    ${bindir}/MRD \
"

FILES_${PN}-ccsp += " \
    ${prefix}/ccsp/moca/CcspMoCA.cfg  \
    ${prefix}/ccsp/moca/CcspMoCADM.cfg  \
    ${prefix}/ccsp/moca/MoCA_isolation.sh  \
    ${prefix}/ccsp/moca/moca_whitelist_ctl.sh  \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/moca/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspMoCA', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
