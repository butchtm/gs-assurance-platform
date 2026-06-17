INSERT INTO gs.project_classification
    (id, code, description, is_active)
VALUES
    (10, 'REAL_CASE_VPA_CROSS_LINKAGE', 'Real Case VPA (cross linkage)', false),
    (11, 'REGULAR_VPA_CROSS_LINKAGE', 'Regular Case VPA (cross linkage)', false)
ON CONFLICT (id) DO UPDATE
SET
    code = EXCLUDED.code,
    description = EXCLUDED.description,
    is_active = EXCLUDED.is_active;

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
SELECT
    batch_mapping.batch_type_id,
    batch_mapping.request_type_id,
    cross_link_classification.id
FROM gs.batch_mapping batch_mapping
INNER JOIN gs.project_classification source_classification
    ON source_classification.id = batch_mapping.project_classification_id
INNER JOIN gs.project_classification cross_link_classification
    ON cross_link_classification.code = 'REAL_CASE_VPA_CROSS_LINKAGE'
WHERE source_classification.code = 'REAL_CASE_VPA'
ON CONFLICT DO NOTHING;

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
SELECT
    batch_mapping.batch_type_id,
    batch_mapping.request_type_id,
    cross_link_classification.id
FROM gs.batch_mapping batch_mapping
INNER JOIN gs.project_classification source_classification
    ON source_classification.id = batch_mapping.project_classification_id
INNER JOIN gs.project_classification cross_link_classification
    ON cross_link_classification.code = 'REGULAR_VPA_CROSS_LINKAGE'
WHERE source_classification.code = 'REGULAR_VPA'
ON CONFLICT DO NOTHING;
