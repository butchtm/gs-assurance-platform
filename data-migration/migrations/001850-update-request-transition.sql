UPDATE gs.request_status_transition
SET permitted_during_consultation = true
WHERE request_status_id = 13 AND permitted_request_status_id = 10;