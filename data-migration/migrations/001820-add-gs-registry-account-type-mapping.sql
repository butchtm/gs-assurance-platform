-- Add missing mapping for GS Registry -> Review Assignment mapping --
INSERT INTO gs.assignment_type_account_type
VALUES (1,2);

UPDATE gs.account_type
SET is_active = true
WHERE code = 'GOLD-STANDARD';