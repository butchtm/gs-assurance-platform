ALTER TABLE gs.request
ADD COLUMN batch_request_id integer NULL,
ADD FOREIGN KEY (batch_request_id) REFERENCES gs.batch_request ON DELETE RESTRICT;
