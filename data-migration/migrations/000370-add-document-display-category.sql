------------------------------
-- DOCUMENT DISPLAY CATEGORIES
------------------------------

CREATE TABLE gs.document_display_category (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.document_display_category (id, code, description)
VALUES
    (1,'REQUIRED','Required'),
    (2,'OTHER','Other'),
    (3,'SUPPORTING','Supporting');

----------------------------
-- v_reference_enumeration
----------------------------

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
	'ProgrammeOfActivities' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.programme_of_activities
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
	'ProjectCategory' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.project_category
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

------------------------------
-- REMOVE DOCUMENT CATEGORY
------------------------------
DROP TABLE IF EXISTS gs.document_type_category;
DROP TABLE IF EXISTS gs.document_category;