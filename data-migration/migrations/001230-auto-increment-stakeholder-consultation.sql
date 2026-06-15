-- create the table sequence
CREATE SEQUENCE
  gs.seq_stakeholder_consultation
AS
  integer
OWNED BY
  gs.stakeholder_consultation.id;

-- wire the sequence into the id column
ALTER TABLE gs.stakeholder_consultation
ALTER COLUMN id SET DEFAULT nextval('gs.seq_stakeholder_consultation');