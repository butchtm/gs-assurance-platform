-- Update Account_Type to include an 'in_active' boolean with active as default
ALTER TABLE gs.account_type
ADD COLUMN is_active bool NOT NULL DEFAULT false;

-- mark all account as active, except 'GOLD-STANDARD'
UPDATE gs.account_type
SET is_active = true
WHERE code != 'GOLD-STANDARD';
