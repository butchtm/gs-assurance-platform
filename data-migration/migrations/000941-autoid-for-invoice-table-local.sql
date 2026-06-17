-- create the invoice table sequence
CREATE SEQUENCE
  gs.invoice_id_seq
AS
  integer
OWNED BY
  gs.invoice.id;

-- wire the sequence into the id column
ALTER TABLE gs.invoice
ALTER COLUMN id SET DEFAULT nextval('gs.invoice_id_seq');

-- permissions for back-end
GRANT USAGE, SELECT ON gs.invoice_id_seq TO gs_assurance_db_writer;