--
-- view to present all requests in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_requests;
CREATE VIEW gs.v_public_stakeholder_requests
AS SELECT
        r.id AS request_id,
        p.gsid,
        r.request_name AS request_name,
        r.crediting_period AS request_crediting_period
    FROM gs.request r
        LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
        LEFT JOIN gs.project p ON p.id = bpr.project_id
        LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
    WHERE sc.consultation_type_id = 1 AND sc.request_id = bpr.request_id;

GRANT SELECT ON gs.v_public_stakeholder_requests TO gs_assurance_db_writer;

--
-- view to present all public documents in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_documents;
CREATE VIEW gs.v_public_stakeholder_documents
AS SELECT
       d.id AS document_id,
       UPPER(d.container_name) AS gsid,
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
   WHERE sc.consultation_type_id = 1 AND
       d.access = true;

GRANT SELECT ON gs.v_public_stakeholder_documents TO gs_assurance_db_writer;
