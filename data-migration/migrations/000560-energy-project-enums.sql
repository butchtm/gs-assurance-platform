CREATE TABLE gs.transition_approval_procedure (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.transition_approval_procedure
	(id, code, description)
VALUES
     (1,'PRELIMINARY_REVIEW_BY_SC','Preliminary Review by SC')
    ,(2,'COMBINED_PRELIMINARY_REVIEW','Combined Preliminary Review')
    ,(3,'PRELIMINARY_REVIEW_BY_SC_COMBINED_VALIDATION','Preliminary Review by SC Combined Validation')
;

CREATE TABLE gs.transition_from_other_scheme (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.transition_from_other_scheme
	(id, code, description)
VALUES
     (0,'NONE','None')
    ,(1,'CDM_METHODOLOGY','CDM Methodology')
    ,(2,'VCS','VCS')
    ,(3,'CCER','CCER')
    ,(4,'REC','REC')
;

CREATE TABLE gs.transition_pathway (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.transition_pathway
	(id, code, description)
VALUES
     (0,'CDM_TO_GS_CDM','CDM to GS CDM')
    ,(1,'CDM_TO_GS_VER','CDM to GS VER')
    ,(2,'GS_CER_TO_GS_VER','GS CER to GS VER')
    ,(3,'GS_CDM_TO_GS_VER','GS CDM to GS VER')
    ,(4,'VCS_TO_GS_VER','VCS to GS VER')
    ,(5,'CCER_TO_GS_VER','CCER to GS VER')
    ,(6,'REC_TO_GS_VER','REC to GS VER')
;

-- Set up the energy project fks
-- TODO: Rename these columns to _id for naming consistency, but this will be a breaking change for the backend
ALTER TABLE gs.energy_project
ADD FOREIGN KEY (transition_approval_procedure) REFERENCES gs.transition_approval_procedure ON DELETE RESTRICT;

ALTER TABLE gs.energy_project
ADD FOREIGN KEY (transition_from_other_scheme) REFERENCES gs.transition_from_other_scheme ON DELETE RESTRICT;

ALTER TABLE gs.energy_project
ADD FOREIGN KEY (transition_pathway) REFERENCES gs.transition_pathway ON DELETE RESTRICT;

--
-- Incorporate the new enums into our ref data view
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