--
-- create reference table for suppression reasons
--
DROP TABLE IF EXISTS gs.suppression_reason;
CREATE TABLE gs.suppression_reason (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
    is_active bool DEFAULT true NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.suppression_reason
	(id, code, description, is_active)
	VALUES (0, 'OFFENSIVE', 'Comment contains language that is offensive and/or inappropriate.', true);

INSERT INTO gs.suppression_reason
	(id, code, description, is_active)
	VALUES (1, 'NOT_RELEVANT', 'Comment has been deemed as not relevant.', true);

INSERT INTO gs.suppression_reason
	(id, code, description, is_active)
	VALUES (2, 'PRIVATE_REVIEW', 'Comment in review by Admin.', true);

GRANT SELECT ON gs.suppression_reason TO gs_assurance_db_writer;

--
-- update comments table to include suppression and reason
--
ALTER TABLE gs.comment
ADD COLUMN suppressed bool NOT NULL DEFAULT false,
ADD COLUMN suppression_reason_id integer NULL;

--
-- Foreign Keys
--
ALTER TABLE gs.comment
ADD FOREIGN KEY (suppression_reason_id) REFERENCES gs.suppression_reason ON DELETE RESTRICT;

--
-- create reference table for deletion reasons
--
DROP TABLE IF EXISTS gs.deletion_reason;
CREATE TABLE gs.deletion_reason (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	is_active bool DEFAULT true NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.deletion_reason
	(id, code, description, is_active)
	VALUES (0, 'SPAM', 'Comment contained malicious payload, has now been processed and deleted.', true);

INSERT INTO gs.deletion_reason
	(id, code, description, is_active)
	VALUES (1, 'OFFENSIVE_DELETION', 'Comment contained offensive and/or inappropriate language, has now been processed and deleted.', true);

INSERT INTO gs.deletion_reason
	(id, code, description, is_active)
	VALUES (2, 'NOT_RELEVANT_DELETION', 'Comment deemed as not relevant, has now been processed and deleted.', true);

GRANT SELECT ON gs.deletion_reason TO gs_assurance_db_writer;

--
-- update comments table to include deletion and reason
--
ALTER TABLE gs.comment
ADD COLUMN deleted bool NOT NULL DEFAULT false,
ADD COLUMN deletion_reason_id integer NULL;

--
-- Foreign Keys
--
ALTER TABLE gs.comment
ADD FOREIGN KEY (deletion_reason_id) REFERENCES gs.deletion_reason ON DELETE RESTRICT;

--
-- update the view to not include suppressed or deleted comments
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_comments;
CREATE OR REPLACE VIEW gs.v_public_stakeholder_comments
AS SELECT c.id,
    sc.project_id,
    c.submitted_comment,
    c.uploaded_by_name,
    c.uploaded_date,
    c.anonymous,
    c.addressed
   FROM gs.comment c
     LEFT JOIN gs.stakeholder_consultation sc ON c.request_id = sc.request_id
  WHERE sc.consultation_type_id = 1 AND c.public_access = true
    AND c.suppressed = false
    AND c.deleted = false;

GRANT SELECT ON gs.v_public_stakeholder_comments TO gs_assurance_db_writer;
