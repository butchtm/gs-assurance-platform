ALTER TABLE gs.document
ADD COLUMN uploaded_by_user_id int4 NULL;

ALTER TABLE gs.document ADD CONSTRAINT fk_uploaded_by_user_id FOREIGN KEY (uploaded_by_user_id) REFERENCES gs.user (id);