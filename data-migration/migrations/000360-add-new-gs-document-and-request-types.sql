----------------------------
-- DOCUMENT TYPES
----------------------------

-- Deactivate all current document types ...
ALTER TABLE gs.document_type
ADD COLUMN is_active bool DEFAULT false NOT NULL;

-- ... except for 'OTHER'
UPDATE gs.document_type
SET is_active = true
WHERE id = 999999;

-- New GS Document Types
INSERT INTO gs.document_type
	(id, code, description, is_active)
VALUES 
	 (1000,'PRELIMINARY_REVIEW_REQUEST_FORM','Preliminary Review Request Form',true)
	,(1001,'TRANSITION_REQUEST_FORM','Transition Request Form',true)
	,(1002,'STAKEHOLDER_CONSULTATION','Stakeholder Consultation',true)
	,(1003,'COVER_LETTER','Cover Letter',true)
	,(1004,'ODA_DECLARATION','ODA Declaration',true)
	,(1005,'TERMS_AND_CONDITIONS','Terms and Conditions',true)
	,(1006,'DESIGN_DOCUMENT','Design Document',true)
	,(1007,'EMISSION_REDUCTIONS','Emission Reductions',true)
	,(1008,'OTHER_STANDARD_PDD','Other Standard PDD',true)
	,(1009,'SDG_TOOL','SDG tool',true)
	,(1010,'MONITORING_REPORT','Monitoring report',true)
	,(1011,'OTHER_STANDARD_MONITORING_REPORT','Other Standard Monitoring Report',true)
	,(1012,'ANNUAL_REPORT','Annual Report',true)
	,(1013,'VALIDATION_REPORT','Validation Report',true)
	,(1014,'VERIFICATION_REPORT','Verification Report',true)
	,(1015,'OTHER_STANDARD_VALIDATION_REPORT','Other Standard Validation Report',true)
	,(1016,'OTHER_STANDARD_VERIFICATION_REPORT','Other Standard Verification Report',true)
	,(1017,'REVIEW_FEEDBACK_FORM','Review Feedback Form',true)
;


----------------------------
-- REQUEST TYPES
----------------------------

-- Deactivate all current request types ...
ALTER TABLE gs.request_type
ADD COLUMN is_active bool DEFAULT false NOT NULL;

-- There are 2 'legacy' Preliminary Reviews so we'll create a fresh one going forward
-- -- ... except for 'Preliminary Review'
-- UPDATE gs.request_type
-- SET is_active = true
-- WHERE id = 1;

-- New GS Review Types
INSERT INTO gs.request_type
	(id, code, description, is_active)
VALUES
     (1000,'PRELIMINARY_REVIEW','Preliminary Review',true)
	,(1001,'DESIGN_CERTIFICATION_PROJECT','Design Certification Project',true)
	,(1002,'DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA','Design Certification PoA with Real Case VPA',true)
	,(1003,'INCLUSION_REAL_CASE_VPA','Inclusion Real Case VPA',true)
	,(1004,'INCLUSION_REGULAR_VPA','Inclusion regular VPA',true)
	,(1005,'PERFORMANCE_CERTIFICATION_|_PROJECT','Performance Certification | Project',true)
	,(1006,'PERFORMANCE_CERTIFICATION_|_VPA','Performance Certification | VPA',true)
	,(1007,'DESIGN_CERTIFICATION_RENEWAL_|_PROJECT','Design Certification Renewal | Project',true)
	,(1008,'DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA','Design Certification Renewal | PoA with Real Case VPA',true)
	,(1009,'RENEWAL_|_REAL_CASE_VPA','Renewal | Real Case VPA',true)
	,(1010,'RENEWAL_|_REGULAR_VPA','Renewal | Regular VPA',true)
	,(1011,'DESIGN_CHANGE_|_WITH_VVB','Design Change | With VVB',true)
	,(1012,'ISSUANCE','Issuance',true)
	,(1013,'GENERIC','Generic',true)
	,(1014,'ANNUAL_REPORT','Annual Report',true)
	,(1015,'PROJECT_DECERTIFICATION','Project Decertification',true)
	,(1016,'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER','Transition Review | GS CDM to GS VER',true)
	,(1017,'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_PROJECT','Combined Design and Performance Certification | Project',true)
	,(1018,'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA','Combined Design and Performance Certification | PoA',true)
	,(1019,'COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA','Combined Design and Performance Certification | VPA',true)
;


----------------------------
-- v_reference_enumeration
----------------------------

-- Add is_active flag to the view, defaulted to true for
-- reference tables which don't have the column.
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
	'DocumentCategory' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
    gs.document_category
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