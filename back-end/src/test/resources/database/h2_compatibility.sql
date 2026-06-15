ALTER TABLE gs.project
ALTER COLUMN gsid varchar GENERATED ALWAYS AS ('GS' || (1000 + id));

DROP TABLE IF EXISTS gs.v_public_documents;

DROP TABLE IF EXISTS gs.v_categorised_request_document;

CREATE VIEW gs.v_categorised_request_document
AS
SELECT
    rd.request_id,
    rd.document_id,
    r.request_type_id,
    d.document_type_id,
    COALESCE(
        rtdt.document_display_category_id,
        CASE
	    	WHEN d.document_type_id = 999999 THEN 2 -- other document type -> other document category
    		ELSE 3 -- otherwise supporting document category
    	END
    ) as document_display_category_id,
    dpss.code AS pii_scan_status_code,
    pa.code AS pii_action_code
FROM
    gs.request_document rd
INNER JOIN
    gs.request r
ON
    r.id = rd.request_id
INNER JOIN
    gs.document d
ON
    d.id = rd.document_id
LEFT JOIN
    gs.request_type_document_type rtdt
ON
    r.request_type_id  = rtdt.request_type_id and
    d.document_type_id = rtdt.document_type_id
LEFT JOIN
    gs.document_pii_analysis dpa
ON
    dpa.document_id = d.id
LEFT JOIN
    gs.document_pii_scan_status dpss
ON
    dpa.status_id = dpss.id
LEFT JOIN
    gs.pii_metadata pm
ON
    d.id = pm.document_id
LEFT JOIN
    gs.pii_action pa
ON
    pm.action_id = pa.id;

CREATE VIEW gs.v_public_documents
(
    gsid,
    project_name,
    project_status_id,
    project_status_code,
    project_status_description,
    request_id,
    request_name,
    request_index,
    request_type_id,
    request_type_code,
    request_type_description,
    document_display_category_id,
    document_display_category_code,
    document_display_category_description,
    document_type_id,
    document_type_code,
    document_type_description,
    document_id,
    document_file_name,
    document_file_size,
    document_content_type,
    document_access,
    document_uploaded,
    document_modified,
    document_is_missing
)
AS SELECT
  p.gsid,
  p.name,
  p.project_status_id,
  ps.code,
  ps.description,
  r.id,
  r.request_name,
  r.index,
  r.request_type_id,
  rt.code,
  rt.description,
  vcrd.document_display_category_id,
  ddc.code,
  ddc.description,
  vcrd.document_type_id,
  dt.code,
  dt.description,
  d.id,
  d.file_name,
  d.file_size,
  d.content_type,
  d.access,
  d.uploaded_timestamp,
  d.modified_timestamp,
  d.is_missing
FROM
  gs.project p
INNER JOIN
  gs.project_status ps
ON
  p.project_status_id = ps.id
INNER JOIN
  gs.base_project_request bpr
ON
  p.id = bpr.project_id
INNER JOIN
  gs.v_categorised_request_document vcrd
ON
  bpr.request_id = vcrd.request_id
INNER JOIN
  gs.document_display_category ddc
ON
  vcrd.document_display_category_id = ddc.id
INNER JOIN
  gs.document_type dt
ON
  vcrd.document_type_id = dt.id
INNER JOIN
  gs.request r
ON
  bpr.request_id = r.id
INNER JOIN
  gs.request_type rt
ON
  r.request_type_id = rt.id
INNER JOIN
  gs."document" d
ON
  vcrd.document_id = d.id
WHERE
  d.access = true AND
  ps.id != 1;

