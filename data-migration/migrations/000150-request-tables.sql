CREATE TABLE gs.request (
	id int4 NOT NULL,
	start_date timestamp NULL,
	end_date timestamp NULL,
	booking_date timestamp NULL,
	request_type_id int4 NOT NULL,
	index int4 NOT NULL,
	request_name varchar NOT NULL,
	last_request_status_change_date timestamp NULL,
	current_request_status_id int4 NOT NULL,
	crediting_period int4 NULL, -- TODO: should this be a foreign key to the project crediting period?
	invoice_id int4 NULL,
	same_request_type_count int4 NOT NULL,
	request_document_status int4 NULL, -- values {0, 1} - should this be an enum or a bool
	replacement_request_id int4 NULL, -- always null - remove?
	emission_reduction_amount int4 NULL,
	manually_added bool NOT NULL,
	migrated bool NOT NULL DEFAULT false,
    PRIMARY KEY (id)
);

ALTER TABLE gs.request
ADD FOREIGN KEY (request_type_id) REFERENCES gs.request_type ON DELETE RESTRICT;

ALTER TABLE gs.request
ADD FOREIGN KEY (current_request_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request
ADD FOREIGN KEY (invoice_id) REFERENCES gs.invoice ON DELETE RESTRICT;

CREATE TABLE gs.base_project_request (
	project_id int4 NOT NULL,
	request_id int4 NOT NULL,
    PRIMARY KEY (project_id, request_id)
);

ALTER TABLE gs.base_project_request
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.base_project_request
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

CREATE TABLE gs.request_status_history (
	id int4 NOT NULL,
	request_id int4 NOT NULL,
	previous_status_id int4 NOT NULL,
	next_status_id int4 NOT NULL,
	changed_by uuid NOT NULL,
	changed_at timestamp NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE gs.request_status_history
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.request_status_history
ADD FOREIGN KEY (previous_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request_status_history
ADD FOREIGN KEY (next_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

-- TODO: Duplicate status changes for a given request e.g.  (request_id, previous_status_id, next_status_id)=(20299, 0, 1) already exists
-- ALTER TABLE gs.request_status_history
-- ADD UNIQUE (request_id, previous_status_id, next_status_id);

CREATE TABLE gs.request_document (
	request_id int4 NOT NULL,
	document_id uuid NOT NULL,
	PRIMARY KEY (request_id, document_id)
);

ALTER TABLE gs.request_document
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.request_document
ADD FOREIGN KEY (document_id) REFERENCES gs.document ON DELETE RESTRICT;
