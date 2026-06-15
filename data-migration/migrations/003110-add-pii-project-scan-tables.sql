-- Create sequences for auto-incrementing IDs
CREATE SEQUENCE gs.pii_scan_run_id_seq;
CREATE SEQUENCE gs.project_id_pii_scan_run_id_seq;
CREATE SEQUENCE gs.pii_finding_id_seq;
CREATE SEQUENCE gs.pii_scan_state_id_seq;

-- Create PiiScanRun table (referenced by ProjectIdPiiScanRun)
CREATE TABLE gs.pii_scan_run (
    id integer NOT NULL DEFAULT nextval('gs.pii_scan_run_id_seq'),
    duration bigint,
    error_message varchar,
    started_at timestamp,
    finished_at timestamp,
    status varchar,
    PRIMARY KEY (id)
);

-- Create ProjectIdPiiScanRun table
CREATE TABLE gs.project_id_pii_scan_run (
    id integer NOT NULL DEFAULT nextval('gs.project_id_pii_scan_run_id_seq'),
    pii_scan_run_id integer NOT NULL,
    project_id integer NOT NULL,
    status varchar,
    PRIMARY KEY (id),
    CONSTRAINT uq_project_id_pii_scan_run_pii_scan_run_id_project_id UNIQUE (pii_scan_run_id, project_id)
);

-- Create PiiFinding table
CREATE TABLE gs.pii_finding (
    id integer NOT NULL DEFAULT nextval('gs.pii_finding_id_seq'),
    pii_scan_run_id integer NOT NULL,
    entity_type varchar,
    project_id integer,
    field_name varchar,
    pii_type varchar,
    probability varchar,
    created_at timestamp,
    PRIMARY KEY (id)
);

-- Create PiiScanState table
CREATE TABLE gs.pii_scan_state (
    id integer NOT NULL DEFAULT nextval('gs.pii_scan_state_id_seq'),
    pii_scan_run_id integer NOT NULL,
    last_project_id_scanned integer NOT NULL,
    updated_at timestamp,
    PRIMARY KEY (id)
);

-- Set sequence ownership
ALTER SEQUENCE gs.pii_scan_run_id_seq OWNED BY gs.pii_scan_run.id;
ALTER SEQUENCE gs.project_id_pii_scan_run_id_seq OWNED BY gs.project_id_pii_scan_run.id;
ALTER SEQUENCE gs.pii_finding_id_seq OWNED BY gs.pii_finding.id;
ALTER SEQUENCE gs.pii_scan_state_id_seq OWNED BY gs.pii_scan_state.id;

-- Add foreign key constraints
ALTER TABLE gs.project_id_pii_scan_run
ADD CONSTRAINT fk_project_id_pii_scan_run_pii_scan_run_id
FOREIGN KEY (pii_scan_run_id)
REFERENCES gs.pii_scan_run(id);

ALTER TABLE gs.project_id_pii_scan_run
ADD CONSTRAINT fk_project_id_pii_scan_run_project_id
FOREIGN KEY (project_id)
REFERENCES gs.project(id);

ALTER TABLE gs.pii_finding
ADD CONSTRAINT fk_pii_finding_pii_scan_run_id
FOREIGN KEY (pii_scan_run_id)
REFERENCES gs.pii_scan_run(id);

ALTER TABLE gs.pii_finding
ADD CONSTRAINT fk_pii_finding_project_id
FOREIGN KEY (project_id)
REFERENCES gs.project(id);

ALTER TABLE gs.pii_scan_state
ADD CONSTRAINT fk_pii_scan_state_pii_scan_run_id
FOREIGN KEY (pii_scan_run_id)
REFERENCES gs.pii_scan_run(id);

-- Grant permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.pii_scan_run TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.project_id_pii_scan_run TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.pii_finding TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.pii_scan_state TO gs_assurance_db_writer;
GRANT USAGE ON gs.pii_scan_run_id_seq TO gs_assurance_db_writer;
GRANT USAGE ON gs.project_id_pii_scan_run_id_seq TO gs_assurance_db_writer;
GRANT USAGE ON gs.pii_finding_id_seq TO gs_assurance_db_writer;
GRANT USAGE ON gs.pii_scan_state_id_seq TO gs_assurance_db_writer;
