--
-- Known Batch types
--
INSERT INTO gs.batch_type
	(id, code, description)
	VALUES (0,'POA_PLUS_ONE_REAL_CASE','PoA + 1 Real Case');

INSERT INTO gs.batch_type
    (id, code, description)
    VALUES (1,'POA_PLUS_ONE_OR_MORE_REAL_CASE','PoA + 1 or more Real Case');

INSERT INTO gs.batch_type
    (id, code, description)
    VALUES (2,'REAL_CASE_PLUS_ONE_OR_MORE_REGULAR_CASE','Real Case + 1 or more Regular Case');

INSERT INTO gs.batch_type
    (id, code, description)
    VALUES (3,'TWO_OR_MORE_REGULAR_CASE','2 or more Regular Case');

--
-- Batch Mappings
--
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'PRELIMINARY_REVIEW' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (0,
        (SELECT id FROM gs.request_type WHERE code = 'DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (1,
        (SELECT id FROM gs.request_type WHERE code = 'PRELIMINARY_REVIEW' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
     VALUES (1,
        (SELECT id FROM gs.request_type WHERE code = 'DESIGN_CHANGE_|_WITH_VVB'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (1,
        (SELECT id FROM gs.request_type WHERE code = 'GENERIC'),
        (SELECT id FROM gs.project_classification WHERE code = 'PROGRAMME_OF_ACTIVITIES'));

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'PRELIMINARY_REVIEW' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'INCLUSION_REGULAR_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'PERFORMANCE_CERTIFICATION_|_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'RENEWAL_|_REGULAR_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'DESIGN_CHANGE_|_WITH_VVB'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'ISSUANCE'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'GENERIC'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (2,
        (SELECT id FROM gs.request_type WHERE code = 'ANNUAL_REPORT'),
        (SELECT id FROM gs.project_classification WHERE code = 'REAL_CASE_VPA'));

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'PRELIMINARY_REVIEW' AND is_active = true),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'INCLUSION_REGULAR_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'PERFORMANCE_CERTIFICATION_|_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'RENEWAL_|_REGULAR_VPA'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'DESIGN_CHANGE_|_WITH_VVB'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'ISSUANCE'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'GENERIC'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
    VALUES (3,
        (SELECT id FROM gs.request_type WHERE code = 'ANNUAL_REPORT'),
        (SELECT id FROM gs.project_classification WHERE code = 'REGULAR_VPA'));
