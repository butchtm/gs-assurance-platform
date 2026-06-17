--
-- Request status to notification template
--
DROP TABLE gs.request_status_notification;

CREATE TABLE gs.request_status_notification (
    id int4 NOT NULL,
    min_round_index int4 NOT NULL,
    max_round_index int4 NOT NULL,
    from_status_id int4 NULL,
    to_status_id int4 NULL,
    request_owner_template_name varchar NULL,
    request_owner_template_version varchar NULL,
    arm_team_template_name varchar NULL,
    arm_team_template_version varchar NULL,
    registry_team_template_name varchar NULL,
    registry_team_template_version varchar NULL,
    stakeholder_template_name varchar NULL,
    stakeholder_template_version varchar NULL,
    PRIMARY KEY (id),
    CONSTRAINT valid_min_round_index CHECK (min_round_index >= 0 AND min_round_index <= max_round_index),
    CONSTRAINT valid_max_round_index CHECK (max_round_index >= 0 AND max_round_index >= min_round_index),
    UNIQUE (
        min_round_index,
        max_round_index,
        from_status_id,
        to_status_id
    )
);

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (from_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (to_status_id) REFERENCES gs.request_status ON DELETE RESTRICT;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (request_owner_template_name, request_owner_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (arm_team_template_name, arm_team_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (registry_team_template_name, registry_team_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (stakeholder_template_name, stakeholder_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

GRANT SELECT ON gs.request_status_notification TO gs_assurance_db_writer;

CREATE TABLE gs.request_status_assignee_notification (
    id serial NOT NULL,
    request_status_notification_id int4 NOT NULL,
    assignment_type_id int4 NOT NULL,
    assignee_template_name varchar NOT NULL,
    assignee_template_version varchar NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (
      request_status_notification_id,
      assignment_type_id
    )
);

ALTER TABLE gs.request_status_assignee_notification
ADD FOREIGN KEY (request_status_notification_id) REFERENCES gs.request_status_notification ON DELETE RESTRICT;

ALTER TABLE gs.request_status_assignee_notification
ADD FOREIGN KEY (assignment_type_id) REFERENCES gs.assignment_type ON DELETE RESTRICT;

ALTER TABLE gs.request_status_assignee_notification
ADD FOREIGN KEY (assignee_template_name, assignee_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

GRANT SELECT ON gs.request_status_assignee_notification TO gs_assurance_db_writer;


--
-- IN PREPARATION notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (10, 1, 99, (SELECT id from gs.request_status WHERE code = 'COMPLETENESS_CHECK'), (SELECT id from gs.request_status WHERE code = 'IN_PREPARATION'),
   'COMPLETENESS_CHECK_FINDINGS', 'v0.0.2',
   null, null,
   null, null,
   null, null)
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (20, 1, 99, (SELECT id from gs.request_status WHERE code = 'IN_ROUND_COMPLETION'), (SELECT id from gs.request_status WHERE code = 'IN_PREPARATION'),
   'REVIEW_FEEDBACK_UPLOADED', 'v0.0.2',
   null, null,
   null, null,
   null, null)
;

--
-- IN AUDIT notifications
--
 
INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (30, 0, 0, null, (SELECT id from gs.request_status WHERE code = 'IN_AUDIT'),
   null, null,
   null, null,
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      30, id, 'VVB_ASSIGNMENT', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('AUDIT');
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (40, 1, 99, (SELECT id from gs.request_status WHERE code = 'COMPLETENESS_CHECK'), (SELECT id from gs.request_status WHERE code = 'IN_AUDIT'),
   'COMPLETENESS_CHECK_FINDINGS', 'v0.0.2',
   null, null,
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      40, id, 'COMPLETENESS_CHECK_FINDINGS', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('AUDIT');
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (50, 1, 99, (SELECT id from gs.request_status WHERE code = 'IN_ROUND_COMPLETION'), (SELECT id from gs.request_status WHERE code = 'IN_AUDIT'),
   'REVIEW_FEEDBACK_UPLOADED', 'v0.0.2',
   null, null,
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      50, id, 'REVIEW_FEEDBACK_UPLOADED', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('AUDIT');
;

--
-- COMPLETENESS_CHECK notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (60, 0, 0, null, (SELECT id from gs.request_status WHERE code = 'COMPLETENESS_CHECK'),
   'REQUEST_RECEIVED', 'v0.0.2',
   'YOUR_TURN', 'v0.0.2',
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      60, id, 'REQUEST_RECEIVED', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('AUDIT');
;

--
-- IN ASSIGNMENT notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (70, 0, 0, null, (SELECT id from gs.request_status WHERE code = 'IN_ASSIGNMENT'),
   'REVIEW_INITIATED', 'v0.0.2',
   'REVIEW_INITIATED', 'v0.0.2',
   null, null,
   'REVIEW_INITIATED', 'v0.0.2')
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      70, id, 'REVIEW_INITIATED', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('AUDIT');
;

--
-- IN PROGRESS notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (80, 0, 0, null, (SELECT id from gs.request_status WHERE code = 'IN_PROGRESS'),
   null, null,
   null, null,
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      80, id, 'YOUR_TURN', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('REVIEW', 'COLLECT_FEE');
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (90, 1, 99, null, (SELECT id from gs.request_status WHERE code = 'IN_PROGRESS'),
   'REVIEW_INITIATED', 'v0.0.2',
   'REVIEW_INITIATED', 'v0.0.2',
   null, null,
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      90, id, 'REVIEW_INITIATED', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('REVIEW', 'AUDIT');
;

--
-- IN ROUND COMPLETION notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (100, 0, 99, null, (SELECT id from gs.request_status WHERE code = 'IN_ROUND_COMPLETION'),
   null, null,
   'YOUR_TURN', 'v0.0.2',
   null, null,
   null, null)
;

--
-- COMPLETED APPROVED notifications
--

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version)
VALUES
  (110, 0, 99, null, (SELECT id from gs.request_status WHERE code = 'COMPLETED_APPROVED'),
   'REQUEST_CLOSED', 'v0.0.2',
   null, null,
   'REQUEST_CLOSED', 'v0.0.2',
   null, null)
;

INSERT INTO gs.request_status_assignee_notification (request_status_notification_id, assignment_type_id, assignee_template_name, assignee_template_version)
SELECT      110, id, 'REQUEST_CLOSED', 'v0.0.2'
FROM        gs.assignment_type at1
WHERE       at1.code IN ('REVIEW', 'AUDIT');
;
