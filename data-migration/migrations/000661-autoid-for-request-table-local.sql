-- create the request table sequence
CREATE SEQUENCE
  gs.seq_request
AS
  integer
OWNED BY
  gs.request.id;

-- wire the sequence into the id column
ALTER TABLE gs.request
ALTER COLUMN id SET DEFAULT nextval('gs.seq_request');