DROP TABLE IF EXISTS gs.v_public_stakeholder_projects;
CREATE VIEW gs.v_public_stakeholder_projects
AS SELECT DISTINCT on (p.gsid) p.gsid,
       p.id AS project_id,
       p.name AS project_name,
       p.description AS description,
       ps.description AS project_status,
       a.name AS account_name,
       csv.description AS current_standard_version,
       proj.gsid AS master_project_gsid,
       p.master_project_id AS master_project,
       p.parent_project_id AS parent_project,
       pc.description AS project_classification,
       p.project_start_date,
       rtp.description AS registration_type,
       p.has_project_assets,
       cs.description AS carbon_stream,
       psz.description AS project_size,
       p.bundle_id,
       p.design_certification_date,
       p.estimated_annual_units,
       p.listing_date,
       t.project_link_in_other_scheme AS transition_project_link_in_other_scheme,
       t.crediting_period_start_date_other_schemes AS transition_crediting_period_start_date_other_schemes,
       t.crediting_period_end_date_other_schemes AS transition_crediting_period_end_date_other_schemes,
       tfos.description AS transition_from_other_schemes,
       tp.description AS transition_pathway,
       p.certified_project_date,
       p.last_design_certification_renewal_date,
       p.last_performance_review_date
   FROM gs.project p
       LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
       LEFT JOIN gs.current_standard_version csv ON p.current_standard_version_id = csv.id
       LEFT JOIN gs.project_classification pc ON p.project_classification_id = pc.id
       LEFT JOIN gs.registration_type rtp ON p.registration_type_id = rtp.id
       LEFT JOIN gs.carbon_stream cs ON p.carbon_stream_id = cs.id
       LEFT JOIN gs.project_size psz ON p.project_size_id = psz.id
       LEFT JOIN gs.account a ON p.account_id = a.id
       LEFT JOIN gs.transition_project t ON p.id = t.project_id
       LEFT JOIN gs.transition_from_other_scheme tfos ON t.transition_from_other_scheme_id = tfos.id
       LEFT JOIN gs.transition_pathway tp ON t.transition_pathway_id = tp.id
       LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
       LEFT JOIN gs.project proj ON p.master_project_id = proj.id
   WHERE sc.consultation_type_id = 1;

DROP TABLE IF EXISTS gs.v_public_stakeholder_requests;
CREATE VIEW gs.v_public_stakeholder_requests
AS SELECT
        r.id AS request_id,
        p.gsid,
        r.request_name AS request_name,
        r.emission_reduction_amount,
        r.crediting_period AS request_crediting_period,
        rs.description AS request_status,
        r.batch_id AS batch_id,
        sc.start_date AS consultation_start_date,
        sc.end_date AS consultation_end_date
    FROM gs.request r
        LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
        LEFT JOIN gs.project p ON p.id = bpr.project_id
        LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
        LEFT JOIN gs.request_status rs on rs.id = r.current_request_status_id
    WHERE sc.consultation_type_id = 1 AND sc.request_id = bpr.request_id;

CREATE TABLE IF NOT EXISTS gs.base_project_document (
	project_id int4 NOT NULL,
	document_id uuid NOT NULL,
	PRIMARY KEY(project_id, document_id)
);

DROP TABLE IF EXISTS gs.v_public_stakeholder_documents;
CREATE VIEW IF NOT EXISTS gs.v_public_stakeholder_documents
AS SELECT
        d.id AS document_id,
        r.id AS request_id,
        d.file_name AS filename,
        d.access AS access,
        d.category AS category,
        dt.description AS type,
        d.uploaded_timestamp AS uploaded_timestamp,
        d.modified_timestamp AS modified_timestamp,
        d.is_latest AS is_latest,
        d.is_missing AS is_missing,
        COALESCE(ddct.description, 'Supporting') as display_category_type
    FROM gs.document d
        LEFT JOIN gs.base_project_document bpd ON bpd.document_id = d.id
        LEFT JOIN gs.project p ON p.id = bpd.project_id
        LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
        LEFT JOIN gs.request_document rd ON rd.document_id = d.id
        LEFT JOIN gs.request r ON rd.request_id = r.id
        LEFT JOIN gs.document_type dt ON d.document_type_id = dt.id
        LEFT JOIN gs.request_type_document_type rtdt ON rtdt.document_type_id = dt.id
        LEFT JOIN gs.document_display_category ddct ON ddct.id = rtdt.document_display_category_id
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
    WHERE sc.consultation_type_id = 1 AND
        d.access = true AND
        (COALESCE(rtdt.request_type_id = r.request_type_id AND dt.id = d.document_type_id, true));

DROP TABLE IF EXISTS gs.v_public_stakeholder_comments;
CREATE VIEW IF NOT EXISTS  gs.v_public_stakeholder_comments
AS SELECT c.id,
    sc.request_id,
    c.submitted_comment,
    c.uploaded_by_name,
    c.uploaded_date,
    c.anonymous,
    c.addressed
   FROM gs.comment c
     LEFT JOIN gs.stakeholder_consultation sc ON c.request_id = sc.request_id
  WHERE sc.consultation_type_id = 1 AND c.public_access = true;

