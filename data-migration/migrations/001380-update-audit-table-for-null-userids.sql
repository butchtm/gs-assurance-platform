ALTER TABLE gs.audit_event
ALTER COLUMN idp_user_id DROP NOT NULL,
ADD COLUMN anonymous bool NULL;