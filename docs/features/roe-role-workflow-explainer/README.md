---
name: roe-role-workflow-explainer
description: Explains the ROE and ARM assignment summaries and how they reflect request workflow progress.
---

# ROE and ARM assignment summaries

## Purpose

`AssignmentSummary.ROE` and `AssignmentSummary.ARM` are operational dashboards
for assignment workload. They aggregate requests that have been assigned to
review or QA/QC work and turn the current assignment/request workflow into
counts by account.

They do not create requests, assign work, advance request status, or decide
whether a request is valid. They read the assignment and request tables and
report where work currently sits.

## Entry points and flow

The secured endpoints are exposed by
`AssignmentSummaryController` for Admin and ARM users:

`GET /assignment-summaries/review` -> `AssignmentSummaryService` -> ROE finder -> `RoeAssignmentSummaryQueryBuilder` -> assignment/request/activity joins -> paginated counts and totals.

`GET /assignment-summaries/arm` -> `AssignmentSummaryService` -> ARM finder -> `ArmAssignmentSummaryQueryBuilder` -> assignment/request/activity joins -> paginated counts and totals.

The same finder path is used to generate the review and ARM spreadsheet
reports through `POST /assignment-summaries/reports/review` and
`POST /assignment-summaries/reports/arm`.

## ROE summary

ROE is the review-assignment view. Its row model,
`RoeAssignmentSummary`, contains:

- reviewer account identity and account type;
- assignment type;
- open request count;
- closed request count;
- first-round count;
- in-progress count;
- due count; and
- overdue count.

The query counts distinct requests associated with review assignments. It
classifies them using the request's current request status:

- closed: request status IDs `4`, `5`, `7`, or `8`;
- open: request status not in that closed set;
- in progress: request status `10`;
- first round: request status `10` and current round index `1`.

Due and overdue counts additionally use review activities, specifically the
conclusion-of-review activity IDs, open activities (`actual_end_date IS NULL`),
and planned end dates. Due work is bounded by the requested due-date filter or
the default upcoming working-day window; overdue work has a planned end date
that has passed.

ROE also distinguishes normal review assignments from QA/QC assignments. The
ordinary review summary uses the review assignment type. The QA/QC variant
joins QA/QC assignments when the relevant summary path requests that behavior.

## ARM summary

ARM is the ARM-team coordination view. Its row model,
`ArmAssignmentSummary`, contains:

- account name;
- upcoming count;
- due count; and
- overdue count.

The ARM query combines two kinds of work:

1. Upcoming reviewer round-completion work, where the request is in progress,
   the activity is a conclusion-of-review activity, and the activity is not
   complete.
2. ARM QA/QC round-completion work, where the request is in round-completion
   status (`13`), the activity is a harmonisation-of-feedback activity, the
   assignment is QA/QC, and the activity is not complete.

Due and overdue values are based on the activity planned end date. Results are
grouped by the account associated with the assignment and only accounts with
at least one matching upcoming, due, or overdue item are returned.

## Relationship to request workflow

The summaries reflect workflow state through the relationships below:

`request` -> `request_account_assignment` -> assignment type/account

`request` -> `request_activity` -> activity type, planned end date, actual end date

The request's current status determines whether ROE work is open, closed,
in-progress, or in first round. The activity's lifecycle determines whether
work is still open, due, or overdue. Assignment type determines whether the
work belongs in the review-oriented ROE path or the QA/QC/ARM path.

This means a request normally appears in these summaries only after the
workflow has created the relevant assignment and activity records. Changing a
request status or completing an activity changes the counts on subsequent
summary queries; the summary itself does not perform that change.

## How requests are assigned to ARM and ROE

The assignment is stored in `RequestAccountAssignment`. It links a request to
an account and an assignment type. The important assignment types are:

- `REVIEW` (`1`): the review work assigned to a reviewer account; this is the
  ROE-facing assignment type.
- `QAQC` (`2`): QA/QC work, including the ARM team's harmonisation-of-feedback
  work; this is the ARM-facing assignment type in the summary queries.
- `COMPLETENESS` (`4`): completeness work. `AssignmentFactory` forces this
  assignment type to the configured ARM-team account rather than using the
  submitted account ID.

The operational handoff is:

`ARM/Admin selects request + target account + assignment type` -> `POST /assignments` -> `AssignmentService` -> persist `RequestAccountAssignment` -> optional assignment notification -> request appears in the relevant assigned-request and summary views.

For an ROE review, the ARM team selects the reviewer account by sending its
account ID together with assignment type `REVIEW`. There is no separate
“choose ROE” operation in `AssignmentSummary`; the summary reads the
assignment that was already created. The reviewer sees the request through the
assignment attached to their account and can update the assignment status
through `PUT /assignments/{assignmentId}/status` when permitted.

### Individual VVB users

There is no direct individual-user assignment mechanism in this model.
`RequestAccountAssignment` stores a `requestId`, an `Account`, an assignment
type, an assignment status, and dates; it does not store a user ID. To assign a
request to a VVB, ARM/Admin assigns the request to the VVB account using the
appropriate assignment type (normally the audit assignment, ID `0`). VVB users
associated with that account then obtain access to the account's assigned
requests through their account association.

