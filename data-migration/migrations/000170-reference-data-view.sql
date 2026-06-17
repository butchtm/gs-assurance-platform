DROP VIEW IF EXISTS gs.v_reference_enumeration;
CREATE VIEW gs.v_reference_enumeration
(
	enumeration,
	id,
	code,
	description
)
AS
SELECT 
	'CarbonStream' AS enumeration,
	id,
	code,
	description
FROM
    gs.carbon_stream
UNION
SELECT 
	'CurrentStandardVersion' AS enumeration,
	id,
	code,
	description
FROM
	gs.current_standard_version
UNION
SELECT 
	'FeeSchedule' AS enumeration,
	id,
	code,
	description
FROM
	gs.fee_schedule
UNION
SELECT 
	'Methodology' AS enumeration,
	id,
	code,
	description
FROM
	gs.methodology
UNION
SELECT 
	'ProgrammeOfActivities' AS enumeration,
	id,
	code,
	description
FROM
	gs.programme_of_activities
UNION
SELECT 
	'ProjectAsset' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_asset
UNION
SELECT 
	'ProjectCategory' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_category
UNION
SELECT 
	'ProjectLocation' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_location
UNION
SELECT 
	'ProjectSize' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_size
UNION
SELECT 
	'ProjectType' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_type
UNION
SELECT 
	'RegistrationType' AS enumeration,
	id,
	code,
	description
FROM
	gs.registration_type
UNION
SELECT 
	'SustainableDevelopmentGoal' AS enumeration,
	id,
	code,
	description
FROM
	gs.sustainable_development_goal
UNION
SELECT 
	'RequestDocumentStatus' AS enumeration,
	id,
	code,
	description
FROM
	gs.request_document_status
UNION
SELECT 
	'RequestType' AS enumeration,
	id,
	code,
	description
FROM
	gs.request_type
UNION
SELECT 
	'InvoiceStatus' AS enumeration,
	id,
	code,
	description
FROM
	gs.invoice_status
UNION
SELECT 
	'ProjectState' AS enumeration,
	id,
	code,
	description
FROM
	gs.project_status
UNION
SELECT 
	'RequestStatus' AS enumeration,
	id,
	code,
	description
FROM
	gs.request_status
UNION
SELECT 
	'DocumentType' AS enumeration,
	id,
	code,
	description
FROM
	gs.document_type
;
