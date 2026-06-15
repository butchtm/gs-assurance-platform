UPDATE gs.request_type_consultation
SET requires_consultation = false
WHERE request_type_id = 1016; -- Transition Review Request