The assigned-request view exposes both the VVB account and reviewer account
IDs/names, and `RequestFinder` scopes assigned-request lookup using the
authenticated user's account ID. This means all authorized users in the
assigned VVB account share the account-level assignment; the current code does
not select one named VVB user as the sole assignee. A VVB user can update an
assignment status only when the assignment belongs to their account and their
role/status combination is permitted.

The assignment create/update payload is
`AssignmentCreateUpdateDTO`: `requestId`, `accountId`, `assignmentTypeId`, and
optionally `assignmentStatusId`. Assignment creation and reassignment are
allowed to Admin, ARM, and Project Developer roles. ARM/Admin are unrestricted
assigners; Project Developers may assign only requests belonging to their own
project account. Batch child requests cannot be assigned or updated directly;
`BatchRequestChildValidator` rejects those operations, so assignment work is
managed through the batch's permitted request path.

The request type also has reference-data rules describing which assignment
types are mandatory or available. They are exposed by
`GET /request-type-assignment-types/{requestTypeId}`. These rules support the
assignment UI, but `AssignmentService` does not itself validate that the
submitted assignment type is one of those rules before saving. Likewise, the
current create path does not visibly enforce that the target account's account
type is compatible with the assignment type; account/assignment-type mappings
exist in reference data and are used for account filtering and permissions.

Assignment status is a separate lifecycle from request status. Assigned
reviewers/VVBs may update their own assignment status only for statuses allowed
by their role, while ARM/Admin have broader assignment-management permissions.

For batch requests, the summary operates at the request level. Each request
created for a project in a batch can have assignment/activity records and can
contribute to the relevant counts. The summary does not apply the batch's PoA,
Real Case VPA, Regular VPA, or batch-type hierarchy rules. Those are handled by
the batch request and project-selection services. Batch parent/child behavior
can affect how request updates and assignments are coordinated, but ROE/ARM
only reports the resulting request assignments that satisfy its query.

## Available filters

`AssignmentSummaryFiltersDTO` supports filters for:

- assignment type;
- whether QA/QC is assigned to the calling account;
- last submitted date range;
- request due-before date;
- project type; and
- request type.

Project-type filtering is applied through `gs.base_project_request` and
`gs.project_project_type`. Request-type filtering is applied to the request
linked to that base-project request. These filters narrow the workflow work
being counted; they do not change assignment or request state.

## Successful output

The API returns a paginated `AssignmentSummaryResponseDTO` containing:

- the summary rows grouped by account; and
- aggregate totals calculated with the same filters.

The report endpoints export the corresponding filtered summary to XLSX.

## Handled failures and restrictions

- Access is restricted to the Admin and ARM roles by
  `AssignmentSummaryController`.
- Empty query results produce an empty summary page/totals representation.
- Pagination and account-name sorting are applied at the query layer.
- Incomplete activities are excluded from due/overdue work by requiring a null
  actual end date.
- Closed/open and due/overdue categories are calculated from explicit request
  status and activity-date conditions rather than from project status.

## Unhandled or notable issues

- Assignment summaries do not filter by the project's lifecycle status
  (`New`, `Listed`, `Design Certified`, `Certified Project`, or equivalent).
- They do not validate that a request type is appropriate for the project's
  current lifecycle status; request type is only an optional reporting filter.
- The summary is an aggregate view and does not explain why an assignment was
  created or repair missing assignment/activity records.
- Assignment creation has a validation gap: request-type/assignment-type
  reference rules and target-account/assignment-type compatibility are not
  fully revalidated by `AssignmentService` at write time. Impact: medium.
- Individual VVB-user targeting is not supported; assignment and access are
  account-scoped. Impact: medium if work must be routed to one named user
  within a VVB account.

## Implementation references

- `back-end/src/main/java/org/goldstandard/assurance/api/controller/AssignmentSummaryController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/AssignmentSummaryService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/assignmentsummary/RoeAssignmentSummary.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/assignmentsummary/ArmAssignmentSummary.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/repository/assignmentsummary/RoeAssignmentSummaryQueryBuilder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/repository/assignmentsummary/ArmAssignmentSummaryQueryBuilder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/repository/assignmentsummary/AssignmentSummaryQueryComponents.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/AssignmentSummaryFiltersDTO.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/controller/AssignmentController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/controller/RequestAssignmentController.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/AssignmentService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/AssignmentFactory.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/AssignmentPermissionsService.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/AssignedRequestAccountFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/RequestFinder.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/RequestAccountAssignment.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/entity/view/AssignedRequestView.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/dto/AssignmentCreateUpdateDTO.java`
- `back-end/src/main/java/org/goldstandard/assurance/api/service/RequestTypeAssignmentTypeService.java`
- `back-end/src/test/java/org/goldstandard/assurance/api/AssignmentSummaryApiIntegrationTest.java`
