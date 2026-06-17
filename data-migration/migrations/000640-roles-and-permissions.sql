-- rw role for the assurance api
DO
'
BEGIN
  IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = ''gs_assurance_db_writer'') THEN
    RAISE NOTICE ''gs_assurance_db_writer role already exists.'';
  ELSE
    CREATE ROLE gs_assurance_db_writer;
  END IF;
END
';

GRANT USAGE ON SCHEMA gs TO gs_assurance_db_writer;

-- views
GRANT SELECT ON gs.v_categorised_request_document TO gs_assurance_db_writer;
GRANT SELECT ON gs.v_reference_enumeration TO gs_assurance_db_writer;

-- ro tables
GRANT SELECT ON gs.account_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.assignment_status TO gs_assurance_db_writer;
GRANT SELECT ON gs.assignment_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.carbon_stream TO gs_assurance_db_writer;
GRANT SELECT ON gs.current_standard_version TO gs_assurance_db_writer;
GRANT SELECT ON gs.document_display_category TO gs_assurance_db_writer;
GRANT SELECT ON gs.document_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.fee_schedule TO gs_assurance_db_writer;
GRANT SELECT ON gs.invoice_status TO gs_assurance_db_writer;
GRANT SELECT ON gs.methodology TO gs_assurance_db_writer;
GRANT SELECT ON gs.programme_of_activities TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_asset TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_category TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_location TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_size TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_status TO gs_assurance_db_writer;
GRANT SELECT ON gs.project_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.registration_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.request_document_status TO gs_assurance_db_writer;
GRANT SELECT ON gs.request_status TO gs_assurance_db_writer;
GRANT SELECT ON gs.request_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.request_type_document_type TO gs_assurance_db_writer;
GRANT SELECT ON gs.sustainable_development_goal TO gs_assurance_db_writer;

-- sequences
GRANT USAGE, SELECT ON gs.process_id_seq TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_account TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_audit_event TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_project TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_project_gsid TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_user TO gs_assurance_db_writer;

-- rw tables
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.account TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.audit_event TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.base_project_document TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.base_project_request TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.bundle TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.document TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.energy_project TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.invoice TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.luf_project TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.process TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_crediting_period TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_methodology TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_project_asset TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_project_location TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_project_type TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_status_history TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_sustainable_development_goal TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_account_assignment TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_document TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_status_history TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.transition_approval_procedure TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.transition_from_other_scheme TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.transition_pathway TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.user TO gs_assurance_db_writer;

-- assurance api backend user
DO
'
BEGIN
  IF EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = ''assurance_backend'') THEN
    RAISE NOTICE ''assurance_backend role already exists.'';
  ELSE
    CREATE ROLE assurance_backend LOGIN;
  END IF;
END
';

GRANT gs_assurance_db_writer TO assurance_backend;