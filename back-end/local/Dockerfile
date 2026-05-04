FROM postgres:15
RUN apt-get update && \
    apt-get install -y libsybdb5 freetds-dev freetds-common gnupg gcc make wget postgresql-server-dev-15 && \
    wget https://github.com/tds-fdw/tds_fdw/archive/v2.0.3.tar.gz && \
    tar -xvzf v2.0.3.tar.gz && \
    cd tds_fdw-2.0.3 && \
    make USE_PGXS=1 && \
    make USE_PGXS=1 install && \
    apt-get remove -y gcc make
RUN apt-get install -y locales locales-all
COPY locales.conf /etc/locales.conf
