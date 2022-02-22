CXXFLAGS_append += " -D_RDK_BROADBAND_PRIV_CAPS_ "
CFLAGS_append = " -D_RDK_BROADBAND_PRIV_CAPS_"
require recipes-ccsp/ccsp/ccsp_common.inc

do_install_append(){
        install -d ${D}${sysconfdir}
        install -d ${D}${sysconfdir}/security/caps/
        install -m 755 ${S}/source/process-capabilities_rdkb.json ${D}${sysconfdir}/security/caps/process-capabilities.json
        if [ "${MACHINE_IMAGE_NAME}" = "CGA4332COM" ] || [ "${MACHINE_IMAGE_NAME}" = "CGM4981COM" ]; then
            sed -i '/webpa/!b;n;n;c \      "drop":"UGID_GROUP"' ${D}${sysconfdir}/security/caps/process-capabilities.json
        fi
}

FILES_${PN} += " ${sysconfdir}/security/caps/process-capabilities.json"
