CREATE TABLE gs.bundle (
	id int4 NOT NULL,
	name varchar NOT NULL,
	master_project_category_id int4 NOT NULL,
	master_project_id int4 NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE gs.bundle
ADD FOREIGN KEY (master_project_category_id) REFERENCES gs.project_category ON DELETE RESTRICT;

ALTER TABLE gs.bundle
ADD FOREIGN KEY (master_project_id) REFERENCES gs.project ON DELETE RESTRICT;