ALTER TABLE gs.project_methodology_update
 ADD COLUMN request_id integer NULL,
 ADD CONSTRAINT request_fkey
  FOREIGN KEY (request_id)
  REFERENCES gs.request(id)
  ON DELETE RESTRICT;
