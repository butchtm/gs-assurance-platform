INSERT INTO gs.carbon_stream
	(id, code, description)
VALUES
    (0,'REPORTING_ONLY','Reporting Only'),
    (1,'VER','VER'),
    (2,'GS_CER','GS CER');

INSERT INTO gs.current_standard_version
	(id, code, description)
VALUES
    (0,'CARBON_FIX','CarbonFix'),
    (1,'CDM','CDM'),
    (2,'GS4GG','Gold Standard for the Global Goals'),
    (3,'JOINT_IMPLEMENTATION','Joint Implementation (JI)'),
    (4,'V0_9_CASH','V0.9 CASH'),
    (5,'V1_0_CASH','v1 CASH');

INSERT INTO gs.fee_schedule
	(id, code, description)
VALUES
    (0,'CASH_PER_CREDIT','Cash per credit'),
    (1,'SHARE_OF_PROCEEDS','Share of proceeds');

INSERT INTO gs.methodology
	(id, code, description)
VALUES
    (0,'ACM0001_FLARING_OR_USE_OF_LANDFILL_GAS','ACM0001 Flaring or use of landfill gas'),
    (1,'ACM0002_GRID_CONNECTED_ELECTRICITY_GENERATION_FROM_RENEWABLE_SOURCES','ACM0002 Grid-connected electricity generation from renewable sources'),
    (2,'ACM0003_PARTIAL_SUBSTITUTION_OF_FOSSIL_FUELS_WITH_ALTERNATIVE_FUELS_OR_LESS_CARBON_INTENSIVE_FUELS_IN_CEMENT_MANUFACTURE','ACM0003 Partial substitution of fossil fuels with alternative fuels or less carbon intensive fuels in cement manufacture');

INSERT INTO gs.project_classification
	(id, code, description)
VALUES
     (0, 'NONE', 'None'),
     (1, 'POA', 'Master Legacy'),
     (2, 'VPA_CPA', 'VPA/CPA'),
     (3, 'REAL_CASE_VPA', 'Real case VPA'),
     (4, 'REGULAR_CASE_VPA', 'Regular case VPA'),
     (5, 'POA', 'POA'),
     (6, 'STANDALONE_PROJECT', 'Standalone Project'),
     (7, 'PROGRAMME_OF_ACTIVITIES', 'Programme of Activities'),
     (8, 'REAL_CASE_VPA', 'Real Case VPA'),
     (9, 'REGULAR_VPA', 'Regular VPA');

INSERT INTO gs.project_asset
	(id, code, description)
VALUES
    (0,'CARBON','Carbon'),
    (1,'RECS','RECs'),
    (2,'WATER','Water');

INSERT INTO gs.project_location
	(id, code, description, least_developed_country)
VALUES
    (1,'AF','Afghanistan', true),
    (2,'AL','Albania', false),
    (305,'INTERNATIONAL','International', false);

INSERT INTO gs.project_size
	(id, code, description)
VALUES
    (0,'MICROSCALE','Micro scale'),
    (1,'SMALL_SCALE','Small Scale'),
    (2,'LARGE_SCALE','Large Scale');

INSERT INTO gs.project_type
	(id, code, description)
VALUES
    (0,'BIOGAS_HEAT','Biogas - Heat'),
    (4,'COMPOSTING','Composting'),
    (4242,'TEST_PROJECT_TYPE','Test Project Type');

INSERT INTO gs.registration_type
	(id, code, description)
VALUES
    (0,'REGULAR','Regular'),
    (1,'RETROACTIVE','Retroactive');

INSERT INTO gs.sustainable_development_goal
	(id, code, description)
VALUES
    (1,'NO_POVERTY','1 - No Poverty'),
    (2,'ZERO_HUNGER','2 - Zero Hunger');

INSERT INTO gs.request_document_status
	(id, code, description)
VALUES
    (0,'DOCUMENTS_MISSING','Documents missing'),
    (1,'DOCUMENTS_COMPLETE','Documents complete');

INSERT INTO gs.request_type
	(id, code, description)
VALUES
    (0,'POA_DESIGN_CONSULTATION','POA Design Consultation'),
    (1000,'PRELIMINARY_REVIEW','Preliminary Review'),
    (1001,'DESIGN_CERTIFICATION_PROJECT','Design Certification Project'),
    (1008,'DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA','Design Certification Renewal | PoA with Real Case VPA');

