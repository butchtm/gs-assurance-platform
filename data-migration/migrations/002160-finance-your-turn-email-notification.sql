ALTER TABLE gs.request_status_notification ADD finance_team_template_name varchar NULL;
ALTER TABLE gs.request_status_notification ADD finance_team_template_version varchar NULL;

UPDATE gs.request_status_notification SET
  finance_team_template_name = 'YOUR_TURN',
  finance_team_template_version = 'v0.0.3'
WHERE
    to_status_id IN (10, 13);
