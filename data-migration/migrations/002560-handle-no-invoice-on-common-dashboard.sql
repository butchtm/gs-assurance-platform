DROP VIEW IF EXISTS gs.v_common_dashboard;
CREATE VIEW
    gs.v_common_dashboard (
        request_id,
        request_name,
        request_type_id,
        request_status_id,
        request_start_date,
        request_end_date,
        current_round_index,
        first_round_start_date,
        current_round_end_date,
        invoice_status_id,
        project_id,
        gsid,
        project_name,
        project_status_id,
        project_classification_id,
        master_gsid,
        parent_id,
        parent_gsid,
        parent_classification_id
    ) AS
SELECT
    request.id,
    request.request_name,
    request.request_type_id,
    request.current_request_status_id,
    COALESCE(request.start_date, '1970-01-01'),
    COALESCE(request.end_date, '1970-01-01'),
    request.current_round_index,
    COALESCE(first_round.start_date, '1970-01-01'),
    COALESCE(current_round.end_date, '1970-01-01'),
    COALESCE(invoice_status.id, -1),
    project.id,
    project.gsid,
    project.name,
    project.project_status_id,
    project.project_classification_id,
    master_project.gsid,
    parent_project.id,
    parent_project.gsid,
    parent_project.project_classification_id
FROM
    gs.request request
    LEFT JOIN gs.base_project_request base_project_request ON request.id = base_project_request.request_id
    LEFT JOIN gs.project project ON base_project_request.project_id = project.id
    LEFT JOIN gs.project master_project ON project.master_project_id = master_project.id
    LEFT JOIN gs.project parent_project ON project.parent_project_id = parent_project.id
    LEFT JOIN gs.invoice invoice ON request.invoice_id = invoice.id
    LEFT JOIN gs.invoice_status invoice_status ON invoice.status_id = invoice_status.id
    LEFT JOIN gs.request_round current_round on request.id = current_round.request_id
    and request.current_round_index = current_round.round_index
    LEFT JOIN gs.request_round first_round on request.id = first_round.request_id
    and first_round.round_index = 1;

GRANT SELECT ON gs.v_common_dashboard TO gs_assurance_db_writer;