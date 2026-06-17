-- create the project table sequence
CREATE SEQUENCE
  gs.seq_project
AS
  integer
OWNED BY
  gs.project.id;

-- wire the sequence into the id column
ALTER TABLE gs.project
ALTER COLUMN id SET DEFAULT nextval('gs.seq_project');