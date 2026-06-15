DROP SCHEMA IF EXISTS staging CASCADE;
DROP SCHEMA IF EXISTS gs CASCADE;
DROP SCHEMA IF EXISTS gs_dq CASCADE;
DROP TABLE IF EXISTS public.databasechangelog;
DROP TABLE IF EXISTS public.databasechangeloglock;
DROP ROLE assurance_backend;
DROP ROLE gs_assurance_db_writer;
DROP ROLE gs_fabric_read_only;
