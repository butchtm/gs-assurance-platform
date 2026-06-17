-- Idempotent local demo seed for Proposal 2 cross-linking.
-- This creates normal Beta PoA/Real Case parent data only; no legacy workaround rows are inserted.

INSERT INTO gs.account
    (id, name, email_address, account_type_id, created_time, is_active, address)
VALUES
    (910001, 'Cross Link Alpha PD', 'cross-link-alpha-pd@test', 3, '2024-05-20T00:00:00Z', true, 'Cross Link Alpha PD address'),
    (910002, 'Cross Link Beta PD', 'cross-link-beta-pd@test', 3, '2024-05-20T00:00:00Z', true, 'Cross Link Beta PD address')
ON CONFLICT (id) DO UPDATE
SET
    name = EXCLUDED.name,
    email_address = EXCLUDED.email_address,
    account_type_id = EXCLUDED.account_type_id,
    created_time = EXCLUDED.created_time,
    is_active = EXCLUDED.is_active,
    address = EXCLUDED.address;

INSERT INTO gs."user"
    (id, user_name, email_address, account_id, is_active, idp_user_id, given_name, family_name, email_verified, groups)
VALUES
    (910001, 'cross-link-alpha-pd', 'cross-link-alpha-pd@test', 910001, true, 'b1131111-1111-4111-8111-111111111111', 'Alpha', 'PD', true, ARRAY['Project-Developer']),
    (910002, 'cross-link-beta-pd', 'cross-link-beta-pd@test', 910002, true, 'b1132222-2222-4222-8222-222222222222', 'Beta', 'PD', true, ARRAY['Project-Developer'])
ON CONFLICT (id) DO UPDATE
SET
    user_name = EXCLUDED.user_name,
    email_address = EXCLUDED.email_address,
    account_id = EXCLUDED.account_id,
    is_active = EXCLUDED.is_active,
    idp_user_id = EXCLUDED.idp_user_id,
    given_name = EXCLUDED.given_name,
    family_name = EXCLUDED.family_name,
    email_verified = EXCLUDED.email_verified,
    groups = EXCLUDED.groups;

INSERT INTO gs.project
    (
        id,
        gsid,
        status,
        name,
        description,
        project_classification_id,
        project_start_date,
        registration_type_id,
        has_project_assets,
        current_standard_version_id,
        carbon_stream_id,
        project_size_id,
        project_status_id,
        design_certification_date_manual_set,
        listing_date_manual_set,
        fee_schedule_id,
        estimated_annual_units,
        master_project_id,
        parent_project_id,
        account_id,
        last_activity
    )
VALUES
    (910101, 'GS910101', false, 'Cross Link Beta PoA - New', 'Cross Link Beta PoA with no Real Case', 7, '2024-05-20T00:00:00Z', 0, true, 2, 1, 1, 1, false, false, 0, 1000, null, null, 910002, '2024-05-20T00:00:00Z'),
    (910102, 'GS910102', false, 'Cross Link Beta PoA - Listed', 'Cross Link Beta listed PoA', 7, '2024-05-20T00:00:00Z', 0, true, 2, 1, 1, 2, false, false, 0, 2000, null, null, 910002, '2024-05-20T00:00:00Z'),
    (910103, 'GS910103', false, 'Cross Link Beta PoA - Design Certified', 'Cross Link Beta design certified PoA', 7, '2024-05-20T00:00:00Z', 0, true, 2, 1, 1, 3, false, false, 0, 3000, null, null, 910002, '2024-05-20T00:00:00Z'),
    (910104, 'GS910104', false, 'Cross Link Beta Real Case - ACM0001', 'Cross Link Beta Real Case using ACM0001', 8, '2024-05-20T00:00:00Z', 0, true, 2, 1, 1, 2, false, false, 0, 2100, 910102, 910102, 910002, '2024-05-20T00:00:00Z'),
    (910105, 'GS910105', false, 'Cross Link Beta Real Case - ACM0002', 'Cross Link Beta Real Case using ACM0002', 8, '2024-05-20T00:00:00Z', 0, true, 2, 1, 1, 3, false, false, 0, 3100, 910103, 910103, 910002, '2024-05-20T00:00:00Z')
