UPDATE gs.request_status_assignee_notification
SET assignee_template_version = 'v0.0.3'
WHERE
    assignee_template_name IN ('YOUR_TURN', 'REVIEW_INITIATED', 'REVIEW_ASSIGNED', 'CONSULTATION_STARTED', 'COMMENT_RECEIVED');

UPDATE gs.request_status_notification
SET arm_team_template_version = 'v0.0.3'
WHERE
    arm_team_template_name IN ('YOUR_TURN', 'REVIEW_INITIATED', 'REVIEW_ASSIGNED', 'CONSULTATION_STARTED', 'COMMENT_RECEIVED');

UPDATE gs.request_status_notification
SET stakeholder_template_version = 'v0.0.3'
WHERE
    stakeholder_template_name IN ('YOUR_TURN', 'REVIEW_INITIATED', 'REVIEW_ASSIGNED', 'CONSULTATION_STARTED', 'COMMENT_RECEIVED');
