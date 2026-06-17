drop table if exists gs.document_probable_pii;
drop table if exists gs.document_pii_analysis;
drop table if exists gs.document_pii_scan_status;

-- NEW TABLES
CREATE TABLE gs.document_pii_scan_status (
	id int4 NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	CONSTRAINT document_pii_scan_status_pkey PRIMARY KEY (id)
);

INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (1,'IN_PROGRESS','In Progress');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (2,'COMPLETED_NO_PII','Completed No PII');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (3,'COMPLETED_PII_FOUND','Completed PII Found');
INSERT INTO gs.document_pii_scan_status (id,code,description)
	VALUES (4,'FAILED','Failed');

CREATE TABLE gs.document_pii_analysis (
	id serial4 NOT NULL,
	document_id uuid NOT NULL,
	status_id int4 NULL,
	CONSTRAINT document_pii_analysis_result_pkey PRIMARY KEY (id), 
	CONSTRAINT document_pii_analysis_result_document_id_fkey FOREIGN KEY (document_id) REFERENCES gs.document(id) ON DELETE RESTRICT,
	CONSTRAINT document_pii_analysis_result_status_id_fkey FOREIGN KEY (status_id) REFERENCES gs.document_pii_scan_status(id) ON DELETE RESTRICT
);

CREATE TABLE gs.document_probable_pii (
	id serial4 NOT NULL,
	document_id uuid NOT NULL,
	entity varchar NOT NULL,
	probability varchar NULL,
	page_number int4 NULL,
	CONSTRAINT document_probable_pii_pkey PRIMARY KEY (id), 
	CONSTRAINT document_probable_pii_document_id_fkey FOREIGN KEY (document_id) REFERENCES gs.document(id) ON DELETE RESTRICT
);

-- Index for frequently queried columns
CREATE INDEX document_pii_analysis_document_id ON gs.document_pii_analysis (document_id);
CREATE INDEX document_probable_pii_document_id ON gs.document_probable_pii (document_id);

-- NEW SEQUENCES
CREATE SEQUENCE
  gs.seq_document_pii_analysis
AS
  integer
OWNED BY
  gs.document_pii_analysis.id;

ALTER TABLE gs.document_pii_analysis
ALTER COLUMN id SET DEFAULT nextval('gs.seq_document_pii_analysis');

CREATE SEQUENCE
  gs.seq_document_probable_pii
AS
  integer
OWNED BY
  gs.document_pii_analysis.id;

ALTER TABLE gs.document_probable_pii
ALTER COLUMN id SET DEFAULT nextval('gs.seq_document_probable_pii');

-- NEW COLUMNS
ALTER TABLE gs.pii_metadata ADD document_id uuid NULL;
ALTER TABLE gs.pii_metadata ADD CONSTRAINT fk_pii_metadata_document_id FOREIGN KEY (document_id) REFERENCES gs.document(id) ON DELETE RESTRICT;

-- VIEW CHANGES
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
    dpss.code AS pii_status_code
   FROM gs.request_document rd
     JOIN gs.request r ON r.id = rd.request_id
     JOIN gs.document d ON d.id = rd.document_id
     LEFT JOIN gs.request_type_document_type rtdt ON r.request_type_id = rtdt.request_type_id AND d.document_type_id = rtdt.document_type_id
     LEFT JOIN gs.document_pii_analysis dpa ON dpa.document_id = d.id
     LEFT JOIN gs.document_pii_scan_status dpss ON dpa.status_id = dpss.id;

CREATE OR REPLACE VIEW gs.v_grouped_document_pii
AS SELECT 
	row_number() over() as id,
	document_id,
    entity,
    probability,
    page_number,
    count(*) AS count
   FROM gs.document_probable_pii
  GROUP BY document_id, entity, probability, page_number;

-- PERMISSIONS
GRANT SELECT ON gs.document_pii_scan_status TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE ON gs.document_pii_analysis TO gs_assurance_db_writer;
GRANT SELECT, INSERT, UPDATE ON gs.document_probable_pii TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_document_pii_analysis TO gs_assurance_db_writer;
GRANT USAGE, SELECT ON gs.seq_document_probable_pii TO gs_assurance_db_writer;
GRANT SELECT ON gs.v_grouped_document_pii TO gs_assurance_db_writer;

