INSERT INTO gs.document
(	id,
	container_name,
	file_name,
	file_size,
	hash_value,
	access,
	category,
	document_type_id,
	content_type,
    uploaded_timestamp,
    modified_timestamp,
    is_latest
)
SELECT 
	"ID",
	"ContainerName",
	"FileName",
	"FileSize",
	"Hash",
	CASE WHEN "Access" = 0 THEN false
	     WHEN "Access" = 1 THEN true
	END,
	CASE WHEN "Category" = 0 THEN false
	     WHEN "Category" = 1 THEN true
	END,
	"DocumentType",
	"ContentType",
	"CreatedDate",
	"LastModifiedDate",
    CASE WHEN "IsLatest" = 0 THEN false
         WHEN "IsLatest" = 1 THEN true
    END
FROM
  staging."Documents"
;

-- Filter out Base Project Document relations which relate to Projects with DQ issues
CREATE TABLE gs_dq.base_project_document_with_dqi_project
AS
SELECT
  *
FROM
  staging."BaseProjectDocument"
WHERE
  "ProjectsID" IN (SELECT "ID" FROM gs_dq.projects_missing_requests) OR
  "ProjectsID" IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);


INSERT INTO gs.base_project_document
(
	project_id,
	document_id
)
SELECT
  "ProjectsID",
  "DocumentsID"
FROM
  staging."BaseProjectDocument"
WHERE
  "ProjectsID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_requests) AND
  "ProjectsID" NOT IN (SELECT "ID" FROM gs_dq.projects_missing_referenced_project);
