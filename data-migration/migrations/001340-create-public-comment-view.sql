--
-- view to present all request comments in stakeholder consultation
--
DROP VIEW IF EXISTS gs.v_public_stakeholder_comments;
CREATE OR REPLACE VIEW gs.v_public_stakeholder_comments
AS SELECT c.id,
    sc.project_id,
    c.submitted_comment,
    c.uploaded_by_name,
    c.uploaded_date,
    c.anonymous,
    c.addressed
   FROM gs.comment c
     LEFT JOIN gs.stakeholder_consultation sc ON c.request_id = sc.request_id
  WHERE sc.consultation_type_id = 1 AND c.public_access = true;

GRANT SELECT ON gs.v_public_stakeholder_comments TO gs_assurance_db_writer;
