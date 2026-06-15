FROM postgres:15

ARG sc_db_username
ARG sc_db_password
ARG sc_db_server=scdata2.database.windows.net
ARG sc_db_port=1433
ARG sc_db_name=SCDatacopy

ENV SC_DB_USERNAME=$sc_db_username
ENV SC_DB_PASSWORD=$sc_db_password
ENV SC_DB_SERVER=$sc_db_server
ENV SC_DB_PORT=$sc_db_port
ENV SC_DB_NAME=$sc_db_name

COPY init.sql /docker-entrypoint-initdb.d/init.sql
COPY locales.conf /etc/freetds/locales.conf
COPY --chmod=0755 run-dump.sh /home/run-dump.sh

RUN apt-get update && \
    apt-get install -y libsybdb5 freetds-dev freetds-common gnupg gcc make wget postgresql-server-dev-15 locales locales-all && \
    wget https://github.com/tds-fdw/tds_fdw/archive/v2.0.3.tar.gz && \
    tar -xvzf v2.0.3.tar.gz && \
    cd tds_fdw-2.0.3 && \
    make USE_PGXS=1 && \
    make USE_PGXS=1 install && \
    apt-get remove -y gcc make

RUN apt-get update && \
    apt-get install -y gettext-base moreutils && \
    envsubst < /docker-entrypoint-initdb.d/init.sql | sponge /docker-entrypoint-initdb.d/init.sql && \
    apt-get remove -y gettext-base moreutils