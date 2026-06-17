UPDATE gs.request_status_notification
SET request_owner_template_version = 'v0.0.4'
WHERE request_owner_template_name IN ('COMPLETENESS_CHECK_FINDINGS');