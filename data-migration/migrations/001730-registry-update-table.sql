CREATE TABLE gs.registry_update (
	project_id integer PRIMARY KEY REFERENCES gs.project,
	created_timestamp timestamp NOT NULL DEFAULT NOW(),
	last_attempt_at timestamp,
	attempts int4 NOT NULL DEFAULT 0
);

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.registry_update TO gs_assurance_db_writer;