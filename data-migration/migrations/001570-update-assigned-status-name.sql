--- Update assignment status from ASSIGNED to ACCEPTED ---
UPDATE gs.assignment_status
SET code = 'ACCEPTED', description = 'Accepted'
WHERE id = 1;