CREATE VIEW gs.v_all_requests
(
    project_id,
    project_gsid,
    project_name,
    project_carbon_stream_id,
    project_size_id,
    project_account_id,
    project_account_name,
    request_id,
    request_name,
    request_type_id,
    batch_id,
    batch_size,
    request_status_id,
    request_status_description,
    request_current_round_index,
    request_round_end_date,
    request_emission_reduction_amount,
    request_invoice_id,
    request_invoice_status_id,
    request_invoice_status,
    vvb_account_id,
    vvb_account_name,
    reviewer_account_id,
    reviewer_account_name
)
AS SELECT
    project.id as project_id,
    project.gsid as project_gsid,
    project.name as project_name,
    project.carbon_stream_id,
    project.project_size_id,
    project.account_id,
    project_account.name AS project_account_name,
    request.id as request_id,
    request.request_name,
    request.request_type_id,
    batch_requests.batch_id,
    batch_requests.request_count_per_batch,
    request.current_request_status_id,
    request_status.description,
    request.current_round_index,
    request_round.end_date,
    request.emission_reduction_amount,
    request.invoice_id,
    invoice_status.id AS invoice_status_id,
    invoice_status.description AS invoice_status_description,
    vvb_acc.id AS vvb_account_id,
    vvb_acc.vvb_account_name,
    reviewer_acc.id AS reviewer_account_id,
    reviewer_acc.reviewer_account_name
FROM gs.request request
LEFT JOIN gs.request_status request_status
    ON request.current_request_status_id = request_status.id
LEFT JOIN (
    SELECT
        batch_id,
        COUNT(request_id) OVER (PARTITION BY batch_id) AS request_count_per_batch,
        request_id
    FROM
        gs.batch_request
) batch_requests
    ON request.id = batch_requests.request_id
LEFT JOIN gs.base_project_request base_project_request
    ON request.id = base_project_request.request_id
LEFT JOIN gs.project project
    ON base_project_request.project_id = project.id
LEFT JOIN gs.account project_account
    ON project.account_id = project_account.id
LEFT JOIN gs.request_round request_round
    ON request.id = request_round.request_id
    AND request.current_round_index = request_round.round_index
LEFT JOIN gs.invoice invoice
    ON request.invoice_id = invoice.id
LEFT JOIN gs.invoice_status invoice_status
    ON invoice.status_id = invoice_status.id
LEFT JOIN (
    SELECT
        ra.request_id,
        a.id,
        a.name AS vvb_account_name
    FROM gs.request_account_assignment ra
    JOIN gs.account a ON ra.account_id = a.id
    WHERE ra.assignment_type_id = 0
) vvb_acc ON request.id = vvb_acc.request_id
LEFT JOIN (
    SELECT
        ra.request_id,
        a.id,
        a.name AS reviewer_account_name
    FROM gs.request_account_assignment ra
    JOIN gs.account a ON ra.account_id = a.id
    WHERE ra.assignment_type_id = 1
) reviewer_acc ON request.id = reviewer_acc.request_id;


GRANT SELECT ON gs.v_all_requests TO gs_assurance_db_writer;