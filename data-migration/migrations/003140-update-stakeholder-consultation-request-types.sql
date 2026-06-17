UPDATE gs.request_type_consultation
SET
    requires_consultation = true
WHERE
    request_type_id in (1004, 1010)