-- NEW NOTIFICATION TEMPLATE
INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('DOCUMENT_PII_FOUND', 'v0.0.1', 'Action Required: Possible PII Detected in Uploaded Document', '<div style="font-family: Arial,sans-serif;">
  <p>Dear ${recipient},</p>
  <p>
  A document you recently uploaded has been flagged for containing potential <strong>Personally Identifiable Information (PII)</strong>.
  </p>
  <p>
  As part of our commitment to data privacy and compliance with the <strong>General Data Protection Regulation (GDPR)</strong>, we automatically scan uploaded documents to identify and protect sensitive information. PII includes data such as names, addresses, identification numbers, and other information that could be used to identify an individual.
  </p>
  <p>
  <strong>Why this matters:</strong><br></br>
  Under GDPR, organizations must ensure that personal data is only processed when necessary and lawful. Including unnecessary PII in documents can pose privacy risks and may lead to regulatory consequences.
  <p>
  </p>
  <p>
    <strong>What you need to do:</strong>
    <ol>
      <li>Review the document and the table below, which outlines the types of PII detected and the pages on which they appear.</li>
      <li>If the PII is not relevant or required, please redact the sensitive information.</li>
      <li>Upload a revised version of the document without the unnecessary PII. If no redaction or removal is required, this step is not required, and no action is necessary.</li>
    </ol>
  </p>
  <p><strong>Next steps:</strong><br></br>
    The <strong>ARM Team</strong> will also conduct a separate review of the document. If they determine that the document contains inappropriate or excessive PII, they may remove it from the platform and contact you to upload a revised version. If the document is intended for public access, the ARM Team will also assess and approve it for publication, where appropriate.
  </p>
  <p>
  Best regards,
  </p>
  <p>
  The Gold Standard Foundation
  </p>

  <hr style="border: 1px solid #cccccc;"/>
  
  <table>
  <tr>
    <td><strong>Request:</strong></td>
    <td>${request.name}</td>
  </tr>
  <tr>
    <td><strong>GSID:</strong></td>
    <td>${project.gsid}</td>
  </tr>
  <tr>
    <td><strong>Project:</strong></td>
    <td>${project.name}</td>
  </tr>
  </table>

  <hr style="border: 1px solid #cccccc;"/>
  <p><strong>Document</strong>: ${documentPii.documentName}</p>
  
  <table style="border: 1px solid #cccccc; padding: 6px; border-collapse: collapse;">
    <tr>
      <th style="border: 1px solid #cccccc; padding: 6px">Entity</th>
      <th style="border: 1px solid #cccccc; padding: 6px">Probability</th>
      <th style="border: 1px solid #cccccc; padding: 6px">Page Number</th>
      <th style="border: 1px solid #cccccc; padding: 6px">No. Occurrences</th>
    </tr>
    <#list documentPii.probablePii![] as probablePii>
      <tr>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.entity!}</td>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.probability!}</td>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.pageNumber!}</td>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.occurrencesOnPage!}</td>
      </tr>
    </#list>
  </table>
</div>', 'Dear ${recipient},

A document you recently uploaded has been flagged for containing potential Personally Identifiable Information (PII).

As part of our commitment to data privacy and compliance with the General Data Protection Regulation (GDPR), we automatically scan uploaded documents to identify and protect sensitive information. PII includes data such as names, addresses, identification numbers, and other information that could be used to identify an individual.

Why this matters:
Under GDPR, organizations must ensure that personal data is only processed when necessary and lawful. Including unnecessary PII in documents can pose privacy risks and may lead to regulatory consequences.

What you need to do:
1. Review the document and the table below, which outlines the types of PII detected and the pages on which they appear.
2. If the PII is not relevant or required, please redact the sensitive information.
3. Upload a revised version of the document without the unnecessary PII. If no redaction or removal is required, this step is not required, and no action is necessary.

Next steps:
The ARM Team will also conduct a separate review of the document. If they determine that the document contains inappropriate or excessive PII, they may remove it from the platform and contact you to upload a revised version. If the document is intended for public access, the ARM Team will also assess and approve it for publication, where appropriate.

Best regards,

The Gold Standard Foundation

Request: ${request.name}
GSID: ${project.gsid}
Project: ${project.name}

Document name: ${documentPii.documentName}

Entity\tProbability\tPage Number\tNo. Occurrences

<#list documentPii.probablePii![] as probablePii>
  ${probablePii.entity!}\t${probablePii.probability!}\t${probablePii.pageNumber!}\t${probablePii.occurrencesOnPage!}
</#list>', '2025-07-31 22:14:03.455');