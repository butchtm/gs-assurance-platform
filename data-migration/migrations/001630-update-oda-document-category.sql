--- Fix ODA Declaration Category ---
UPDATE gs.request_type_document_type
SET document_display_category_id = 1
WHERE request_type_id = 1000 AND document_type_id = 1004; --- Preliminary Review and ODA Declaration ---