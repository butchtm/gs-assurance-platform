ALTER TABLE gs.request
ADD submitted_by_user_id INT4 NULL;

ALTER TABLE gs.request
ADD FOREIGN KEY (submitted_by_user_id) REFERENCES gs.user ON DELETE RESTRICT;