INSERT INTO gs.invoice_status
	(id, code, description)
VALUES
    (0,'NOT_TRIGGERED','Not triggered'),
    (1,'TRIGGERED','Triggered'),
    (2,'GENERATED','Generated'),
    (3,'PAID','Paid'),
    (4,'VOID','Void'),
    (5,'NOT_REQUIRED','Not Required');

INSERT INTO gs.project_status
	(id, code, description, sequence)
VALUES
    (1,'NEW','New', 1),
	(2,'LISTED','Listed', 2),
	(3,'DESIGN_CERTIFIED','Design Certified', 3),
	(4,'CERTIFIED_PROJECT','Certified Project', 4),
	(5,'DE_CERTIFIED','De-certified', 5);

INSERT INTO gs.request_status
	(id, code, description)
VALUES
    (0,'NOT_REQUESTED','Not requested'),
    (1,'BOOKED','Booked'),
    (2,'REVIEW_PLANNED_TO_START','Planned To Start'),
    (3,'TODO: UNKNOWN STATUS','TODO: UNKNOWN STATUS'),
    (4,'COMPLETED_APPROVED','Completed - Approved'),
    (5,'COMPLETED_REJECTED','Completed - Rejected'),
    (6,'IN_PREPARATION','In Preparation'),
    (7,'OMITTED','Omitted'),
    (8,'CANCELLED','Cancelled'),
    (9,'COMPLETENESS_CHECK','Completeness Check'),
    (10,'IN_PROGRESS','In Progress'),
    (11,'IN_AUDIT','In Audit'),
    (12,'IN_ASSIGNMENT','In Assignment'),
    (13,'IN_ROUND_COMPLETION','In Round Completion');

INSERT INTO gs.document_type
	(id, code, description)
VALUES
    (0,'CDM_PROJECT_DESIGN_DOCUMENT','CDM Project Design Document'),
    (1,'GOLD_STANDARD_PROJECT_DESIGN_DOCUMENT','Gold Standard Project Design Document'),
    (424242,'TEST','Test'),
    (999999,'OTHER','Other');

INSERT INTO gs.account_type
	(id, code, description, is_active)
VALUES
    (0,'ACCOUNT_TYPE_0','Account Type 0', 1),
    (1,'ACCOUNT_TYPE_1','Account Type 1', 1),
    (2,'ACCOUNT_TYPE_2','Account Type 2', 1),
    (3,'ACCOUNT_TYPE_3','Account Type 3', 1),
    (4,'ACCOUNT_TYPE_4','Account Type 4', 1);

INSERT INTO gs.assignment_type
	(id, code, description)
VALUES
    (0,'AUDIT','Audit'),
    (1,'REVIEW','Review'),
    (2,'QA/QC','QA/QC'),
    (3,'COLLECT_FEE','Collect Fee'),
    (4,'COMPLETENESS_CHECK','Completeness Check'),
    (5,'STAKEHOLDER_CONSULTATION','Stakeholder Consultation Check');

INSERT INTO gs.assignment_status
	(id, code, description)
VALUES
    (0,'PENDING','Pending'),
    (1,'ACCEPTED','Accepted'),
    (2,'CANCELLED','Cancelled');

INSERT INTO gs.transition_approval_procedure
	(id, code, description)
VALUES
    (1,'PRELIMINARY_REVIEW_BY_SC','Preliminary Review by SC'),
    (2,'COMBINED_PRELIMINARY_REVIEW','Combined Preliminary Review'),
    (3,'PRELIMINARY_REVIEW_BY_SC_COMBINED_VALIDATION','Preliminary Review by SC Combined Validation');

INSERT INTO gs.transition_from_other_scheme
	(id, code, description)
VALUES
    (0,'NONE','None'),
    (1,'CDM_METHODOLOGY','CDM Methodology'),
    (2,'VCS','VCS'),
    (3,'CCER','CCER'),
    (4,'REC','REC');

INSERT INTO gs.transition_pathway
	(id, code, description)
VALUES
    (0,'CDM_TO_GS_CDM','CDM to GS CDM'),
    (1,'CDM_TO_GS_VER','CDM to GS VER'),
    (2,'GS_CER_TO_GS_VER','GS CER to GS VER'),
    (3,'GS_CDM_TO_GS_VER','VCS to GS VER'),
    (4,'VCS_TO_GS_VER','CCER to GS VER'),
    (5,'CCER_TO_GS_VER','REC to GS VER'),
    (6,'REC_TO_GS_VER','REC to GS VER');

