CREATE TABLE gs.project_status_history (
	id int4 NOT NULL,
	base_project_id int4 NOT NULL,
	previous_status_id int4 NOT NULL,
	next_status_id int4 NOT NULL,
	PRIMARY KEY (id)
);

ALTER TABLE gs.project_status_history
ADD FOREIGN KEY (previous_status_id) REFERENCES gs.project_status ON DELETE RESTRICT;

ALTER TABLE gs.project_status_history
ADD FOREIGN KEY (next_status_id) REFERENCES gs.project_status ON DELETE RESTRICT;

--ALTER TABLE gs.project_status_history
--ADD UNIQUE (base_project_id, previous_status_id, next_status_id);
