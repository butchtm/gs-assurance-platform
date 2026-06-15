ALTER TABLE gs.programme_of_activities
RENAME TO project_classification;

ALTER TABLE gs.project
RENAME COLUMN programme_of_activities_id TO project_classification_id;

-- only title (name?), project_classification_id, project_developer, gsid and id should be mandatory i.e. "NOT NULL"
ALTER TABLE gs.project
ALTER COLUMN status DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN description DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN project_start_date DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN registration_type_id DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN has_project_assets DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN current_standard_version_id DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN carbon_stream_id DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN project_size_id DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN fee_schedule_id DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN estimated_annual_units DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN project_developer_name DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN project_developer_email DROP NOT NULL;

ALTER TABLE gs.project
ALTER COLUMN account_id SET NOT NULL;

--
-- Incorporate the enum changes into our ref data view
--
CREATE OR REPLACE VIEW gs.v_reference_enumeration
(
	enumeration,
	id,
	code,
	description,
	is_active
)
AS
SELECT
	'TransitionApprovalProcedure' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.transition_approval_procedure
UNION
SELECT
	'TransitionFromOtherScheme' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.transition_from_other_scheme
UNION
SELECT
	'TransitionPathway' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.transition_pathway
UNION
SELECT
	'DocumentDisplayCategory' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.document_display_category
UNION
SELECT
	'AssignmentType' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.assignment_type
UNION
SELECT
	'AssignmentStatus' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.assignment_status
UNION
SELECT
	'AccountType' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.account_type
UNION
SELECT
	'CarbonStream' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.carbon_stream
UNION
SELECT
	'CurrentStandardVersion' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.current_standard_version
UNION
SELECT
	'FeeSchedule' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.fee_schedule
UNION
SELECT
	'Methodology' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.methodology
UNION
SELECT
	'ProjectClassification' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_classification
UNION
SELECT
	'ProjectAsset' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_asset
UNION
SELECT
	'ProjectLocation' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_location
UNION
SELECT
	'ProjectSize' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_size
UNION
SELECT
	'ProjectType' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_type
UNION
SELECT
	'RegistrationType' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.registration_type
UNION
SELECT
	'SustainableDevelopmentGoal' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.sustainable_development_goal
UNION
SELECT
	'RequestDocumentStatus' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.request_document_status
UNION
SELECT
	'RequestType' AS enumeration,
	id,
	code,
	description,
	is_active
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
	gs.invoice_status
UNION
SELECT
	'ProjectState' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_status
UNION
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
	'DocumentType' AS enumeration,
	id,
	code,
	description,
	is_active
FROM
	gs.document_type
;
