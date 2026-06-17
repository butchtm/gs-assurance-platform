-- Update request status transition table to include boolean with permitted during consultation true as default
ALTER TABLE gs.request_status_transition
ADD COLUMN permitted_during_consultation bool NOT NULL DEFAULT true;

-- Mark IN_PROGRESS as false when it attempts to go into a terminal state
UPDATE gs.request_status_transition
SET permitted_during_consultation = false
WHERE request_status_id = '10'
AND (permitted_request_status_id = '4'
 OR permitted_request_status_id = '5'
 OR permitted_request_status_id = '7'
 OR permitted_request_status_id = '8');

-- Mark IN_ASSIGNMENT as false when it attempts to go into a terminal state
UPDATE gs.request_status_transition
SET permitted_during_consultation = false
WHERE request_status_id = '12'
AND (permitted_request_status_id = '4'
 OR permitted_request_status_id = '5'
 OR permitted_request_status_id = '7'
 OR permitted_request_status_id = '8');

-- Mark IN_ROUND_COMPLETION as false when it attempts to go into a terminal state
UPDATE gs.request_status_transition
SET permitted_during_consultation = false
WHERE request_status_id = '13'
AND (permitted_request_status_id = '4'
 OR permitted_request_status_id = '5'
 OR permitted_request_status_id = '7'
 OR permitted_request_status_id = '8');
