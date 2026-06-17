--
-- Batch Mapping For POA with Transition Review request type.
--
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (1,
        (SELECT id FROM gs.request_type WHERE code = 'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));