INSERT INTO gs.document_display_category
    (id, code, description)
VALUES
    (1,'REQUIRED','Required'),
    (2,'OTHER','Other'),
    (3,'SUPPORTING','Supporting');

INSERT INTO gs.request_type_assignment_type
    (request_type_id, assignment_type_id, mandatory)
VALUES
    (1000, 1, true),
    (1000, 2, false);


INSERT INTO gs.assignment_type_account_type
    (assignment_type_id, account_type_id)
VALUES
    (1, 3),
    (2, 1),
    (2, 2);

INSERT INTO gs.request_status_transition
    (request_status_id, permitted_request_status_id, minimum_round_of_review_count, permitted_during_consultation)
VALUES
    (11, 8, null, true),
    (11, 9, null, true),
    (11, 10, 1, true),
    (11, 7, null, false),
    (10, 8, null, false);

INSERT INTO gs.request_status_account_type
    (request_status_id, account_type_id, can_progress)
VALUES
    (11, 1, true),
    (6, 3, false);

INSERT INTO gs.request_type_document_type
    (request_type_id, document_type_id, document_display_category_id)
VALUES
    (1001, 1, 1),
    (1000, 1, 1),
    (1001, 999999, 3),
    (1000, 999999, 3);

INSERT INTO gs.request_type_consultation
    (request_type_id, requires_consultation)
VALUES
    (1001, true),
    (1000, false),
    (1002, true);

INSERT INTO gs.batch_type
	(id, code, description)
VALUES
    (0,'POA_PLUS_ONE_REAL_CASE','PoA + 1 Real Case'),
    (1,'POA_PLUS_ONE_OR_MORE_REAL_CASE','PoA + 1 or more Real Case'),
    (2,'REAL_CASE_PLUS_ONE_OR_MORE_REGULAR_CASE','Real Case + 1 or more Regular Case'),
    (3,'TWO_OR_MORE_REGULAR_CASE','2 or more Regular Case');

INSERT INTO gs.batch_mapping
    (batch_type_id, request_type_id, project_classification_id)
VALUES
    (0, 1000, 7),
    (1, 1000, 7),
    (0, 1000, 8),
    (1, 1001, 8),
    (0, 1001, 9),
    (1, 1001, 9),
    (0, 1000, 5);

INSERT INTO gs.request_round_status
    (id, code, description)
VALUES
    (1, 'OPEN', 'Open'),
    (2, 'CLOSED', 'Closed');

INSERT INTO gs.project_status_update_mapping
    (request_status_id, request_type_id, project_status_id)
VALUES
    (4, 0, 4),
    (4, 1000, 2),
    (4, 1001, 3),
    (4, 1008, 3);

INSERT INTO gs.pathway (id, name, index, fast_track)
VALUES
(0, 'Completeness Check', 0, NULL),
(1, 'Preliminary Review Fast Track', 1, TRUE),
(2, 'QA/QC Internal Only', 2, FALSE),
(3, 'Review By RoE', 3, FALSE);

INSERT INTO gs.pathway_group (id, name, index, total_days, pathway_id)
VALUES
(0, 'Completeness Check', 0, 10, 0),
(1, 'Completeness Check', 1, 3, 0),
(2, 'Preliminary Review Fast Track', 0, 15, 1),
(3, 'Preliminary Review Fast Track', 1, 8, 1),
(4, 'QA/QC Internal Only', 0, 21, 2),
(5, 'QA/QC Internal Only', 1, 15, 2),
(6, 'Review by RoE', 0, 21, 3),
(7, 'Review by RoE', 1, 15, 3);

