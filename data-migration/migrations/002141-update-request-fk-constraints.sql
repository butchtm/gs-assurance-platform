ALTER TABLE gs.batch
  DROP CONSTRAINT IF EXISTS batch_request_parent_request_id_fkey,
  ADD CONSTRAINT batch_request_parent_request_id_fkey
    FOREIGN KEY (parent_request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.batch_request 
  DROP CONSTRAINT IF EXISTS batch_request_request_id_fkey,
  ADD CONSTRAINT batch_request_request_id_fkey 
  FOREIGN KEY (request_id) 
  REFERENCES gs.request(id) 
  ON UPDATE CASCADE
  ON DELETE RESTRICT;

ALTER TABLE gs.request_activity
  DROP CONSTRAINT IF EXISTS fk_request,
  ADD CONSTRAINT fk_request
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs."comment"
  DROP CONSTRAINT IF EXISTS comment_request_id_fkey,
  ADD CONSTRAINT comment_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.request_round
  DROP CONSTRAINT IF EXISTS request_round_request_id_fkey,
  ADD CONSTRAINT request_round_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.request_status_history
  DROP CONSTRAINT IF EXISTS request_status_history_request_id_fkey,
  ADD CONSTRAINT request_status_history_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.request_document
  DROP CONSTRAINT IF EXISTS request_document_request_id_fkey,
  ADD CONSTRAINT request_document_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.stakeholder_consultation
  DROP CONSTRAINT IF EXISTS stakeholder_consultation_request_id_fkey,
  ADD CONSTRAINT stakeholder_consultation_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.request_account_assignment
  DROP CONSTRAINT IF EXISTS request_account_assignment_request_id_fkey,
	ADD CONSTRAINT request_account_assignment_request_id_fkey
		FOREIGN KEY (request_id)
		REFERENCES gs.request(id)
		ON UPDATE CASCADE
		ON DELETE RESTRICT;

ALTER TABLE gs.base_project_request
  DROP CONSTRAINT IF EXISTS base_project_request_request_id_fkey,
  ADD CONSTRAINT base_project_request_request_id_fkey
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;

ALTER TABLE gs.request_methodology
  DROP CONSTRAINT IF EXISTS fk_request,
  ADD CONSTRAINT fk_request
    FOREIGN KEY (request_id)
    REFERENCES gs.request (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT;