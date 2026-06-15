-- only tracking invoice status for mvp: relax other constraints

ALTER TABLE gs.invoice
ALTER COLUMN amount DROP NOT NULL;

ALTER TABLE gs.invoice
ALTER COLUMN is_archived DROP NOT NULL;