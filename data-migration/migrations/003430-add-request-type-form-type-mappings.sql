ALTER TABLE gs.request_type_document_type
ADD COLUMN form_type_id int4 NULL;

ALTER TABLE gs.request_type_document_type ADD FOREIGN KEY (form_type_id) REFERENCES gs.digital_form_type (id);

-- add annual report form type to annual report request and document type
UPDATE gs.request_type_document_type
SET
    form_type_id = 0
WHERE
    request_type_id = 1014
    AND document_type_id = 1012;