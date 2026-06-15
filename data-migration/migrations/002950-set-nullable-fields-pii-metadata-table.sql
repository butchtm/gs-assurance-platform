ALTER TABLE gs.pii_metadata
ALTER COLUMN submitted_by_user_id drop NOT NULL;

ALTER TABLE gs.pii_metadata
ALTER COLUMN submitted_by_account_id drop NOT NULL;

ALTER TABLE gs.pii_metadata
ALTER COLUMN type_detail drop NOT NULL;