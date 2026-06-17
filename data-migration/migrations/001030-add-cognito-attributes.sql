ALTER TABLE gs.user
ADD COLUMN email_verified bool NULL;

ALTER TABLE gs.user
ADD COLUMN groups varchar[] NULL;