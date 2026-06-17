CREATE VIEW gs.v_categorised_request_form
AS SELECT
	rf.id AS form_id,
    rf.request_id AS request_id,
    COALESCE(rtdt.document_display_category_id,
      CASE WHEN rtdt.document_type_id = 999999 THEN 2 -- other document type -> other form category
      ELSE 3 -- otherwise supporting form category
      END
    ) as display_category_id
FROM gs.request_form rf
LEFT JOIN gs.form_definition fd
    ON rf.definition_id = fd.id
LEFT JOIN gs.request r
    ON rf.request_id = r.id
LEFT JOIN gs.request_type_document_type rtdt
    ON r.request_type_id = rtdt.request_type_id
    AND fd.type_id = rtdt.form_type_id