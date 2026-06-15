CREATE VIEW gs.v_searchable_project
AS SELECT
    p.id,
    p.gsid,
    p.gsid_num,
    p."name",
    p.account_id,
    p.master_project_id,
    p.parent_project_id,
    p.project_status_id,
    p.bundle_id
FROM gs.project p;

GRANT SELECT ON gs.v_searchable_project TO gs_assurance_db_writer;