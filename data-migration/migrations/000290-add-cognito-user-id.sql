ALTER TABLE gs.user
ADD COLUMN idp_user_id uuid NOT NULL;

ALTER TABLE gs.user
ADD UNIQUE (idp_user_id);