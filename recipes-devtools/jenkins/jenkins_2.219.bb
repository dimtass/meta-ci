SUMMARY = "Continuous Integration and Job Scheduling Server"

LICENSE = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=4d92cd373abda3937c2bc47fbc49d690"


SRC_URI = "http://updates.jenkins-ci.org/download/war/${PV}/jenkins.war \
           file://jenkins-server.service \
          "
SRC_URI[md5sum] = "da952f7483c8560205f2fa924a03ae64"
SRC_URI[sha256sum] = "49757dcf796f7c8714d915eeaa22effcf50546e716b7d818183c304c30005b7e"

inherit allarch systemd useradd

do_install() {
    install -d ${D}${datadir}/jenkins
    install -m0644 ${WORKDIR}/jenkins.war ${D}${datadir}/jenkins/

    install -d ${D}${systemd_unitdir}/system
    install -m0644 ${WORKDIR}/jenkins-server.service ${D}${systemd_unitdir}/system
    sed -i -e s:/usr/share:${datadir}: ${D}${systemd_unitdir}/system/jenkins-server.service
}

PACKAGES =+ "jenkins-common"
FILES_jenkins-common = "${datadir}/jenkins/jenkins.war"

PACKAGES =+ "jenkins-server" 
FILES_jenkins-server = "${systemd_unitdir}"

# FIXME: http://jenkins-ci.361315.n4.nabble.com/Using-UNIX-PAM-authentication-from-a-non-root-user-tp378559p378563.html
# 1) add jenkins to 'shadow' group
# 2) chown root.shadow /etc/shadow*
# 3) chmod g+r /etc/shadow*

USERADD_PACKAGES = "${PN}-server"
USERADD_PARAM_${PN}-server = "--system --create-home --home-dir ${localstatedir}/lib/jenkins jenkins"

SYSTEMD_PACKAGES = "${PN}-server"
SYSTEMD_SERVICE_jenkins-server = "jenkins-server.service"

# From the debian packages, not sure about psmisc.
RDEPENDS_jenkins-server = "openjre-8 \
                           procps file psmisc \
                           jenkins-common \
                           ttf-dejavu-sans \
                          "

# For unix user auth
RRECOMMENDS_jenkins-server = "openssh-sshd"
