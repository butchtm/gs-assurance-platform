--
-- Stakeholder Consultation Type
--
CREATE TABLE gs.consultation_type (
	id integer NOT NULL,
	user_type varchar NOT NULL,
	working_days integer NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.consultation_type
	(id, user_type, working_days)
	VALUES (0, 'TAC', 15);

INSERT INTO gs.consultation_type
	(id, user_type, working_days)
	VALUES (1, 'PUBLIC', 10);
