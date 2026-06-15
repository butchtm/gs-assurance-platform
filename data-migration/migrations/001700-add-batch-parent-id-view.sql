CREATE VIEW gs.v_batch_parent_id
(
    request_id,
    project_id,
    parent_request_id,
    parent_project_id
)
AS SELECT
    bpr.request_id,
    bpr.project_id,
    b.parent_request_id,
    parent_bpr.project_id
FROM
    gs.base_project_request bpr
LEFT JOIN gs.batch_request br
    ON bpr.request_id = br.request_id
LEFT JOIN gs.batch b
	ON br.batch_id = b.id
LEFT JOIN gs.base_project_request parent_bpr
	ON b.parent_request_id = parent_bpr.request_id;

GRANT SELECT ON gs.v_batch_parent_id TO gs_assurance_db_writer;