INSERT INTO gs.request_type_assignment_type
(request_type_id, assignment_type_id, mandatory)
VALUES
-- POA Design Consultation
(0, 1, false),
(0, 2, false),
(0, 3, true),
(0, 4, true),
-- Preliminary Review
(1, 1, false),
(1, 2, false),
(1, 3, true),
(1, 4, true),
-- Internal Validation
(2, 1, true),
(2, 2, false),
(2, 3, true),
(2, 4, true),
-- Design Review
(3, 0, true),
(3, 1, true),
(3, 2, false),
(3, 3, true),
(3, 4, true),
-- Internal Verification
(4, 1, true),
(4, 2, false),
(4, 3, true),
(4, 4, true),
-- Performance Review
(5, 0, true),
(5, 1, true),
(5, 2, false),
(5, 3, true),
(5, 4, true),
-- Crediting Period Renewal
(6, 0, true),
(6, 1, true),
(6, 2, false),
(6, 3, true),
(6, 4, true),
-- Design Change Review
(7, 0, true),
(7, 1, true),
(7, 2, false),
(7, 3, true),
(7, 4, true),
-- Transition Review (Previous GS Version to GS4GG)
(8, 0, false),
(8, 1, true),
(8, 2, false),
(8, 3, true),
(8, 4, true),
-- Design Certification Renewal
(9, 0, true),
(9, 1, true),
(9, 2, false),
(9, 3, true),
(9, 4, true),
-- Preliminary Review (transition)
(13, 1, false),
(13, 2, false),
(13, 3, true),
(13, 4, true),
-- Transition Review (GS CDM to GS VER)
(14, 0, false),
(14, 1, true),
(14, 2, false),
(14, 3, true),
(14, 4, true),
-- Preliminary Review
(15, 1, false),
(15, 2, false),
(15, 3, true),
(15, 4, true),
-- POA Preliminary Review
(16, 1, false),
(16, 2, false),
(16, 3, true),
(16, 4, true);

INSERT INTO gs.project_status_update_mapping
(request_type_id, request_status_id, project_status_id)
VALUES
-- POA Design Consultation
(0, 4, 2),
-- Preliminary Review
(1, 4, 2),
-- Design Review
(3, 4, 3),
-- Performance Review
(5, 4, 4),
-- Transition Review (Previous GS Version to GS4GG)
(8, 4, 2),
-- Preliminary Review (transition)
(13, 4, 2),
-- Transition Review (GS CDM to GS VER)
(14, 4, 2),
-- Preliminary Review
(15, 4, 2),
-- POA Preliminary Review
(16, 4, 2);