FROM liquibase/liquibase

USER root

RUN apt-get update && \
    apt-get -yqq install openssh-client && \
    apt-get clean

RUN mkdir -p /home/liquibase/.ssh
RUN chown -R liquibase /home/liquibase
USER liquibase
