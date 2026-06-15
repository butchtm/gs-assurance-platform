--
-- Basic round of review statuses (rather than a boolean)
--
CREATE TABLE gs.request_round_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)    
);

INSERT INTO gs.request_round_status
  (id, code, description)
VALUES
   (1, 'OPEN', 'Open')
  ,(2, 'CLOSED', 'Closed')
;

GRANT SELECT ON gs.request_round_status TO gs_assurance_db_writer;

--
-- Round of review
--
CREATE TABLE gs.request_round (
    id int4 NOT NULL,
    request_id int4 NOT NULL,
    round_index int4 NOT NULL,
	start_date timestamp NOT NULL,
	end_date timestamp NOT NULL,
    status_id int4 NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT valid_round_index CHECK (round_index > 0 AND round_index < 4),
    UNIQUE (request_id, round_index)
);

ALTER TABLE gs.request_round
ADD FOREIGN KEY (request_id) REFERENCES gs.request ON DELETE RESTRICT;

ALTER TABLE gs.request_round
ADD FOREIGN KEY (status_id) REFERENCES gs.request_round_status ON DELETE RESTRICT;

GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_round TO gs_assurance_db_writer;

-- create the round of review table sequence
CREATE SEQUENCE
  gs.request_round_id_seq
AS
  integer
OWNED BY
  gs.request_round.id;

-- wire the sequence into the id column
ALTER TABLE gs.request_round
ALTER COLUMN id SET DEFAULT nextval('gs.request_round_id_seq');

-- permissions for back-end
GRANT USAGE, SELECT ON gs.request_round_id_seq TO gs_assurance_db_writer;

--
-- Point request table at latest round of review
--
ALTER TABLE gs.request
ADD COLUMN current_round_index int4 NULL;

ALTER TABLE gs.request
ADD FOREIGN KEY (id, current_round_index) REFERENCES gs.request_round (request_id, round_index) ON DELETE RESTRICT;

--
-- Recreate the reference data view incorporating the new request_round_status enum
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
UNION
SELECT
	'RequestRoundStatus' AS enumeration,
	id,
	code,
	description,
	true as is_active
FROM
	gs.request_round_status
;
