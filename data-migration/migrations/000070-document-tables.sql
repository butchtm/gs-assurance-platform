CREATE TABLE gs.document (
	id uuid NOT NULL,
	container_name varchar NOT NULL,
	file_name varchar NOT NULL,
	file_size int8 NOT NULL,
	hash_value bytea,
	access bool NOT NULL,
	category bool NOT NULL,
	document_type_id int4 NOT NULL,
	content_type text NOT NULL,
	document_hash varchar,
	uploaded_timestamp timestamp,
	modified_timestamp timestamp,
	is_latest bool,
	PRIMARY KEY(id)
);

ALTER TABLE gs.document 
ADD FOREIGN KEY (document_type_id) REFERENCES gs.document_type ON DELETE RESTRICT;

ALTER TABLE gs.document
ADD UNIQUE(container_name, file_name);

CREATE TABLE gs.base_project_document (
	project_id int4 NOT NULL,
	document_id uuid NOT NULL,
	PRIMARY KEY(project_id, document_id)
);

ALTER TABLE gs.base_project_document
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.base_project_document
ADD FOREIGN KEY (document_id) REFERENCES gs.document ON DELETE RESTRICT;