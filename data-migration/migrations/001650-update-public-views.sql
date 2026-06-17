--
-- drop unnecessary views
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_types;
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_locations;
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_assets;
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_sus_dev_goals;
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_methodologies;
DROP VIEW IF EXISTS gs.v_public_stakeholder_project_crediting;

--
-- view to present publicly available projects in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_projects;
CREATE VIEW gs.v_public_stakeholder_projects
AS SELECT DISTINCT ON (p.gsid)
       p.gsid,
       p.id AS project_id,
       p.name AS project_name,
       p.description as description,
       ps.description AS project_status,
       a.name AS account_name,
       csv.description AS current_standard_version,
       p.master_project_id AS master_project,
       p.parent_project_id AS parent_project,
       pc.description AS project_classification,
       p.project_start_date,
       rtp.description AS registration_type,
       p.has_project_assets,
       cs.description AS carbon_stream,
       psz.description AS project_size,
       p.bundle_id,
       p.design_certification_date,
       p.estimated_annual_units,
       p.listing_date,
       t.project_link_in_other_scheme AS transition_project_link_in_other_scheme,
       t.crediting_period_start_date_other_schemes AS transition_crediting_period_start_date_other_schemes,
       t.crediting_period_end_date_other_schemes AS transition_crediting_period_end_date_other_schemes,
       tfos.description AS transition_from_other_schemes,
       tp.description AS transition_pathway,
       p.certified_project_date,
       p.last_design_certification_renewal_date,
       p.last_performance_review_date,
       sc.start_date AS consultation_start_date,
       sc.end_date AS consultation_end_date
   FROM gs.project p
       LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
       LEFT JOIN gs.current_standard_version csv ON p.current_standard_version_id = csv.id
       LEFT JOIN gs.project_classification pc ON p.project_classification_id = pc.id
       LEFT JOIN gs.registration_type rtp ON p.registration_type_id = rtp.id
       LEFT JOIN gs.carbon_stream cs ON p.carbon_stream_id = cs.id
       LEFT JOIN gs.project_size psz ON p.project_size_id = psz.id
       LEFT JOIN gs.account a ON p.account_id = a.id
       LEFT JOIN gs.transition_project t ON p.id = t.project_id
       LEFT JOIN gs.transition_from_other_scheme tfos ON t.transition_from_other_scheme_id = tfos.id
       LEFT JOIN gs.transition_pathway tp ON t.transition_pathway_id = tp.id
       LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
   WHERE sc.consultation_type_id = 1;

GRANT SELECT ON gs.v_public_stakeholder_projects TO gs_assurance_db_writer;