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
        request_crediting_period_id,
        request_monitoring_period_id,
        request_monitoring_period_fee_schedule_id,
        invoice_status_id,
        project_id,
        gsid,
        project_name,
        project_status_id,
        project_classification_id,
        master_gsid,
        parent_id,
        parent_gsid,
        parent_classification_id,
        project_types_string,
        project_fee_schedule_id,
        project_crediting_monitoring_period_fee_schedule_id,
        account_name
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
    request.crediting_period,
    request.monitoring_period_id,
    request_monitoring_period.fee_schedule,
    invoice_status.id,
    project.id,
    project.gsid,
    project.name,
    project.project_status_id,
    project.project_classification_id,
    master_project.gsid,
    parent_project.id,
    parent_project.gsid,
    parent_project.project_classification_id,
    (
        SELECT
            STRING_AGG (pt.description, ', ')
        FROM
            gs.project_project_type ppt
            JOIN gs.project_type pt ON pt.id = ppt.project_type_id
        WHERE
            ppt.project_id = project.id
    ) AS project_types_string,
    project.fee_schedule_id,
    project_crediting_monitoring_period.fee_schedule,
    account.name
FROM
    gs.request request
    LEFT JOIN gs.base_project_request base_project_request ON request.id = base_project_request.request_id
    LEFT JOIN gs.project project ON base_project_request.project_id = project.id
    LEFT JOIN gs.project master_project ON project.master_project_id = master_project.id
    LEFT JOIN gs.project parent_project ON project.parent_project_id = parent_project.id
    LEFT JOIN gs.invoice invoice ON request.invoice_id = invoice.id
    LEFT JOIN gs.invoice_status invoice_status ON invoice.status_id = invoice_status.id
    LEFT JOIN gs.request_round current_round ON request.id = current_round.request_id
    AND request.current_round_index = current_round.round_index
    LEFT JOIN gs.request_round first_round ON request.id = first_round.request_id
    AND first_round.round_index = 1
    LEFT JOIN gs.account account ON project.account_id = account.id
    LEFT JOIN gs.request_monitoring_period request_monitoring_period
        ON request.id = request_monitoring_period.request_id
    LEFT JOIN gs.project_crediting_monitoring_period project_crediting_monitoring_period
        ON project_crediting_monitoring_period.project_id = project.id
        AND request.crediting_period = project_crediting_monitoring_period.period_id
        AND request.monitoring_period_id = project_crediting_monitoring_period.id;

GRANT SELECT ON gs.v_common_dashboard TO gs_assurance_db_writer;