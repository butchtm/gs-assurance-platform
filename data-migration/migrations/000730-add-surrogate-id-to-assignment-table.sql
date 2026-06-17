ALTER TABLE IF EXISTS gs.request_account_assignment
ADD UNIQUE (request_id, account_id, assignment_type_id);

ALTER TABLE gs.request_account_assignment
DROP CONSTRAINT request_account_assignment_pkey;

ALTER TABLE gs.request_account_assignment
ADD COLUMN id serial NOT NULL;

ALTER TABLE gs.request_account_assignment
ADD PRIMARY KEY (id);

GRANT USAGE, SELECT ON gs.request_account_assignment_id_seq TO gs_assurance_db_writer;