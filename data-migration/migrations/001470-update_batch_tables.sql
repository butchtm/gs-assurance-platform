ALTER TABLE gs.batch_request
RENAME TO batch;

ALTER TABLE gs.request
DROP COLUMN batch_request_id;

ALTER TABLE gs.batch
DROP COLUMN request_id;

ALTER TABLE gs.batch
DROP COLUMN project_id;

ALTER TABLE gs.request
ADD COLUMN batch_id integer NULL,
ADD FOREIGN KEY (batch_id) REFERENCES gs.batch ON DELETE RESTRICT;

ALTER SEQUENCE gs.seq_batch_request RENAME TO seq_batch;

CREATE TABLE gs.batch_request (
    id integer NOT NULL,
    batch_id integer NOT NULL,
    request_id integer NOT NULL,
    project_id integer NOT NULL,
    PRIMARY KEY(id),
    UNIQUE(batch_id, project_id, request_id)
);

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (batch_id) REFERENCES gs.batch ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.batch_request
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

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