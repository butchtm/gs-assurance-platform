--
-- These records were unreachable before as they could only be needed on round 0
--
UPDATE gs.request_status_notification
SET min_round_index = 0
WHERE id in ( 10, 20, 40, 50 );

-- This record is unreachable as it is only needed after round 1 has begun
--
UPDATE gs.request_status_notification
SET max_round_index = 99
WHERE id = 80;

--
-- Moving this record a better placed in the account_status_notification table, and remove non-required records
--
DELETE FROM gs.request_status_assignee_notification
WHERE id in ( 1, 8, 9 );

DELETE FROM gs.request_status_notification
WHERE id = 90;

INSERT INTO gs.assignment_status_notification
VALUES (1,
        (SELECT id FROM gs.assignment_status WHERE code = 'PENDING'),
        (SELECT id FROM gs.assignment_type WHERE code = 'AUDIT'),
        (SELECT id FROM gs.notification_template WHERE name = 'VVB_ASSIGNMENT' AND version = 'v0.0.2')),
       (2,
        (SELECT id FROM gs.assignment_status WHERE code = 'ASSIGNED'),
        (SELECT id FROM gs.assignment_type WHERE code = 'AUDIT'),
        (SELECT id FROM gs.notification_template WHERE name = 'VVB_ASSIGNMENT' AND version = 'v0.0.2'));

