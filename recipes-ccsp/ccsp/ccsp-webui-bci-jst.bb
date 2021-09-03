SUMMARY = "CCSP WebUI component"
HOMEPAGE = "http://github.com/ccsp-yocto/webui"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../../LICENSE;md5=0ef5cb68a38cd4d4c9f9d350c50f68f8"

DEPENDS = "ccsp-common-library sso"
require ccsp_common.inc
SRC_URI = "\
    ${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/webui-bwg;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH} \
    ${CMF_GIT_ROOT}/rdkb/devices/rdkbemu/rdkbemu_xb3;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};destsuffix=xb3;name=xb3 \
    "

FILESEXTRAPATHS_prepend := "${THISDIR}/ccsp-webui-bci:"

SRC_URI_append = " \
                 file://LICENSE \
                 file://ajax_maintenance_window_conf.jst \
                 file://bci_maintenance_window_jst.patch;apply=no \
                 "

SRCREV = "${AUTOREV}"
SRCREV_xb3 = "${AUTOREV}"
SRCREV_FORMAT = "default_xb3"

PV = "${RDK_RELEASE}"

S = "${WORKDIR}/git/source/Styles/xb3"

inherit autotools systemd

inherit lxc
LXC_TEMPLATE = "lxc-lighttpd"
LXC_NAME = "webui"
LXC_LOG_PATH = "/rdklogs/logs"
LXC_LOG_LEVEL = "8"

CFLAGS += " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -fPIC \
    "

LDFLAGS += " \
     -ldbus-1 \
     -lsso \
     "

EXTRA_OECONF = "CCSP_COMMON_LIB=${STAGING_LIBDIR}"

do_install() {
        install -d ${D}${base_libdir}/rdk
        install -d ${D}${systemd_unitdir}/system
}

do_install_append() {
    # Config files and scripts
    install -d ${D}/usr/www2
    install -d ${D}/usr/www2/actionHandler
    install -d ${D}/usr/www2/cmn
    install -d ${D}/usr/www2/cmn/css
    install -d ${D}/usr/www2/cmn/css/lib
    install -d ${D}/usr/www2/cmn/img
    install -d ${D}/usr/www2/cmn/js
    install -d ${D}/usr/www2/cmn/js/lib
    install -d ${D}/usr/www2/includes
    install -d ${D}/fss/gw/usr/ccsp
    install -d ${D}${sysconfdir}
    install -m 755 ${S}/code/status-500.html ${D}/usr/www2
    install -m 755 ${S}/jst/*.jst ${D}/usr/www2
    install -m 755 ${S}/jst/actionHandler/*.jst ${D}/usr/www2/actionHandler
    install -m 755 ${S}/code/cmn/css/*.css ${D}/usr/www2/cmn/css
    install -m 755 ${S}/code/cmn/css/lib/*.css ${D}/usr/www2/cmn/css/lib
    install -m 755 ${S}/code/cmn/img/* ${D}/usr/www2/cmn/img
    install -m 755 ${S}/code/cmn/js/*.js ${D}/usr/www2/cmn/js
    install -m 755 ${S}/code/cmn/js/lib/* ${D}/usr/www2/cmn/js/lib
    install -m 755 ${S}/jst/includes/*.jst ${D}/usr/www2/includes
    install -m 755 ${S}/config/*.sh ${D}${sysconfdir}
    install -m 0755 ${WORKDIR}/ajax_maintenance_window_conf.jst ${D}/usr/www2/actionHandler/
    sed -i 's/usr\/www/usr\/www2/g' ${D}${sysconfdir}/webgui.sh
}


FILES_${PN} += "/fss/* /fss/gw/* /fss/gw/usr/* /fss/gw/usr/ccsp/* /opt/www2/* ${base_libdir}/rdk/* ${libdir}"

FILES_${PN} += "/usr/www2/*"
FILES_${PN} += "/usr/www2/actionHandler/*"
FILES_${PN} += "/usr/www2/cmn/*"
FILES_${PN} += "/usr/www2/includes/*"
FILES_${PN}-dbg += "${libdir}/extensions/*/.debug/* \
                    /fss/gw/usr/ccsp/.debug/*"



