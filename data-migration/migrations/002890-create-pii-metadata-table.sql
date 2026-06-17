--Create pii action reference data as will be needed on front and back
DROP TABLE IF EXISTS gs.pii_action;
CREATE TABLE gs.pii_action (
    id integer NOT NULL,
    code varchar NOT NULL,
    description varchar NOT NULL,
    PRIMARY KEY(id)
);

GRANT SELECT ON gs.pii_action TO gs_assurance_db_writer;

--Create pii metadata table
CREATE TABLE gs.pii_metadata (
    id integer NOT NULL,
    project_id integer NOT NULL,
    request_id integer NULL,
    type varchar NOT NULL,
    type_detail varchar NOT NULL,
    submitted_by_user_id integer NOT NULL,
    submitted_by_account_id integer NULL,
    actioned boolean NOT NULL,
    actioned_by_id integer NULL,
    action_id integer NULL,
    created timestamp NOT NULL,
    updated timestamp NULL,
    PRIMARY KEY(id)
);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_project_id
FOREIGN KEY (project_id)
REFERENCES gs.project(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_submitted_by_account_id
FOREIGN KEY (submitted_by_account_id)
REFERENCES gs.account(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_submitted_by_user_id
FOREIGN KEY (submitted_by_user_id)
REFERENCES gs."user"(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_actioned_by_id
FOREIGN KEY (actioned_by_id)
REFERENCES gs.account(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_action_id
FOREIGN KEY (action_id)
REFERENCES gs.pii_action(id);

ALTER TABLE gs.pii_metadata
ADD CONSTRAINT fk_pii_metadata_request_id
FOREIGN KEY (request_id)
REFERENCES gs.request(id);

GRANT SELECT, INSERT, UPDATE ON gs.pii_metadata TO gs_assurance_db_writer;

CREATE SEQUENCE
  gs.pii_metadata_id_seq
AS
  integer
OWNED BY
  gs.pii_metadata.id;

SELECT
  setval('gs.pii_metadata_id_seq', 1);


ALTER TABLE gs.pii_metadata
ALTER COLUMN id SET DEFAULT nextval('gs.pii_metadata_id_seq');

GRANT USAGE, SELECT ON gs.pii_metadata_id_seq TO gs_assurance_db_writer;

-- Update reference data tables
-- gs.v_reference_enumeration source

CREATE OR REPLACE VIEW gs.v_reference_enumeration
AS SELECT 'TransitionApprovalProcedure'::text AS enumeration,
    transition_approval_procedure.id,
    transition_approval_procedure.code,
    transition_approval_procedure.description,
    transition_approval_procedure.is_active
   FROM gs.transition_approval_procedure
UNION
 SELECT 'TransitionFromOtherScheme'::text AS enumeration,
    transition_from_other_scheme.id,
    transition_from_other_scheme.code,
    transition_from_other_scheme.description,
    transition_from_other_scheme.is_active
   FROM gs.transition_from_other_scheme
UNION
 SELECT 'TransitionPathway'::text AS enumeration,
    transition_pathway.id,
    transition_pathway.code,
    transition_pathway.description,
    transition_pathway.is_active
   FROM gs.transition_pathway
UNION
 SELECT 'DocumentDisplayCategory'::text AS enumeration,
    document_display_category.id,
    document_display_category.code,
    document_display_category.description,
    true AS is_active
   FROM gs.document_display_category
UNION
 SELECT 'AssignmentType'::text AS enumeration,
    assignment_type.id,
    assignment_type.code,
    assignment_type.description,
    true AS is_active
   FROM gs.assignment_type
UNION
 SELECT 'AssignmentStatus'::text AS enumeration,
    assignment_status.id,
    assignment_status.code,
    assignment_status.description,
    true AS is_active
   FROM gs.assignment_status
UNION
 SELECT 'AccountType'::text AS enumeration,
    account_type.id,
    account_type.code,
    account_type.description,
    true AS is_active
   FROM gs.account_type
UNION
 SELECT 'CarbonStream'::text AS enumeration,
    carbon_stream.id,
    carbon_stream.code,
    carbon_stream.description,
    true AS is_active
   FROM gs.carbon_stream
UNION
 SELECT 'CurrentStandardVersion'::text AS enumeration,
    current_standard_version.id,
    current_standard_version.code,
    current_standard_version.description,
    current_standard_version.is_active
   FROM gs.current_standard_version
UNION
 SELECT 'FeeSchedule'::text AS enumeration,
    fee_schedule.id,
    fee_schedule.code,
    fee_schedule.description,
    true AS is_active
   FROM gs.fee_schedule
UNION
 SELECT 'Methodology'::text AS enumeration,
    methodology.id,
    methodology.code,
    methodology.description,
    methodology.is_active
   FROM gs.methodology
UNION
 SELECT 'ProjectClassification'::text AS enumeration,
    project_classification.id,
    project_classification.code,
    project_classification.description,
    project_classification.is_active
   FROM gs.project_classification
UNION
 SELECT 'ProjectAsset'::text AS enumeration,
    project_asset.id,
    project_asset.code,
    project_asset.description,
    true AS is_active
   FROM gs.project_asset
UNION
 SELECT 'ProjectLocation'::text AS enumeration,
    project_location.id,
    project_location.code,
    project_location.description,
    true AS is_active
   FROM gs.project_location
UNION
 SELECT 'ProjectSize'::text AS enumeration,
    project_size.id,
    project_size.code,
    project_size.description,
    true AS is_active
   FROM gs.project_size
UNION
 SELECT 'ProjectType'::text AS enumeration,
    project_type.id,
    project_type.code,
    project_type.description,
    true AS is_active
   FROM gs.project_type
UNION
 SELECT 'RegistrationType'::text AS enumeration,
    registration_type.id,
    registration_type.code,
    registration_type.description,
    true AS is_active
   FROM gs.registration_type
UNION
 SELECT 'SustainableDevelopmentGoal'::text AS enumeration,
    sustainable_development_goal.id,
    sustainable_development_goal.code,
    sustainable_development_goal.description,
    true AS is_active
   FROM gs.sustainable_development_goal
UNION
 SELECT 'RequestDocumentStatus'::text AS enumeration,
    request_document_status.id,
    request_document_status.code,
    request_document_status.description,
    true AS is_active
   FROM gs.request_document_status
UNION
 SELECT 'RequestType'::text AS enumeration,
    request_type.id,
    request_type.code,
    request_type.description,
    request_type.is_active
   FROM gs.request_type
UNION
 SELECT 'InvoiceStatus'::text AS enumeration,
    invoice_status.id,
    invoice_status.code,
    invoice_status.description,
    true AS is_active
   FROM gs.invoice_status
UNION
 SELECT 'ProjectState'::text AS enumeration,
    project_status.id,
    project_status.code,
    project_status.description,
    true AS is_active
   FROM gs.project_status
UNION
 SELECT 'RequestStatus'::text AS enumeration,
    request_status.id,
    request_status.code,
    request_status.description,
    true AS is_active
   FROM gs.request_status
UNION
 SELECT 'DocumentType'::text AS enumeration,
    document_type.id,
    document_type.code,
    document_type.description,
    document_type.is_active
   FROM gs.document_type
UNION
 SELECT 'RequestRoundStatus'::text AS enumeration,
    request_round_status.id,
    request_round_status.code,
    request_round_status.description,
    true AS is_active
   FROM gs.request_round_status
UNION
 SELECT 'SuppressionReason'::text AS enumeration,
    suppression_reason.id,
    suppression_reason.code,
    suppression_reason.description,
    suppression_reason.is_active
   FROM gs.suppression_reason
UNION
 SELECT 'DeletionReason'::text AS enumeration,
    deletion_reason.id,
    deletion_reason.code,
    deletion_reason.description,
    deletion_reason.is_active
   FROM gs.deletion_reason
UNION
 SELECT 'BatchType'::text AS enumeration,
    batch_type.id,
    batch_type.code,
    batch_type.description,
    true AS is_active
   FROM gs.batch_type
UNION
 SELECT 'PiiAction'::text AS enumeration,
    pii_action.id,
    pii_action.code,
    pii_action.description,
    true AS is_active
   FROM gs.pii_action;


-- Add pii action choices
INSERT INTO gs.pii_action
	(id, code, description)
	VALUES (0,'NEW','New');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (1,'UNDER_INVESTIGATION','Under Investigation');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (2,'ACCEPTED','Accepted');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (3,'REDACTED','Redacted');

INSERT INTO gs.pii_action
	(id, code, description)
	VALUES (4,'PARTIALLY_REDACTED','Partially Redacted');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (5,'REMOVED','Removed');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (6,'PII_NOT_SUITABLE','PII not suitable for the public');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (7,'DOCUMENT_REMOVED','Document Removed');

INSERT INTO gs.pii_action
    (id, code, description)
    VALUES (8,'NO_PII_FOUND','No PII Found');