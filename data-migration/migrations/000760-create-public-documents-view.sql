--
-- view to present publicly available documents for projects with status other than 'NEW'
--
DROP VIEW IF EXISTS gs.v_public_documents;
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
    document_access
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
  d.access
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