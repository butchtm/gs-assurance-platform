UPDATE gs.account
SET is_active = true
WHERE is_active is NULL;

ALTER TABLE gs.account
ALTER COLUMN is_active SET NOT NULL;