--- Updating Capitalisation of Descriptions  ---
UPDATE gs.request_type
SET description = 'Inclusion Regular VPA'
WHERE id = 1004;

UPDATE gs.fee_schedule
SET description = 'Cash per Credit'
WHERE id = 0;

UPDATE gs.fee_schedule
SET description = 'Share of Proceeds'
WHERE id = 1;

UPDATE gs.invoice_status
SET description = 'Not Triggered'
WHERE id = 0;
