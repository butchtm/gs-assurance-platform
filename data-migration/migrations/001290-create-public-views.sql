--
-- view to present all projects types in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_types;
CREATE VIEW gs.v_public_stakeholder_project_types
(id, project_id,project_type_description)
AS SELECT row_number() over(), ppt.project_id, pt.description
FROM gs.project_type pt
LEFT JOIN gs.project_project_type ppt
ON pt.id = ppt.project_type_id
LEFT JOIN gs.stakeholder_consultation sc
ON ppt.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_types TO gs_assurance_db_writer;

--
-- view to present all projects locations in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_locations;
CREATE VIEW gs.v_public_stakeholder_project_locations
(id, project_id, project_location)
AS SELECT row_number() over(), ppl.project_id, pl.description
FROM gs.project_location pl
LEFT JOIN gs.project_project_location ppl
ON pl.id = ppl.project_location_id
LEFT JOIN gs.stakeholder_consultation sc
ON ppl.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_locations TO gs_assurance_db_writer;

--
-- view to present all projects assets in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_assets;
CREATE VIEW gs.v_public_stakeholder_project_assets
(id, project_id, project_asset)
AS SELECT row_number() over(), ppa.project_id, pa.description
FROM gs.project_asset pa
LEFT JOIN gs.project_project_asset ppa
ON pa.id = ppa.project_asset_id
LEFT JOIN gs.stakeholder_consultation sc
ON ppa.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_assets TO gs_assurance_db_writer;

--
-- view to present all projects sustainable dev goals in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_sus_dev_goals;
CREATE VIEW gs.v_public_stakeholder_project_sus_dev_goals
(id, project_id, project_goal)
AS SELECT row_number() over(), psdg.project_id, sdg.description
FROM gs.sustainable_development_goal sdg
LEFT JOIN gs.project_sustainable_development_goal psdg
ON sdg.id = psdg.sustainable_development_goal_id
LEFT JOIN gs.stakeholder_consultation sc
ON psdg.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_sus_dev_goals TO gs_assurance_db_writer;

--
-- view to present all projects methodologies in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_methodologies;
CREATE VIEW gs.v_public_stakeholder_project_methodologies
(id, project_id, project_methodology)
AS SELECT row_number() over(), pm.project_id, m.description
FROM gs.methodology m
LEFT JOIN gs.project_methodology pm
ON m.id = pm.methodology_id
LEFT JOIN gs.stakeholder_consultation sc
ON pm.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_methodologies TO gs_assurance_db_writer;

--
-- view to present all projects crediting periods in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_crediting;
CREATE VIEW gs.v_public_stakeholder_project_crediting
(id, project_id, period_id, start_date, end_date)
AS SELECT row_number() over(), pcp.project_id, pcp.period_id, pcp.start_date, pcp.end_date
FROM gs.project_crediting_period pcp
LEFT JOIN gs.stakeholder_consultation sc
ON pcp.project_id = sc.project_id
WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_project_crediting TO gs_assurance_db_writer;