INSERT INTO gs.activity (id, name, display_name, index, total_days, internal, account_type_id, pathway_group_id)
VALUES
(0, 'Assignment of CC', 'Assigning for CC', 0, 2, TRUE, 4, 0),
(1, 'Complete Completeness Check', 'Round 1 Completeness Check', 1, 8, TRUE, 4, 0),
(2, 'Complete Completeness Check', 'Round 2+ Completeness Check', 0, 3, TRUE, 4, 1),
(3, 'Assignment of PR', 'Assigning for Review', 0, 2, TRUE, 4, 2),
(4, 'Conclusion of Review', 'Round 1 Review', 1, 13, TRUE, 4, 2),
(5, 'Conclusion of Review', 'Round 2+ Review', 0, 8, TRUE, 4, 3),
(6, 'Assignment for Project Review', 'Assigning for Review', 0, 2, TRUE, 4, 4),
(7, 'Conclusion of Review', 'Round 1 Review', 1, 10, TRUE, 0, 4),
(8, 'Harmonisation of Feedback', 'Harmonising Round 1 Feedback',2, 9, TRUE, 4, 4),
(9, 'Conclusion of Review', 'Round 2+ Review', 0, 8, TRUE, 0, 5),
(10, 'Harmonisation of Feedback', 'Harmonising Round 2+ Feedback', 1, 7, TRUE, 4, 5),
(11, 'Assignment for Project Review and Obtain Declaration of CoI', 'Assigning for Review', 0, 4, TRUE, 4, 6),
(12, 'Conclusion of Review', 'Round 1 Review', 1, 9, FALSE, 0, 6),
(13, 'Harmonisation of Feedback', 'Harmonising Round 1 Feedback',2, 8, TRUE, 4, 6),
(14, 'Conclusion of Review', 'Round 2+ Review', 0, 9, FALSE, 0, 7),
(15, 'Harmonisation of Feedback', 'Harmonising Round 2+ Feedback', 1, 6, TRUE, 4, 7);

INSERT INTO gs.notification_template (
    id,
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES
(
   1,
   'COMMENT_RECEIVED',
   'v0.0.3',
   'New Comment',
   '',
   '',
   NOW()
),
(
   2,
   'CONSULTATION_STARTED',
   'v0.0.6',
   'Consultation Started',
   '',
   '',
   NOW()
),
(
   3,
   'NGO_CONSULTATION_NOTIFICATION',
   'v0.0.3',
   'NGO Consultation Notification',
   '',
   '',
    NOW()
),
(
   4,
   'REPORT_GENERATED',
   'v0.0.1',
   'GS Assurance Platform Report Available',
   '',
   'Dear ${recipient}, Download your report here: ${download.presignedUrl}. Key ${download.key}, expires on ${download.expiryDate} at ${download.expiryTime}',
    NOW()
),
(
   5,
   'REQUEST_MESSAGE_RECEIVED',
   'v0.0.1',
   'New message received for request',
   '',
   '',
    NOW()
);

INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (1,'IN_PROGRESS','In Progress');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (2,'COMPLETED_NO_PII','Completed No PII');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (3,'COMPLETED_PII_FOUND','Completed PII Found');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (4,'FAILED','Failed');
INSERT INTO gs.pii_action
	(id, code, description)
VALUES
    (0,'NEW','New'),
    (1,'UNDER_INVESTIGATION','Under Investigation'),
    (2,'ACCEPTED','Approved'),
    (3,'REDACTED','Redacted'),
    (4,'PARTIALLY_REDACTED','Partially Redacted'),
    (5,'REMOVED','Removed'),
    (6,'PII_NOT_SUITABLE','PII Not Suitable'),
    (7,'DOCUMENT_REMOVED','Document Removed'),
    (8,'NO_PII_FOUND','No PII Found'),
    (9,'OBSOLETE','Obsolete');

    INSERT INTO gs.conversation_status(id, code, description)
    VALUES
    (0, 'OPEN', 'Open'),
    (1, 'CLOSED', 'Closed');

    INSERT INTO gs.design_cert_date_matrix (id, request_type_id, vvb_assigned, days)
    VALUES (1, 1001, TRUE, 28),
           (2, 1002, TRUE, 28),
           (3, 1003, TRUE, 28),
           (4, 1017, TRUE, 42),
           (5, 1018, TRUE, 42),
           (6, 1019, TRUE, 42),
           (7, 1004, TRUE, 14),
           (8, 1004, FALSE, 28);

INSERT INTO gs.digital_form_type (id, code, description)
VALUES (0, 'ANNUAL_REPORT', 'Annual Report');

INSERT INTO gs.form_definition_status (id, code, description)
VALUES
    (0, 'DRAFT', 'Draft'),
    (1, 'PUBLISHED', 'Published');

INSERT INTO gs.request_form_status (id, code, description)
VALUES
    (0, 'DRAFT', 'Draft'),
    (1, 'SUBMITTED', 'Submitted');