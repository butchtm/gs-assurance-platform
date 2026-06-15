--
-- map legacy request types to new request types
-- preparatory to generating the legacy request/document categorisation
--

CREATE TABLE pg_temp.legacy_request_type_mapping (
  legacy_request_type_id int4 NOT NULL,
  new_request_type_code varchar NOT NULL
);

INSERT INTO pg_temp.legacy_request_type_mapping (
  legacy_request_type_id,
  new_request_type_code
) VALUES
     (0,'PRELIMINARY_REVIEW')
    ,(1,'PRELIMINARY_REVIEW')
    ,(2,'GENERIC')
    ,(3,'DESIGN_CERTIFICATION_PROJECT')
    ,(4,'GENERIC')
    ,(5,'PERFORMANCE_CERTIFICATION_|_PROJECT')
    ,(6,'DESIGN_CERTIFICATION_RENEWAL_|_PROJECT')
    ,(7,'DESIGN_CHANGE_|_WITH_VVB')
    ,(8,'GENERIC')
    ,(9,'DESIGN_CERTIFICATION_RENEWAL_|_PROJECT')
    ,(10,'DESIGN_CHANGE_|_WITH_VVB')
    ,(11,'GENERIC')
    ,(12,'DESIGN_CHANGE_|_WITH_VVB')
    ,(13,'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER')
    ,(14,'TRANSITION_REVIEW_|_GS_CDM_TO_GS_VER')
    ,(15,'PRELIMINARY_REVIEW')
    ,(16,'PRELIMINARY_REVIEW')
;

--
-- map legacy document types to new document types
-- preparatory to generating the legacy request/document categorisation
--

CREATE TABLE pg_temp.legacy_document_type_mapping (
  legacy_document_type_id int4 NOT NULL,
  new_document_type_code varchar NOT NULL
);

INSERT INTO pg_temp.legacy_document_type_mapping (
  legacy_document_type_id,
  new_document_type_code
) VALUES
     (0,'OTHER_STANDARD_PDD')
    ,(1,'DESIGN_DOCUMENT')
    ,(2,'COVER_LETTER')
    ,(3,'TERMS_AND_CONDITIONS')
    ,(4,'ODA_DECLARATION')
    ,(5,'EMISSION_REDUCTIONS')
    ,(6,'OTHER_STANDARD_VALIDATION_REPORT')
    ,(7,'VALIDATION_REPORT')
    ,(8,'OTHER_STANDARD_MONITORING_REPORT')
    ,(9,'OTHER_STANDARD_VERIFICATION_REPORT')
    ,(10,'MONITORING_REPORT')
    ,(11,'VERIFICATION_REPORT')
    ,(12,'MONITORING_REPORT')
    ,(13,'STAKEHOLDER_CONSULTATION')
    ,(14,'DESIGN_DOCUMENT')
    ,(15,'VALIDATION_REPORT')
    ,(16,'EMISSION_REDUCTIONS')
    ,(17,'COVER_LETTER')
    ,(18,'DESIGN_DOCUMENT')
    ,(19,'TRANSITION_REQUEST_FORM')
    ,(20,'STAKEHOLDER_CONSULTATION')
    ,(21,'STAKEHOLDER_CONSULTATION')
    ,(23,'STAKEHOLDER_CONSULTATION')
    ,(24,'STAKEHOLDER_CONSULTATION')
    ,(27,'EMISSION_REDUCTIONS')
    ,(28,'VALIDATION_REPORT')
    ,(31,'DESIGN_DOCUMENT')
    ,(34,'STAKEHOLDER_CONSULTATION')
    ,(35,'VALIDATION_REPORT')
    ,(39,'VALIDATION_REPORT')
    ,(40,'VALIDATION_REPORT')
    ,(41,'VALIDATION_REPORT')
    ,(46,'PRELIMINARY_REVIEW_REQUEST_FORM')
    ,(47,'SDG_TOOL')
    ,(48,'ANNUAL_REPORT')
    ,(49,'PRELIMINARY_REVIEW_REQUEST_FORM')
;

--
-- Populate the actual mapping table for legacy doc and request types
--
INSERT INTO gs.request_type_document_type (
	request_type_id,
    document_type_id,
    document_display_category_id
)
SELECT
  lrtm.legacy_request_type_id,
  ldtm.legacy_document_type_id,
  ddc.id
FROM
  gs.temp_gs_new_mappings gnm
JOIN
  pg_temp.legacy_request_type_mapping lrtm
ON
  gnm.request_type = lrtm.new_request_type_code
JOIN
  pg_temp.legacy_document_type_mapping ldtm
ON
  gnm.document_type = ldtm.new_document_type_code
LEFT JOIN
  gs.document_display_category ddc
ON
  gnm.display_category = ddc.code
;


DROP TABLE gs.temp_gs_new_mappings;