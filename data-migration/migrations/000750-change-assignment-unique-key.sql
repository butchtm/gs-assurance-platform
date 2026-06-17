ALTER TABLE gs.request_account_assignment
DROP CONSTRAINT request_account_assignment_request_id_account_id_assignment_key;

-- Only one account can be assigned a given role for a request
ALTER TABLE gs.request_account_assignment
ADD UNIQUE (request_id, assignment_type_id);
