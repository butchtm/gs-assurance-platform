-- Filter out Project Status Change Logs which relate to Projects with DQ issues
CREATE TABLE gs_dq.project_status_historys_with_dqi_project
AS
SELECT
  *
FROM
  staging."ProjectStateChangeLog"
WHERE
  "BaseProjectID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
  "BaseProjectID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);

-- Filter out Project Status Change Logs which relate to null BaseProjectID
CREATE TABLE gs_dq.project_status_historys_with_null_base_project_id
AS
SELECT
  *
FROM
  staging."ProjectStateChangeLog"
WHERE
  "BaseProjectID" IS NULL;

-- TODO: Multiple identical status transitions for same BaseProjectID
-- TODO: Are we missing dates for the status transitions?
--       Both raised with SC
--       For now, we are excluding transitions with no BaseProjectID and
--                not deduping the transitions
INSERT INTO gs.project_status_history (
	id,
	base_project_id,
	previous_status_id,
	next_status_id
)
SELECT
	"ID",
	"BaseProjectID",
	"PreviousState",
	"NextState"
FROM staging."ProjectStateChangeLog"
WHERE
  "BaseProjectID" IS NOT NULL AND
  "BaseProjectID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "BaseProjectID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);