ON CONFLICT (id) DO UPDATE
SET
    gsid = EXCLUDED.gsid,
    status = EXCLUDED.status,
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    project_classification_id = EXCLUDED.project_classification_id,
    project_start_date = EXCLUDED.project_start_date,
    registration_type_id = EXCLUDED.registration_type_id,
    has_project_assets = EXCLUDED.has_project_assets,
    current_standard_version_id = EXCLUDED.current_standard_version_id,
    carbon_stream_id = EXCLUDED.carbon_stream_id,
    project_size_id = EXCLUDED.project_size_id,
    project_status_id = EXCLUDED.project_status_id,
    design_certification_date_manual_set = EXCLUDED.design_certification_date_manual_set,
    listing_date_manual_set = EXCLUDED.listing_date_manual_set,
    fee_schedule_id = EXCLUDED.fee_schedule_id,
    estimated_annual_units = EXCLUDED.estimated_annual_units,
    master_project_id = EXCLUDED.master_project_id,
    parent_project_id = EXCLUDED.parent_project_id,
    account_id = EXCLUDED.account_id,
    last_activity = EXCLUDED.last_activity;

DELETE FROM gs.project_project_type
WHERE project_id IN (910101, 910102, 910103, 910104, 910105);

INSERT INTO gs.project_project_type
    (project_id, project_type_id)
VALUES
    (910101, 4),
    (910102, 4),
    (910103, 4),
    (910104, 4),
    (910105, 4);

DELETE FROM gs.project_project_asset
WHERE project_id IN (910101, 910102, 910103, 910104, 910105);

INSERT INTO gs.project_project_asset
    (project_id, project_asset_id)
VALUES
    (910101, 0),
    (910102, 0),
    (910103, 0),
    (910104, 0),
    (910105, 0);

DELETE FROM gs.project_project_location
WHERE project_id IN (910101, 910102, 910103, 910104, 910105);

INSERT INTO gs.project_project_location
    (project_id, project_location_id)
VALUES
    (910101, 2),
    (910102, 2),
    (910103, 2),
    (910104, 2),
    (910105, 2);

DELETE FROM gs.project_sustainable_development_goal
WHERE project_id IN (910101, 910102, 910103, 910104, 910105);

INSERT INTO gs.project_sustainable_development_goal
    (project_id, sustainable_development_goal_id)
VALUES
    (910101, 1),
    (910102, 1),
    (910103, 2),
    (910104, 1),
    (910105, 2);

DELETE FROM gs.project_methodology
WHERE project_id IN (910101, 910102, 910103, 910104, 910105);

INSERT INTO gs.project_methodology
    (project_id, methodology_id)
VALUES
    (910101, 0),
    (910102, 0),
    (910103, 1),
    (910104, 0),
    (910105, 1);

DELETE FROM gs.project_crediting_period_methodology
WHERE project_id IN (910104, 910105);

DELETE FROM gs.project_crediting_period
WHERE project_id IN (910104, 910105);

INSERT INTO gs.project_crediting_period
    (project_id, period_id, start_date, end_date)
VALUES
    (910104, 1, '2024-05-20T00:00:00Z', '2025-05-20T00:00:00Z'),
    (910105, 1, '2024-06-20T00:00:00Z', '2025-06-20T00:00:00Z');

INSERT INTO gs.project_crediting_period_methodology
    (project_id, period_id, methodology_id, "version")
VALUES
    (910104, 1, 0, 1.0),
    (910105, 1, 1, 2.0);

SELECT setval('gs.account_id_seq', GREATEST((SELECT last_value FROM gs.account_id_seq), 910002));
SELECT setval('gs.user_id_seq', GREATEST((SELECT last_value FROM gs.user_id_seq), 910002));
SELECT setval('gs.project_id_seq', GREATEST((SELECT last_value FROM gs.project_id_seq), 910105));
SELECT setval('gs.project_gsid_seq', GREATEST((SELECT last_value FROM gs.project_gsid_seq), 910105));
