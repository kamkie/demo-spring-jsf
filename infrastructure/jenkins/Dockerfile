FROM jenkins/jenkins

RUN /usr/local/bin/install-plugins.sh workflow-api:2.23 workflow-job:2.15 blueocean gradle git \
    cloudbees-folder ssh-slaves greenballs mask-passwords ansicolor publish-over-ssh sonar build-timeout \
    ssh-agent timestamper htmlpublisher warnings junit ssh mailer subversion