DROP TABLE IF EXISTS gs.v_all_requests;
CREATE VIEW gs.v_all_requests
(
    project_id,
    project_gsid,
    project_name,
    project_carbon_stream_id,
    project_size_id,
    project_account_id,
    project_account_name,
    project_risk,
    project_risk_summary,
    request_id,
    request_name,
    request_type_id,
    request_type_description,
    batch_id,
    batch_size,
    request_status_id,
    request_status_description,
    request_current_round_index,
    request_round_start_date,
    request_round_end_date,
    request_emission_reduction_amount,
    last_request_status_change_date,
    invoice_id,
    invoice_status_id,
    invoice_status,
    vvb_account_id,
    vvb_account_name,
    reviewer_account_id,
    reviewer_account_name,
    latest_activity_id,
    latest_activity_name,
    latest_activity_planned_end_date,
    latest_activity_responsible_account_id,
    latest_activity_responsible_account_name,
    submitted_by_id,
    submitted_by_given_name,
    submitted_by_family_name,
    submitted_by_email_address,
    qaqc_account_id,
    qaqc_account_name
)
AS SELECT
    project.id as project_id,
    project.gsid as project_gsid,
    project.name as project_name,
    project.carbon_stream_id,
    project.project_size_id,
    project.account_id,
    project_account.name AS project_account_name,
    (SELECT prs.risk
    FROM gs.project_risk_summary AS prs
    WHERE prs.project_id = project.id
    AND prs.date_applied =
    (SELECT MAX(date_applied)
    FROM gs.project_risk_summary
    WHERE project_id = project.id)) AS project_risk,
    (SELECT prs.summary
    FROM gs.project_risk_summary AS prs
    WHERE prs.project_id = project.id
    AND prs.date_applied =
    (SELECT MAX(date_applied)
    FROM gs.project_risk_summary
    WHERE project_id = project.id)) AS project_risk_summary,
    request.id as request_id,
    request.request_name,
    request.request_type_id,
    request_type.description,
    batch_requests.batch_id,
    batch_requests.request_count_per_batch,
    request.current_request_status_id,
    request_status.description,
    request.current_round_index,
    request_round.start_date,
    request_round.end_date,
    request.emission_reduction_amount,
    request.last_request_status_change_date,
    request.invoice_id,
    invoice_status.id AS invoice_status_id,
    invoice_status.description AS invoice_status_description,
    vvb_acc.id AS vvb_account_id,
    vvb_acc.vvb_account_name,
    reviewer_acc.id AS reviewer_account_id,
    reviewer_acc.reviewer_account_name,
    activities.activity_id,
    activities.display_name,
    activities.planned_end_date,
    activities.responsible_account_id,
    activities.responsible_account_name,
    submitted_by_user.id,
    submitted_by_user.given_name,
    submitted_by_user.family_name,
    submitted_by_user.email_address,
    qaqc_acc.id as qaqc_account_id,
    qaqc_acc.qaqc_account_name
FROM gs.request request
LEFT JOIN gs.request_type request_type
    ON request.request_type_id = request_type.id
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
        a.name AS qaqc_account_name
    FROM
        gs.request_account_assignment ra
        JOIN gs.account a ON ra.account_id = a.id
    WHERE
        ra.assignment_type_id = 2
) qaqc_acc ON request.id = qaqc_acc.request_id
LEFT JOIN (
    SELECT
        ra.request_id,
        a.id,
        a.name AS reviewer_account_name
    FROM gs.request_account_assignment ra
    JOIN gs.account a ON ra.account_id = a.id
    WHERE ra.assignment_type_id = 1
) reviewer_acc ON request.id = reviewer_acc.request_id
LEFT JOIN (
    SELECT DISTINCT ON (request_id) request_id,
        display_name,
        request_activity.id,
        start_date,
        planned_end_date,
        activity_id,
        responsible_account_id,
        account.name as responsible_account_name
    FROM gs.request_activity
    LEFT JOIN gs.account
        ON account.id = request_activity.responsible_account_id
    ORDER BY request_id, start_date DESC
    ) activities ON activities.request_id = request.id
LEFT JOIN gs."user" submitted_by_user
    ON request.submitted_by_user_id = submitted_by_user.id
WHERE project.id IS NOT NULL;

