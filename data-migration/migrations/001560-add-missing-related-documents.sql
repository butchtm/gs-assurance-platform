-- New GS Document Types
INSERT INTO gs.document_type
	(id, code, description, is_active)
VALUES 
	 (1018,'POA_DESIGN_DOCUMENT','PoA Design Document',true)
	,(1019,'VPA_DESIGN_DOCUMENT','VPA Design Document',true)
	,(1020,'DESIGN_CONSULTATION_REPORT','Design Consultation Report',true)
;

CREATE TEMP TABLE temp_gs_new_mappings (
    request_type TEXT,
    document_type TEXT,
    display_category TEXT
);

--
-- New mappings generated from the Confluence page
--
INSERT INTO pg_temp.temp_gs_new_mappings (
	request_type,
	document_type,
	display_category
) VALUES
    ('DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA', 'POA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('DESIGN_CERTIFICATION_POA_WITH_REAL_CASE_VPA', 'DESIGN_CONSULTATION_REPORT', 'REQUIRED')
   ,('INCLUSION_REAL_CASE_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('INCLUSION_REGULAR_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA', 'POA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('DESIGN_CERTIFICATION_RENEWAL_|_POA_WITH_REAL_CASE_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('RENEWAL_|_REAL_CASE_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('RENEWAL_|_REGULAR_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('GENERIC', 'POA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('GENERIC', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('GENERIC', 'DESIGN_CONSULTATION_REPORT', 'REQUIRED')
   ,('COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA', 'POA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
   ,('COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_POA', 'DESIGN_CONSULTATION_REPORT', 'REQUIRED')
   ,('COMBINED_DESIGN_AND_PERFORMANCE_CERTIFICATION_|_VPA', 'VPA_DESIGN_DOCUMENT', 'REQUIRED')
;

--- Insert New mappings into mapping table ---
INSERT INTO gs.request_type_document_type (
	request_type_id,
    document_type_id,
    document_display_category_id
)
SELECT
  rt.id,
  dt.id,
  ddc.id
FROM
  pg_temp.temp_gs_new_mappings gnm
LEFT JOIN
  gs.request_type rt
ON
  gnm.request_type = rt.code AND
  rt.is_active = true
LEFT JOIN
  gs.document_type dt
ON
  gnm.document_type = dt.code AND
  dt.is_active = true
LEFT JOIN
  gs.document_display_category ddc
ON
  gnm.display_category = ddc.code
;

DROP TABLE IF EXISTS pg_temp.temp_gs_new_mappings;