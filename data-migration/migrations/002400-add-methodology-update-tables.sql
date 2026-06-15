CREATE TABLE gs.project_methodology_update (
	id integer NOT NULL UNIQUE,
    project_id int4 NOT NULL,
	period_id int4 NOT NULL,
	created_timestamp timestamp NOT NULL,
	created_by_id integer NULL,
	PRIMARY KEY(id),
    CONSTRAINT project_fkey
      FOREIGN KEY (project_id)
      REFERENCES gs.project
      ON DELETE RESTRICT,
    CONSTRAINT project_methodology_update_account_fkey
      FOREIGN KEY (created_by_id)
      REFERENCES gs.account
      ON DELETE RESTRICT);

GRANT SELECT, INSERT, UPDATE ON gs.project_methodology_update TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.project_methodology_update_seq
AS
  integer
OWNED BY
  gs.project_methodology_update.id;

ALTER TABLE gs.project_methodology_update
ALTER COLUMN id SET DEFAULT nextval('gs.project_methodology_update_seq');

GRANT USAGE, SELECT ON gs.project_methodology_update_seq TO gs_assurance_db_writer;

CREATE TABLE gs.methodology_update (
	project_methodology_update_id integer NOT NULL,
    methodology_id int4 NOT NULL,
	initial_methodology_version decimal,
	new_methodology_version decimal,
	operation varchar NOT NULL,
	PRIMARY KEY(project_methodology_update_id, methodology_id),
	CONSTRAINT project_methodology_update_fkey
      FOREIGN KEY (project_methodology_update_id)
      REFERENCES gs.project_methodology_update
      ON DELETE RESTRICT,
    CONSTRAINT methodology_fkey
      FOREIGN KEY (methodology_id)
      REFERENCES gs.methodology
      ON DELETE RESTRICT);

GRANT SELECT, INSERT, UPDATE ON gs.methodology_update TO gs_assurance_db_writer;
