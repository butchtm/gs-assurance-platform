--
-- Support deprecation of specific transition approval procedures
--
ALTER TABLE gs.transition_approval_procedure
ADD COLUMN is_active bool DEFAULT false NOT NULL
;

--
-- Updated classifications
--
UPDATE gs.transition_approval_procedure
SET    description = 'Preliminary review by SC, validation by VVB and design review by SC'
WHERE  id = 1
;

UPDATE gs.transition_approval_procedure
SET    description = 'Combined preliminary Review, validation and design review by SC'
WHERE  id = 2
;

UPDATE gs.transition_approval_procedure
SET    description = 'Preliminary Review by SC, combined validation and verification by VVB'
WHERE  id = 3
;

--
-- New classifications
--
INSERT INTO gs.transition_approval_procedure
	(id, code, description, is_active)
VALUES
	 (
		4, 
		'COMBINED_PRELIMINARY_REVIEW_AND_VALIDATION_BY_CB',
		'Combined preliminary review and validation by CB',
		true
		)
	,(
		5,
		'PRELIMINARY_REVIEW_BY_CB_AND_VALIDATION_BY_VVB',
		'Preliminary review by CB and validation by VVB',
		true
		)
	,(
		6,
		'PRELIMINARY_REVIEW_BY_CB_AND_COMBINED_VALIDATION_&_VERIFICATION_BY_VVB',
		'Preliminary review by CB and combined validation & verification by VVB',
		true
		)
;	

--
-- Expose transition_approval_procedure.is_active on the ref view
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
	is_active
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
