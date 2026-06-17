ALTER TABLE gs.form_definition DROP CONSTRAINT fk_form_status_id;

ALTER TABLE gs.form_definition
ADD CONSTRAINT fk_form_definition_status_id
FOREIGN KEY (status_id)
REFERENCES gs.form_definition_status(id);

DROP TABLE gs.form_status;