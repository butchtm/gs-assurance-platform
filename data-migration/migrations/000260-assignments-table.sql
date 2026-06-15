CREATE TABLE gs.assignment_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (0,'AUDIT','Audit');

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (1,'REVIEW','Review');

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (2,'QA/QC','QA/QC');

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (3,'COLLECT_FEE','Collect Fee');

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (4,'COMPLETENESS_CHECK','Completeness Check');

INSERT INTO gs.assignment_type
	(id, code, description)
	VALUES (5,'STAKEHOLDER_CONSULTATION','Stakeholder Consultation');

CREATE TABLE gs.assignment_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.assignment_status
	(id, code, description)
	VALUES (0,'PENDING','Pending');

INSERT INTO gs.assignment_status
	(id, code, description)
	VALUES (1,'ASSIGNED','Assigned');

INSERT INTO gs.assignment_status
	(id, code, description)
	VALUES (2,'CANCELLED','Cancelled');

CREATE TABLE gs.request_account_assignment (
	request_id int4 NOT NULL,
	account_id int4 NOT NULL,
	assignment_type_id int4 NOT NULL,
	assignment_status_id int4 NOT NULL,
	created_time timestamp NOT NULL,
	updated_time timestamp NULL,
	PRIMARY KEY (request_id, account_id, assignment_type_id)
);

ALTER TABLE gs.request_account_assignment
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.request_account_assignment
ADD FOREIGN KEY (account_id) REFERENCES gs.account ON DELETE RESTRICT;

ALTER TABLE gs.request_account_assignment
ADD FOREIGN KEY (assignment_type_id) REFERENCES gs.assignment_type ON DELETE RESTRICT;

ALTER TABLE gs.request_account_assignment
ADD FOREIGN KEY (assignment_status_id) REFERENCES gs.assignment_status ON DELETE RESTRICT;
