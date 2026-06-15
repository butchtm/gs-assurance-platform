--
-- Support deprecation of specific project classifications
--
ALTER TABLE gs.project_classification
ADD COLUMN is_active bool DEFAULT false NOT NULL
;

--
-- Updated classifications
--
UPDATE gs.project_classification
SET    code = 'REAL_CASE_VPA_LEGACY'
WHERE  id = 3
;

--
-- New classifications
--
INSERT INTO gs.project_classification
	(id, code, description, is_active)
VALUES
	 (6, 'STANDALONE_PROJECT', 'Standalone Project', true)
	,(7, 'PROGRAMME_OF_ACTIVITIES', 'Programme of Activities', true)
	,(8, 'REAL_CASE_VPA', 'Real Case VPA', true)
	,(9, 'REGULAR_VPA', 'Regular VPA', true)
;	

--
-- Expose project_classification.is_active on the ref view
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
	is_active
FROM
    gs.transition_from_other_scheme
UNION
SELECT
	'TransitionPathway' AS enumeration,
	id,
	code,
	description,
	is_active
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
	is_active
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
