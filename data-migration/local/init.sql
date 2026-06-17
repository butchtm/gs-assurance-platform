CREATE DATABASE assurance_db
WITH
  ENCODING = 'UTF8'
  LC_COLLATE = 'en_GB.UTF-8'
  LC_CTYPE = 'en_GB.UTF-8'
  CONNECTION LIMIT = -1
  TEMPLATE 'template0';

\connect assurance_db;

CREATE EXTENSION tds_fdw;

CREATE SERVER scdatacopy FOREIGN DATA WRAPPER tds_fdw OPTIONS (
  servername '$SC_DB_SERVER',
  port '$SC_DB_PORT',
  database '$SC_DB_NAME',
  character_set 'UTF-8'
);

GRANT USAGE ON FOREIGN SERVER scdatacopy TO postgres;

CREATE USER MAPPING FOR postgres SERVER scdatacopy OPTIONS (username '$SC_DB_USERNAME', password '$SC_DB_PASSWORD');
