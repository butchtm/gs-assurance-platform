--
-- view to present projects that are in a batch
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_batch_projects;
CREATE VIEW gs.v_public_stakeholder_batch_projects
AS SELECT DISTINCT p.gsid,
          p.id AS project_id,
          p.name AS project_name,
          p.description AS description,
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
          br.batch_id
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
          RIGHT JOIN gs.batch_request br ON p.id = br.project_id;

GRANT SELECT ON gs.v_public_stakeholder_batch_projects TO gs_assurance_db_writer;

--
-- view to present requests that are in a batch
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_batch_children_requests;
CREATE VIEW gs.v_public_stakeholder_batch_children_requests
AS SELECT r.id AS request_id,
          p.gsid,
          r.request_name AS request_name,
          rs.description AS request_status,
          r.crediting_period AS request_crediting_period,
          r.emission_reduction_amount,
          r.batch_id AS batch_id,
          sc.start_date AS consultation_start_date,
          sc.end_date AS consultation_end_date,
          b.parent_request_id
       FROM gs.request r
           LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
           LEFT JOIN gs.project p ON p.id = bpr.project_id
           LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
           LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
           LEFT JOIN gs.request_status rs on rs.id = r.current_request_status_id
           RIGHT JOIN gs.batch_request br ON br.request_id = r.id
           RIGHT JOIN gs.batch b ON b.id = br.batch_id;

GRANT SELECT ON gs.v_public_stakeholder_batch_children_requests TO gs_assurance_db_writer;

--
-- view to present all requests in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_requests;
CREATE VIEW gs.v_public_stakeholder_requests
AS SELECT
        r.id AS request_id,
        p.gsid,
        r.request_name AS request_name,
        rs.description AS request_status,
        r.crediting_period AS request_crediting_period,
        r.emission_reduction_amount,
        r.batch_id AS batch_id,
        sc.start_date AS consultation_start_date,
        sc.end_date AS consultation_end_date
    FROM gs.request r
        LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
        LEFT JOIN gs.project p ON p.id = bpr.project_id
        LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
        LEFT JOIN gs.request_status rs on rs.id = r.current_request_status_id
    WHERE sc.consultation_type_id = 1
    	AND sc.request_id = bpr.request_id
        AND sc.start_date < NOW()
        AND sc.end_date > NOW();

GRANT SELECT ON gs.v_public_stakeholder_requests TO gs_assurance_db_writer;

--
-- view to present basic projects details in consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_projects_basic;
CREATE VIEW gs.v_public_stakeholder_projects_basic
AS SELECT DISTINCT
       p.gsid,
       p.id AS project_id,
       p.name AS project_name
      FROM gs.project p
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
     WHERE sc.consultation_type_id = 1
     AND sc.start_date < now()
     AND sc.end_date > now();

GRANT SELECT ON gs.v_public_stakeholder_projects_basic TO gs_assurance_db_writer;