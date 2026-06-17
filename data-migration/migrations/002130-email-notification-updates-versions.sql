UPDATE gs.request_status_notification
SET request_owner_template_version = 'v0.0.3'
WHERE
    request_owner_template_name = 'REVIEW_INITIATED';
