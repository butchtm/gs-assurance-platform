ALTER VIEW gs.v_categorised_request_document RENAME COLUMN pii_status_code TO pii_scan_status_code;

CREATE OR REPLACE VIEW gs.v_categorised_request_document
AS SELECT rd.request_id,
    rd.document_id,
    r.request_type_id,
    d.document_type_id,
    COALESCE(rtdt.document_display_category_id,
        CASE
            WHEN d.document_type_id = 999999 THEN 2
            ELSE 3
        END) AS document_display_category_id,
    dpss.code AS pii_scan_status_code,
    pa.code AS pii_action_code
   FROM gs.request_document rd
     JOIN gs.request r ON r.id = rd.request_id
     JOIN gs.document d ON d.id = rd.document_id
     LEFT JOIN gs.request_type_document_type rtdt ON r.request_type_id = rtdt.request_type_id AND d.document_type_id = rtdt.document_type_id
     LEFT JOIN gs.document_pii_analysis dpa ON dpa.document_id = d.id
     LEFT JOIN gs.document_pii_scan_status dpss ON dpa.status_id = dpss.id
     LEFT JOIN gs.pii_metadata pm ON d.id = pm.document_id
     LEFT JOIN gs.pii_action pa ON pm.action_id = pa.id;
