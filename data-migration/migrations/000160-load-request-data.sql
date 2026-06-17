-- Filter out requests which have missing invoice
CREATE TABLE gs_dq.request_with_missing_invoice
AS
SELECT
	*
FROM
	staging."Reviews"
WHERE
	"InvoiceID" IS NOT NULL AND
	"InvoiceID" NOT IN (SELECT "ID" FROM staging."Invoices");

INSERT INTO gs.request (
	id,
	end_date,
	booking_date,
	request_type_id,
	index,
	request_name,
	last_request_status_change_date,
	current_request_status_id,
	crediting_period,
	invoice_id,
	same_request_type_count,
	request_document_status,
	replacement_request_id,
	emission_reduction_amount,
	manually_added,
	migrated
)
SELECT
	"ID",
	"EndDate",
	"BookingDate",
	"ReviewType",
	"Index",
	"ReviewName",
	"ReviewState_LastStateChangeDate",
	"ReviewState_CurrentState",
	"ReviewCreditingPeriod",
	"InvoiceID",
	"SameReviewTypeCount",
	"ReviewDocumentStatus",
	"ReplacementReviewId",
	"EmissionReductionAmount",
	CASE WHEN "ManuallyAdded" = 0 THEN false
         WHEN "ManuallyAdded" = 1 THEN true
    END,
    true
FROM
    staging."Reviews"
WHERE
	"InvoiceID" IS NULL OR
	"InvoiceID" IN (SELECT "ID" FROM staging."Invoices");

-- Filter out Base Project Requests where Request has DQ issue
CREATE TABLE gs_dq.base_project_request_with_dqi_request
AS
SELECT
    *
FROM staging."BaseProjectReview"
WHERE 
    "ReviewsID" IN (SELECT "ID" FROM gs_dq.request_with_missing_invoice);  

-- Filter out Base Project Requests where Project has DQ issue
CREATE TABLE gs_dq.base_project_request_with_dqi_project
AS
SELECT
    *
FROM staging."BaseProjectReview"
WHERE 
    "ProjectsID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
    "ProjectsID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);  

-- Filter out Base Project Request where Request is missing
CREATE TABLE gs_dq.base_project_request_with_missing_request
AS
SELECT
    *
FROM staging."BaseProjectReview"
WHERE 
    "ReviewsID" NOT IN (SELECT "ID" FROM staging."Reviews");  


INSERT INTO gs.base_project_request (
	project_id,
	request_id
)
SELECT
	"ProjectsID",
	"ReviewsID"
FROM staging."BaseProjectReview"
WHERE
    "ReviewsID" NOT IN (SELECT "ID" FROM gs_dq.request_with_missing_invoice) AND
	"ReviewsID" IN (SELECT "ID" FROM staging."Reviews") AND
    "ProjectsID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
    "ProjectsID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project); 

-- TODO: Add dq tables for request_status_history

INSERT INTO gs.request_status_history (
	id,
	request_id,
	previous_status_id,
	next_status_id,
	changed_by,
	changed_at
)
SELECT
	"ID",
	"ReviewID",
	"PreviousState",
	"NextState",
	"ChangedBy",
	"ChangedAt"
FROM
	staging."ReviewStateChangeLog"
WHERE
    "ReviewID" NOT IN (SELECT "ID" FROM gs_dq.request_with_missing_invoice) AND
	"ReviewID" IN (SELECT "ID" FROM staging."Reviews");

-- TODO: Add dq tables for request_document

INSERT INTO gs.request_document (
	request_id,
	document_id
)
SELECT
	"ReviewsID",
	"DocumentsID"
FROM
	staging."DocumentReview"
WHERE
    "ReviewsID" NOT IN (SELECT "ID" FROM gs_dq.request_with_missing_invoice) AND
	"ReviewsID" IN (SELECT "ID" FROM staging."Reviews");