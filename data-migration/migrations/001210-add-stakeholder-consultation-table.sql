--
-- Stakeholder Consultation Table
--
CREATE TABLE gs.stakeholder_consultation (
    id integer NOT NULL,
    consultation_type_id integer NOT NULL,
    project_id integer NOT NULL,
    request_id integer NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
	PRIMARY KEY(id),
    UNIQUE (project_id, request_id, consultation_type_id)
);

--
-- Stakeholder Consultation Foreign Keys
--
ALTER TABLE gs.stakeholder_consultation
ADD FOREIGN KEY (consultation_type_id) REFERENCES gs.consultation_type ON DELETE RESTRICT;

ALTER TABLE gs.stakeholder_consultation
ADD FOREIGN KEY (project_id) REFERENCES gs.project ON DELETE RESTRICT;

ALTER TABLE gs.stakeholder_consultation
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;
