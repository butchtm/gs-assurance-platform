--Dropping old fk request->invoice constraints adding new one on cascade update

ALTER TABLE gs.request
DROP CONSTRAINT IF EXISTS request_invoice_id_fkey,
DROP CONSTRAINT IF EXISTS request_invoice_id_fkey1,
ADD CONSTRAINT request_invoice_id_fkey
FOREIGN KEY (invoice_id)
REFERENCES gs.invoice (id)
ON UPDATE CASCADE
ON DELETE RESTRICT;