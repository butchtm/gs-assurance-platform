CREATE OR REPLACE VIEW gs.v_public_requests
AS SELECT p.gsid,
    r.id AS request_id,
    r.request_name,
    r.index AS request_index,
    r.request_type_id,
    rt.description AS request_type_description,
    sc.start_date AS consultation_start_date,
    sc.end_date AS consultation_end_date
   FROM gs.request r
     JOIN gs.base_project_request bpr ON r.id = bpr.request_id
     JOIN gs.request_type rt ON r.request_type_id = rt.id
     JOIN gs.project p ON bpr.project_id = p.id
     JOIN gs.project_status ps ON p.project_status_id = ps.id
     LEFT JOIN gs.stakeholder_consultation sc ON (r.id = sc.request_id AND sc.consultation_type_id=1)
  WHERE ps.id <> 1;

GRANT SELECT ON gs.v_public_requests TO gs_assurance_db_writer;