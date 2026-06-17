-- Rename existing column first
ALTER TABLE gs.pii_metadata
RENAME COLUMN actioned_by_id TO actioned_by_account_id;

--Alter pii metadata table to add new columns
ALTER TABLE gs.pii_metadata
ADD COLUMN actioned_by_user_id integer NULL,
ADD COLUMN comment_id integer NULL;

-- Add foreign key constraints
ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_actioned_by_user_id
FOREIGN KEY (actioned_by_user_id)
REFERENCES gs."user"(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_comment_id
FOREIGN KEY (comment_id)
REFERENCES gs.comment(id);