DROP TABLE gs.v_assigned_requests;
CREATE VIEW gs.v_assigned_requests
(
    project_id,
    project_gsid,
    project_name,
    project_carbon_stream_id,
    project_size_id,
    project_account_id,
    project_account_name,
    project_risk,
    project_risk_summary,
    request_id,
    request_name,
    request_type_id,
    request_type_description,
    batch_id,
    batch_size,
    request_status_id,
    request_status_description,
    current_round_index,
    current_round_start_date,
    current_round_end_date,
    request_emission_reduction_amount,
    last_request_status_change_date,
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
    (SELECT prs.risk
    FROM gs.project_risk_summary AS prs
    WHERE prs.project_id = project.id
    AND prs.date_applied =
    (SELECT MAX(date_applied)
    FROM gs.project_risk_summary
    WHERE project_id = project.id)) AS project_risk,
    (SELECT prs.summary
    FROM gs.project_risk_summary AS prs
    WHERE prs.project_id = project.id
    AND prs.date_applied =
    (SELECT MAX(date_applied)
    FROM gs.project_risk_summary
    WHERE project_id = project.id)) AS project_risk_summary,
    request.id,
    request.request_name,
    request.request_type_id,
    request_type.description,
    batch_requests.batch_id,
    batch_requests.request_count_per_batch,
    request.current_request_status_id,
    request_status.description,
    request.current_round_index,
    request_round.start_date,
    request_round.end_date,
    request.emission_reduction_amount,
    request.last_request_status_change_date,
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

DROP TABLE IF EXISTS gs.v_reference_enumeration;
CREATE VIEW gs.v_reference_enumeration
(
	enumeration,
	id,
	code,
	description,
	is_active
)
AS
SELECT
	'RequestStatus' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.request_status
UNION
SELECT
	'RequestType' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.request_type
UNION
SELECT
	'InvoiceStatus' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.invoice_status;

DROP TABLE IF EXISTS gs.v_public_stakeholder_batch_projects;
CREATE VIEW gs.v_public_stakeholder_batch_projects
AS SELECT DISTINCT p.gsid,
          p.id AS project_id,
          p.name AS project_name,
          p.description AS description,
          ps.description AS project_status,
          a.name AS account_name,
          csv.description AS current_standard_version,
          proj.gsid AS master_project_gsid,
          p.master_project_id AS master_project,
          p.parent_project_id AS parent_project,
          pc.description AS project_classification,
          p.project_start_date,
          rtp.description AS registration_type,
          p.has_project_assets,
          cs.description AS carbon_stream,
          psz.description AS project_size,
          p.bundle_id,
          p.design_certification_date,
          p.estimated_annual_units,
          p.listing_date,
          t.project_link_in_other_scheme AS transition_project_link_in_other_scheme,
          t.crediting_period_start_date_other_schemes AS transition_crediting_period_start_date_other_schemes,
          t.crediting_period_end_date_other_schemes AS transition_crediting_period_end_date_other_schemes,
          tfos.description AS transition_from_other_schemes,
          tp.description AS transition_pathway,
          p.certified_project_date,
          p.last_design_certification_renewal_date,
          p.last_performance_review_date,
          br.batch_id
      FROM gs.project p
          LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
          LEFT JOIN gs.current_standard_version csv ON p.current_standard_version_id = csv.id
          LEFT JOIN gs.project_classification pc ON p.project_classification_id = pc.id
          LEFT JOIN gs.registration_type rtp ON p.registration_type_id = rtp.id
          LEFT JOIN gs.carbon_stream cs ON p.carbon_stream_id = cs.id
          LEFT JOIN gs.project_size psz ON p.project_size_id = psz.id
          LEFT JOIN gs.account a ON p.account_id = a.id
          LEFT JOIN gs.transition_project t ON p.id = t.project_id
          LEFT JOIN gs.transition_from_other_scheme tfos ON t.transition_from_other_scheme_id = tfos.id
          LEFT JOIN gs.transition_pathway tp ON t.transition_pathway_id = tp.id
          LEFT JOIN gs.project proj ON p.master_project_id = proj.id
          RIGHT JOIN gs.batch_request br ON p.id = br.project_id;

DROP TABLE IF EXISTS gs.v_public_stakeholder_batch_children_requests;
CREATE VIEW gs.v_public_stakeholder_batch_children_requests
AS SELECT r.id AS request_id,
          p.gsid,
          r.request_name AS request_name,
          rs.description AS request_status,
          r.crediting_period AS request_crediting_period,
          r.emission_reduction_amount,
          r.batch_id AS batch_id,
          sc.start_date AS consultation_start_date,
          sc.end_date AS consultation_end_date,
          b.parent_request_id
       FROM gs.request r
           LEFT JOIN gs.base_project_request bpr ON bpr.request_id = r.id
           LEFT JOIN gs.project p ON p.id = bpr.project_id
           LEFT JOIN gs.project_status ps ON p.project_status_id = ps.id
           LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
           LEFT JOIN gs.request_status rs on rs.id = r.current_request_status_id
           RIGHT JOIN gs.batch_request br ON br.request_id = r.id
           RIGHT JOIN gs.batch b ON b.id = br.batch_id;

DROP TABLE IF EXISTS gs.v_public_stakeholder_projects_basic;
CREATE VIEW gs.v_public_stakeholder_projects_basic
AS SELECT DISTINCT
       p.gsid,
       p.id AS project_id,
       p.name AS project_name
      FROM gs.project p
        LEFT JOIN gs.stakeholder_consultation sc ON p.id = sc.project_id
     WHERE sc.consultation_type_id = 1;

DROP TABLE IF EXISTS gs.v_batch_parent_id;
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

DROP TABLE IF EXISTS gs.v_searchable_project;
CREATE VIEW gs.v_searchable_project
AS SELECT
    p.id,
    p.gsid,
    p.gsid_num,
    p."name",
    p.project_classification_id,
    p.account_id,
    p.master_project_id,
    p.parent_project_id,
    p.project_status_id,
    p.bundle_id
FROM gs.project p;

DROP TABLE IF EXISTS gs.v_project_tree_node cascade;
CREATE VIEW gs.v_project_tree_node
AS SELECT
    p.id,
    p.gsid,
    p.gsid_num,
    p."name",
    p.account_id,
    p.project_classification_id,
    p.master_project_id,
    p.parent_project_id,
    p.project_status_id,
    p.bundle_id
FROM gs.project p;

DROP TABLE IF EXISTS gs.v_common_dashboard;
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
    request_monitoring_period.period_id,
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
    LEFT JOIN gs.request_monitoring_period request_monitoring_period ON request.id = request_monitoring_period.request_id
    LEFT JOIN gs.project_crediting_monitoring_period project_crediting_monitoring_period ON request.crediting_period = project_crediting_monitoring_period.period_id AND request.monitoring_period_id = project_crediting_monitoring_period.id;

DROP TABLE IF EXISTS gs.v_status_report_projects cascade;
CREATE VIEW gs.v_status_report_projects AS
SELECT p.id, pt.code AS "type", pt.description AS "type_description", p.project_start_date AS "start_date"
FROM gs.project p
JOIN gs.project_project_type ppt ON ppt.project_id = p.id
JOIN gs.project_type pt ON ppt.project_type_id = pt.id;

DROP TABLE IF EXISTS gs.v_status_report_requests cascade;
CREATE VIEW gs.v_status_report_requests AS
SELECT r.id, rt.code AS "type", rt.description AS "type_description", rs.code AS "status_code", r.start_date, r.end_date
FROM gs.request r
JOIN gs.request_status rs ON rs.id = r.current_request_status_id
JOIN gs.request_type rt ON rt.id = r.request_type_id;

ALTER TABLE gs.methodology ALTER COLUMN id RESTART WITH 3;

DROP TABLE IF EXISTS gs.v_public_requests;
CREATE VIEW gs.v_public_requests
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
     JOIN gs.project p ON bpr.request_id = r.id
     JOIN gs.project_status ps ON p.project_status_id = ps.id
     LEFT JOIN gs.stakeholder_consultation sc ON (r.id = sc.request_id AND sc.consultation_type_id=1)
  WHERE ps.id <> 1;

DROP TABLE IF EXISTS gs.v_categorised_request_form;
CREATE VIEW gs.v_categorised_request_form
  AS SELECT
  	rf.id AS form_id,
    rf.request_id AS request_id,
    COALESCE(rtdt.document_display_category_id,
        CASE WHEN rtdt.document_type_id = 999999 THEN 2 -- other document type -> other form category
        ELSE 3 -- otherwise supporting form category
        END
    ) as display_category_id
  FROM gs.request_form rf
  LEFT JOIN gs.form_definition fd
      ON rf.definition_id = fd.id
  LEFT JOIN gs.request r
      ON rf.request_id = r.id
  LEFT JOIN gs.request_type_document_type rtdt
      ON r.request_type_id = rtdt.request_type_id
      AND fd.type_id = rtdt.form_type_id;

ALTER TABLE gs.methodology ALTER COLUMN paa SET DEFAULT true;