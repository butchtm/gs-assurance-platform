--
-- helper view for the API to get request documents categorised by display type
--
CREATE VIEW gs.v_categorised_request_document
AS
SELECT
    rd.request_id,
    rd.document_id,
    r.request_type_id,
    d.document_type_id,
    coalesce(
        rtdt.document_display_category_id,
        (SELECT id FROM gs.document_display_category WHERE code = 'SUPPORTING')
    )
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
    d.document_type_id = rtdt.document_type_id 
