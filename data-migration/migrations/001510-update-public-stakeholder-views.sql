--
-- view to present publicly available projects in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_projects;
CREATE VIEW gs.v_public_stakeholder_projects
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
          p.last_performance_review_date
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
      WHERE sc.consultation_type_id = 1
        AND sc.start_date < NOW()
        AND sc.end_date > NOW();

--
-- view to present all requests in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_requests;
CREATE VIEW gs.v_public_stakeholder_requests
AS SELECT
        r.id AS request_id,
        p.gsid,
        r.request_name AS request_name,
        r.crediting_period AS request_crediting_period,
        sc.start_date AS consultation_start_date,
        sc.end_date AS consultation_end_date
    FROM gs.request r
        LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
        LEFT JOIN gs.project p ON p.id = bpr.project_id
        LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
    WHERE sc.consultation_type_id = 1
    	AND sc.request_id = bpr.request_id
        AND sc.start_date < NOW()
        AND sc.end_date > NOW();

--
-- view to present all public documents in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_documents;
CREATE VIEW gs.v_public_stakeholder_documents
AS SELECT
       d.id AS document_id,
       r.id AS request_id,
       d.file_name AS filename,
       d.access AS access,
       d.category AS category,
       dt.description AS type,
       d.uploaded_timestamp AS uploaded_timestamp,
       d.modified_timestamp AS modified_timestamp,
       d.is_latest AS is_latest,
       COALESCE(ddct.description, 'Supporting') as display_category_type
   FROM gs.document d
       LEFT JOIN gs.base_project_document bpd ON bpd.document_id = d.id
       LEFT JOIN gs.project p ON p.id = bpd.project_id
       LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
       LEFT JOIN gs.request_document rd ON rd.document_id = d.id
       LEFT JOIN gs.request r ON rd.request_id = r.id
       LEFT JOIN gs.document_type dt ON d.document_type_id = dt.id
       LEFT JOIN gs.request_type_document_type rtdt ON rtdt.document_type_id = dt.id
        AND rtdt.request_type_id = r.request_type_id
       LEFT JOIN gs.document_display_category ddct ON ddct.id = rtdt.document_display_category_id
       LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
   WHERE sc.consultation_type_id = 1
   	AND d.access = true
   	AND sc.request_id = r.id;

--
-- view to present all request comments in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_comments;
CREATE OR REPLACE VIEW gs.v_public_stakeholder_comments
AS SELECT c.id,
    sc.request_id,
    c.submitted_comment,
    c.uploaded_by_name,
    c.uploaded_date,
    c.anonymous,
    c.addressed
   FROM gs.comment c
     LEFT JOIN gs.stakeholder_consultation sc ON c.request_id = sc.request_id
     LEFT JOIN gs.request r ON c.request_id = r.id
  WHERE sc.consultation_type_id = 1
    AND c.public_access = true
    AND c.request_id = r.id;
