INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_OR_REGULAR_CASE_POA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA'),
        (SELECT id FROM gs.project_classification WHERE code = 'POA'));



INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA_LEGACY'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_CASE_VPA'));



