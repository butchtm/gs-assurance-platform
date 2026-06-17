CREATE VIEW gs.v_arm_assigned_requests
(
    project_id,
    project_gsid,
    project_name,
    project_account_id,
    project_account_name,
    request_id,
    request_name,
    batch_id,
    batch_size,
    request_status_id,
    request_status_description,
    request_current_round_index,
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
    reviewer_account_name
)
AS SELECT
    project.id,
    project.gsid,
    project.name,
    project.account_id,
    project_account.name,
    request.id,
    request.request_name,
    batch_requests.batch_id,
    batch_requests.request_count_per_batch,
    request.current_request_status_id,
    request_status.description,
    request.current_round_index,
    arm_assignment.id,
    arm_assignment.account_id,
    arm_assignment.assignment_type_id,
    arm_assignment.assignment_status_id,
    arm_assignment_status.description,
    arm_assignment.created_time,
    arm_assignment.due_date,
    vvb_account.id,
    vvb_account.name,
    reviewer_account.id,
    reviewer_account.name
FROM
    gs.request_account_assignment arm_assignment
LEFT JOIN gs.request
    ON arm_assignment.request_id = request.id
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
    ON arm_assignment.request_id = batch_requests.request_id
LEFT JOIN gs.base_project_request
    ON arm_assignment.request_id = base_project_request.request_id
LEFT JOIN gs.project
    ON base_project_request.project_id = project.id
LEFT JOIN gs.account project_account
    ON project.account_id = project_account.id
LEFT JOIN gs.assignment_status arm_assignment_status
    ON arm_assignment.assignment_status_id = arm_assignment_status.id
LEFT JOIN gs.request_account_assignment vvb_assignment
    ON arm_assignment.request_id = vvb_assignment.request_id
    AND vvb_assignment.assignment_type_id = 0
LEFT JOIN gs.request_account_assignment reviewer_assignment
    ON arm_assignment.request_id = reviewer_assignment.request_id
    AND reviewer_assignment.assignment_type_id = 1
LEFT JOIN gs.account vvb_account
    ON vvb_assignment.account_id = vvb_account.id
LEFT JOIN gs.account reviewer_account
    ON reviewer_assignment.account_id = reviewer_account.id;

GRANT SELECT ON gs.v_arm_assigned_requests TO gs_assurance_db_writer;