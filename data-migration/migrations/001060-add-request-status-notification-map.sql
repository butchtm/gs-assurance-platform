--
-- Request status to notification template
--
CREATE TABLE gs.request_status_notification (
    id int4 NOT NULL,
    min_round_index int4 NOT NULL,
    max_round_index int4 NOT NULL,
    from_status_id int4 NULL,
    to_status_id int4 NULL,
    template_id int4 NOT NULL,
    notify_request_owner bool NOT NULL,
    notify_assignment_type_ids int4[] NOT NULL, /* mvp, no RI, just array of ids */
    notify_arm_team bool NOT NULL,
    notify_registry_team bool NOT NULL,
    notify_stakeholders bool NOT NULL,
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
ADD FOREIGN KEY (template_id) REFERENCES gs.notification_template ON DELETE RESTRICT;

GRANT SELECT ON gs.request_status_notification TO gs_assurance_db_writer;

CREATE TABLE pg_temp.request_status_notification (
    id int4 NOT NULL,
    min_round_index int4 NOT NULL,
    max_round_index int4 NOT NULL,
    from_status_code varchar NULL,
    to_status_code varchar NULL,
    template_name varchar NOT NULL,
    template_version varchar NOT NULL,
    notify_request_owner bool NOT NULL,
    notify_assignment_types varchar[] NOT NULL,
    notify_arm_team bool NOT NULL,
    notify_registry_team bool NOT NULL,
    notify_stakeholders bool NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (
      min_round_index,
      max_round_index,
      from_status_code,
      to_status_code
    )
);

INSERT INTO pg_temp.request_status_notification
  (id, min_round_index, max_round_index, from_status_code, to_status_code, template_name, template_version, notify_request_owner, notify_assignment_types, notify_arm_team, notify_stakeholders, notify_registry_team)
VALUES
   (1, 1, 99, 'COMPLETENESS_CHECK', 'IN_PREPARATION', 'COMPLETENESS_CHECK_FINDINGS', 'v0.0.1', true, ARRAY[]::varchar[], false, false, false)
  ,(2, 1, 99, 'IN_ROUND_COMPLETION', 'IN_PREPARATION', 'REVIEW_FEEDBACK_UPLOADED', 'v0.0.1', true, ARRAY[]::varchar[], false, false, false) 
  ,(3, 0, 0, null, 'IN_AUDIT', 'VVB_ASSIGNMENT', 'v0.0.1', false, ARRAY['AUDIT'], false, false, false)
  ,(4, 1, 99, 'COMPLETENESS_CHECK', 'IN_AUDIT', 'COMPLETENESS_CHECK_FINDINGS', 'v0.0.1', true, ARRAY['AUDIT'], false, false, false)
  ,(5, 1, 99, 'IN_ROUND_COMPLETION', 'IN_AUDIT', 'REVIEW_FEEDBACK_UPLOADED', 'v0.0.1', true, ARRAY['AUDIT'], false, false, false)
  ,(6, 0, 0, null, 'COMPLETENESS_CHECK', 'REQUEST_RECEIVED', 'v0.0.1', false, ARRAY['COMPLETENESS_CHECK'], false, false, false)
  ,(7, 0, 0, null, 'IN_ASSIGNMENT', 'REVIEW_INITIATED', 'v0.0.1', true, ARRAY['AUDIT'], true, false, false)
  ,(8, 0, 0, null, 'IN_PROGRESS', 'YOUR_TURN', 'v0.0.1', false, ARRAY['REVIEW', 'COLLECT_FEE'], false, false, false)
  ,(9, 1, 99, null, 'IN_PROGRESS', 'YOUR_TURN', 'v0.0.1', true, ARRAY['REVIEW', 'AUDIT'], true, false, false)
  ,(10, 0, 99, null, 'IN_ROUND_COMPLETION', 'YOUR_TURN', 'v0.0.1', false, ARRAY[]::varchar[], true, false, false)
  ,(11, 0, 99, null, 'COMPLETED_APPROVED', 'REQUEST_CLOSED', 'v0.0.1', true, ARRAY['AUDIT', 'REVIEW'], false, false, true)
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id, template_id, notify_request_owner, notify_assignment_type_ids, notify_arm_team, notify_stakeholders, notify_registry_team)
SELECT
  rsn.id,
  rsn.min_round_index,
  rsn.max_round_index,
  rsf.id,
  rst.id,
  nt.id,
  rsn.notify_request_owner,
  ARRAY(SELECT at1.id FROM UNNEST(rsn.notify_assignment_types) assignment_type LEFT JOIN gs.assignment_type at1 ON assignment_type = at1.code),
  rsn.notify_arm_team,
  rsn.notify_stakeholders,
  rsn.notify_registry_team
FROM
  pg_temp.request_status_notification rsn
LEFT JOIN
  gs.request_status rsf
ON
  rsn.from_status_code = rsf.code
LEFT JOIN
  gs.request_status rst
ON
  rsn.to_status_code = rst.code
LEFT JOIN
  gs.notification_template nt
ON
  rsn.template_name = nt.name AND
  rsn.template_version = nt.version
;
