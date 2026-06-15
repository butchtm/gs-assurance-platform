
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
        r.batch_id AS batch_id,
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

GRANT SELECT ON gs.v_public_stakeholder_requests TO gs_assurance_db_writer;