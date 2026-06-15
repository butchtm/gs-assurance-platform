ALTER TABLE gs.request_status_notification
ADD COLUMN vvb_template_name varchar NULL;

ALTER TABLE gs.request_status_notification
ADD COLUMN vvb_template_version varchar NULL;

ALTER TABLE gs.request_status_notification
ADD FOREIGN KEY (vvb_template_name, vvb_template_version) REFERENCES gs.notification_template (name, version) ON DELETE RESTRICT;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version,
   vvb_template_name, vvb_template_version)
VALUES
  (120, 2, 99, (SELECT id from gs.request_status WHERE code = 'IN_PREPARATION'), (SELECT id from gs.request_status WHERE code = 'IN_PROGRESS'),
   'REVIEW_INITIATED', 'v0.0.3',
   null, null,
   null, null,
   null, null,
   'REVIEW_INITIATED', 'v0.0.3')
;

INSERT INTO gs.request_status_notification
  (id, min_round_index, max_round_index, from_status_id, to_status_id,
   request_owner_template_name, request_owner_template_version,
   arm_team_template_name, arm_team_template_version,
   registry_team_template_name, registry_team_template_version,
   stakeholder_template_name, stakeholder_template_version,
   vvb_template_name, vvb_template_version)
VALUES
  (130, 2, 99, (SELECT id from gs.request_status WHERE code = 'IN_AUDIT'), (SELECT id from gs.request_status WHERE code = 'IN_PROGRESS'),
   'REVIEW_INITIATED', 'v0.0.3',
   null, null,
   null, null,
   null, null,
   'REVIEW_INITIATED', 'v0.0.3')
;