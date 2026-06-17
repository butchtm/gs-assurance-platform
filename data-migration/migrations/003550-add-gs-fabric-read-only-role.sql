DO
'
BEGIN
  IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = ''gs_fabric_read_only'') THEN
    RAISE NOTICE ''gs_fabric_read_only role already exists.'';
  ELSE
    CREATE ROLE gs_fabric_read_only LOGIN;
  END IF;
END
';

GRANT CONNECT ON DATABASE assurance_db TO gs_fabric_read_only;
GRANT USAGE ON SCHEMA gs TO gs_fabric_read_only;
GRANT SELECT ON ALL TABLES IN SCHEMA gs TO gs_fabric_read_only;
ALTER DEFAULT PRIVILEGES IN SCHEMA gs GRANT SELECT ON TABLES TO gs_fabric_read_only;