DROP VIEW gs.v_public_documents;
DROP VIEW gs.v_categorised_request_document;

CREATE VIEW gs.v_categorised_request_document
AS
SELECT
    rd.request_id,
    rd.document_id,
    r.request_type_id,
    d.document_type_id,
    coalesce(
        rtdt.document_display_category_id,
        (SELECT id FROM gs.document_display_category WHERE code = 'OTHER')
    ) as document_display_category_id
FROM 
    gs.request_document rd
INNER JOIN
    gs.request r
ON 
    r.id = rd.request_id 
INNER JOIN 
    gs.document d
ON 
    d.id = rd.document_id 
LEFT JOIN
    gs.request_type_document_type rtdt 
ON 
    r.request_type_id  = rtdt.request_type_id and 
    d.document_type_id = rtdt.document_type_id;

GRANT SELECT ON gs.v_categorised_request_document TO gs_assurance_db_writer;

CREATE VIEW gs.v_public_documents
(
    gsid,
    project_name,
    project_status_id,
    project_status_code,
    project_status_description,
    request_id,
    request_name,
    request_index,
    request_type_id,
    request_type_code,
    request_type_description,
    document_display_category_id,
    document_display_category_code,
    document_display_category_description,
    document_type_id,
    document_type_code,
    document_type_description,
    document_id,
    document_file_name,
    document_file_size,
    document_content_type,
    document_access,
    document_uploaded,
    document_modified
)
AS SELECT
  p.gsid,
  p.name,
  p.project_status_id,
  ps.code,
  ps.description,
  r.id,
  r.request_name,
  r.index,
  r.request_type_id,
  rt.code,
  rt.description,
  vcrd.document_display_category_id,
  ddc.code,
  ddc.description,
  vcrd.document_type_id,
  dt.code,
  dt.description,
  d.id,
  d.file_name,
  d.file_size,
  d.content_type,
  d.access,
  d.uploaded_timestamp,
  d.modified_timestamp
FROM
  gs.project p
INNER JOIN
  gs.project_status ps
ON
  p.project_status_id = ps.id
INNER JOIN
  gs.base_project_request bpr
ON
  p.id = bpr.project_id
INNER JOIN
  gs.v_categorised_request_document vcrd
ON
  bpr.request_id = vcrd.request_id
INNER JOIN
  gs.document_display_category ddc
ON
  vcrd.document_display_category_id = ddc.id
INNER JOIN
  gs.document_type dt
ON
  vcrd.document_type_id = dt.id
INNER JOIN
  gs.request r
ON
  bpr.request_id = r.id
INNER JOIN
  gs.request_type rt
ON
  r.request_type_id = rt.id
INNER JOIN
  gs."document" d
ON
  vcrd.document_id = d.id
WHERE
  d.access = true AND
  ps.id != 1;

GRANT SELECT ON gs.v_public_documents TO gs_assurance_db_writer;

UPDATE gs.request_type_document_type
SET document_display_category_id = 3
WHERE document_display_category_id = 2;