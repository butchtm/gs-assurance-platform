-- create the project table gsid sequence
CREATE SEQUENCE
  gs.seq_project_gsid
AS
  integer
OWNED BY
  gs.project.id;

-- default gsid to 'GS'-prefixed next value of the sequence
ALTER TABLE gs.project
ALTER COLUMN gsid SET DEFAULT ('GS' || nextval('gs.seq_project_gsid'));


