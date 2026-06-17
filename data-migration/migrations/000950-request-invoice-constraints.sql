ALTER TABLE gs.request
ADD FOREIGN KEY (invoice_id) REFERENCES gs.invoice ON DELETE RESTRICT;

-- partial index to restrict invoice to a single request
CREATE UNIQUE INDEX request_uniq_invoice
ON gs.request (invoice_id)
WHERE invoice_id IS NOT NULL;