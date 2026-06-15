DROP TABLE IF EXISTS gs.batch_type;
CREATE TABLE gs.batch_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

GRANT SELECT ON gs.batch_type TO gs_assurance_db_writer;

CREATE TABLE gs.batch_mapping (
    batch_type_id integer NOT NULL,
    request_type_id integer NOT NULL,
    project_classification_id integer NOT NULL,
    PRIMARY KEY(batch_type_id, request_type_id, project_classification_id)
);

ALTER TABLE gs.batch_mapping
ADD FOREIGN KEY (batch_type_id) REFERENCES gs.batch_type ON DELETE RESTRICT;

ALTER TABLE gs.batch_mapping
ADD FOREIGN KEY (request_type_id) REFERENCES gs.request_type ON DELETE RESTRICT;

ALTER TABLE gs.batch_mapping
ADD FOREIGN KEY (project_classification_id) REFERENCES gs.project_classification ON DELETE RESTRICT;

GRANT SELECT ON gs.batch_mapping TO gs_assurance_db_writer;

CREATE TABLE gs.batch_request (
    id integer NOT NULL,
    batch_type_id integer NOT NULL,
    request_id integer NOT NULL,
    project_id integer NOT NULL,
    parent_request_id integer NULL,
    PRIMARY KEY(id),
    UNIQUE(project_id, request_id)
);

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (batch_type_id) REFERENCES gs.batch_type ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (parent_request_id) REFERENCES gs.request ON DELETE RESTRICT;

GRANT SELECT, INSERT, UPDATE ON gs.batch_request TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.seq_batch_request
AS
  integer
OWNED BY
  gs.batch_request.id;

ALTER TABLE gs.batch_request
ALTER COLUMN id SET DEFAULT nextval('gs.seq_batch_request');

GRANT USAGE, SELECT ON gs.seq_batch_request TO gs_assurance_db_writer;