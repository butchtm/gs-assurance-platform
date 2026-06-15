DROP VIEW gs.v_assigned_requests;
CREATE VIEW gs.v_assigned_requests
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
    request_type_description,
    batch_id,
    batch_size,
    request_status_id,
    request_status_description,
    current_round_index,
    current_round_end_date,
    request_emission_reduction_amount,
    invoice_id,
    invoice_status_id,
    invoice_status_description,
    assignment_id,
    assigned_account_id,
    assignment_type_id,
    assignment_status_id,
    assignment_status_description,
    assignment_created_date,
    assignment_due_date,
    vvb_account_id,
    vvb_account_name,
    reviewer_account_id,
    reviewer_account_name,
    latest_activity_name,
    latest_activity_planned_end_date,
    latest_activity_responsible_account_id,
    latest_activity_responsible_account_name,
    submitted_by_id,
    submitted_by_given_name,
    submitted_by_family_name,
    submitted_by_email_address
)
AS SELECT
    project.id,
    project.gsid,
    project.name,
    project.carbon_stream_id,
    project.project_size_id,
    project.account_id,
    project_account.name,
    request.id,
    request.request_name,
    request.request_type_id,
    request_type.description,
    batch_requests.batch_id,
    batch_requests.request_count_per_batch,
    request.current_request_status_id,
    request_status.description,
    request.current_round_index,
    request_round.end_date,
    request.emission_reduction_amount,
    request.invoice_id,
    COALESCE(invoice_status.id, -1),
    invoice_status.description,
    assignment.id,
    assignment.account_id,
    assignment.assignment_type_id,
    assignment.assignment_status_id,
    assignment_status.description,
    assignment.created_time,
    assignment.due_date,
    vvb_account.id,
    vvb_account.name,
    reviewer_account.id,
    reviewer_account.name,
    activities.display_name,
    activities.planned_end_date,
    activities.responsible_account_id,
    activities.account_name,
    submitted_by_user.id,
    submitted_by_user.given_name,
    submitted_by_user.family_name,
    submitted_by_user.email_address
FROM
    gs.request_account_assignment assignment
LEFT JOIN gs.request
    ON assignment.request_id = request.id
LEFT JOIN gs.request_type
    ON request_type.id = request.request_type_id
LEFT JOIN gs.request_status
    ON request.current_request_status_id = request_status.id
LEFT JOIN (
    SELECT
        batch_id,
        COUNT(request_id) OVER (PARTITION BY batch_id) AS request_count_per_batch,
        request_id
    FROM
        gs.batch_request
) batch_requests
    ON assignment.request_id = batch_requests.request_id
LEFT JOIN gs.base_project_request
    ON assignment.request_id = base_project_request.request_id
LEFT JOIN gs.project
    ON base_project_request.project_id = project.id
LEFT JOIN gs.account project_account
    ON project.account_id = project_account.id
LEFT JOIN gs.assignment_status assignment_status
    ON assignment.assignment_status_id = assignment_status.id
LEFT JOIN gs.request_account_assignment vvb_assignment
    ON assignment.request_id = vvb_assignment.request_id
    AND vvb_assignment.assignment_type_id = 0
LEFT JOIN gs.request_account_assignment reviewer_assignment
    ON assignment.request_id = reviewer_assignment.request_id
    AND reviewer_assignment.assignment_type_id = 1
LEFT JOIN gs.account vvb_account
    ON vvb_assignment.account_id = vvb_account.id
LEFT JOIN gs.account reviewer_account
    ON reviewer_assignment.account_id = reviewer_account.id
LEFT JOIN gs.request_round
    ON request.id = request_round.request_id
    AND request.current_round_index = request_round.round_index
LEFT JOIN gs.invoice
    ON request.invoice_id = invoice.id
LEFT JOIN gs.invoice_status
    ON invoice.status_id = invoice_status.id
LEFT JOIN (
    SELECT DISTINCT ON (request_id) request_id,
        display_name,
        request_activity.id,
        start_date,
        planned_end_date,
        end_date,
        activity_id,
        responsible_account_id,
        account.name AS account_name
    FROM gs.request_activity
    LEFT JOIN gs.account
        ON account.id = request_activity.responsible_account_id
    WHERE end_date is NULL
    ORDER BY request_id, start_date DESC
) activities
    ON activities.request_id = request.id
LEFT JOIN gs."user" submitted_by_user
    ON request.submitted_by_user_id = submitted_by_user.id;

GRANT SELECT ON gs.v_assigned_requests TO gs_assurance_db_writer;
