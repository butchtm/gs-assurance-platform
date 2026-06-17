-- Filter out bundles where master project has DQ issues
CREATE TABLE gs_dq.bundles_with_dqi_master_project
AS
SELECT
  *
FROM
  staging."Bundles"
WHERE
  "MasterProjectID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
  "MasterProjectID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO gs.bundle (
	id,
	name,
	master_project_category_id,
	master_project_id
)
SELECT
	"ID",
	"Name",
	"MasterProjectCategory",
	"MasterProjectID"
FROM
    staging."Bundles"
WHERE
    "MasterProjectID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
    "MasterProjectID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);    


-- add foreign key from project to bundle
ALTER TABLE gs.project 
ADD FOREIGN KEY (bundle_id) REFERENCES gs.bundle ON DELETE RESTRICT;