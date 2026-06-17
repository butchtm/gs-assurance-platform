DROP TABLE IF EXISTS gs.form_status;
CREATE TABLE gs.form_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.form_status
	(id, code, description)
VALUES
    (0,'DRAFT','Draft'),
    (1,'PUBLISHED','Published');


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
   FROM gs.pii_action
UNION
 SELECT 'ConversationStatus'::text AS enumeration,
    conversation_status.id,
    conversation_status.code,
    conversation_status.description,
    true AS is_active
   FROM gs.conversation_status
UNION
SELECT 'DigitalFormType'::text AS enumeration,
    digital_form_type.id,
    digital_form_type.code,
    digital_form_type.description,
    true AS is_active
   FROM gs.digital_form_type
UNION
SELECT 'FormStatus'::text AS enumeration,
   form_status.id,
   form_status.code,
   form_status.description,
   true AS is_active
  FROM gs.form_status;

CREATE TABLE gs.form_definition (
	id integer NOT NULL,
	form_type_id integer NOT NULL,
	form_schema varchar NOT NULL,
	ui_schema varchar NOT NULL,
	version decimal NULL,
	status_id integer NOT NULL,
	created_at timestamp NOT NULL,
	updated_at timestamp,
	PRIMARY KEY(id)
);

ALTER TABLE gs.form_definition
ADD CONSTRAINT fk_digital_form_type_id
FOREIGN KEY (form_type_id)
REFERENCES gs.digital_form_type(id);

ALTER TABLE gs.form_definition
ADD CONSTRAINT fk_form_status_id
FOREIGN KEY (status_id)
REFERENCES gs.form_status(id);

CREATE SEQUENCE
  gs.seq_form_definition
AS
  integer
OWNED BY
  gs.form_definition.id;

ALTER TABLE gs.form_definition
ALTER COLUMN id SET DEFAULT nextval('gs.seq_form_definition');

GRANT SELECT, INSERT, UPDATE ON gs.form_definition TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_form_definition TO gs_assurance_db_writer;