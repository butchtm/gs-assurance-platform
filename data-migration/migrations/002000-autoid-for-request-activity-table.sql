-- create the request activity table sequence
CREATE SEQUENCE
  gs.seq_request_activity
AS
  integer
OWNED BY
  gs.request_activity.id;

-- wire the sequence into the id column
ALTER TABLE gs.request_activity
ALTER COLUMN id SET DEFAULT nextval('gs.seq_request_activity');

GRANT USAGE, SELECT ON gs.seq_request_activity TO gs_assurance_db_writer;
