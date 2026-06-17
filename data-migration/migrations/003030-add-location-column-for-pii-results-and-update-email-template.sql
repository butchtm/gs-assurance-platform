ALTER TABLE gs.document_probable_pii ADD "location" varchar NULL;

CREATE OR REPLACE VIEW gs.v_grouped_document_pii
AS SELECT row_number() OVER () AS id,
    document_id,
    entity,
    probability,
    page_number,
    count(*) AS count,
    location
   FROM gs.document_probable_pii
  GROUP BY document_id, entity, probability, page_number, location;

-- NEW NOTIFICATION TEMPLATE
INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('DOCUMENT_PII_FOUND', 'v0.0.3', 'Action Required: Possible PII Detected in Uploaded Document', '<div style="font-family: Arial,sans-serif;">
  <p>Dear ${recipient},</p>
  <p>
  Following a review by the ARM Team, one or more documents previously uploaded to the platform have been identified as containing Personally Identifiable Information (PII) that is unsuitable for public release.
  </p>
  <p>
  Depending on the outcome of the review, the document(s) will have been either:
  </p>
  <p>
    <ol>
      <li>Removed from the platform – where the PII was deemed unnecessary for the project and request.</li>
      <li>Flagged as unsuitable for public release – where the document can remain on the platform but cannot be made public.</li>
    </ol>
  </p>
  <p>
  The affected documents have been provided under the body of this email.
  </p>
  <p><strong>Next steps:</strong><br></br>
  </p>
  <p>
    <ol>
      <li>If the document was removed: The original uploader must review the content, redact any PII, and reupload a redacted version to the platform.</li>
      <li>If the document was flagged: The uploader should assess whether the document is intended for public access. If so, they must redact the PII and reupload a revised version. If not, no further action is required.</li>
    </ol>
  </p>
  <p>
  <p>
  Please ensure that any reuploaded documents are fully compliant with the General Data Protection Regulation (GDPR).
  </p>
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

<#list multiDocumentPii![] as documentPii>
  <hr style="border: 1px solid #cccccc;"/>
  <p><strong>Document</strong>: ${documentPii.documentName}</p>
  <table style="border: 1px solid #cccccc; padding: 6px; border-collapse: collapse;">
    <tr>
      <th style="border: 1px solid #cccccc; padding: 6px">Entity</th>
      <th style="border: 1px solid #cccccc; padding: 6px">Probability</th>
      <#if documentPii.probablePii[0].pageNumber??>
        <th style="border: 1px solid #cccccc; padding: 6px">Page Number</th>
      </#if>
      <#if documentPii.probablePii[0].location??>
        <th style="border: 1px solid #cccccc; padding: 6px">Location</th>
      </#if>
      <th style="border: 1px solid #cccccc; padding: 6px">No. Occurrences</th>
    </tr>
    <#list documentPii.probablePii![] as probablePii>
      <tr>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.entity!}</td>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.probability!}</td>
        <#if probablePii.pageNumber??>
          <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.pageNumber!}</td>
        </#if>
        <#if probablePii.location??>
          <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.location!}</td>
        </#if>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.occurrences!}</td>
      </tr>
    </#list>
  </table>
  <br/>
</#list>
<p>
As part of our commitment to data privacy and compliance with the <strong>General Data Protection Regulation (GDPR)</strong>, we automatically scan uploaded documents to identify and protect sensitive information. PII includes data such as names, addresses, identification numbers, and other information that could be used to identify an individual.
</p>
<p>
<strong>Why this matters:</strong><br></br>
Under GDPR, organizations must ensure that personal data is only processed when necessary and lawful. Including unnecessary PII in documents can pose privacy risks and may lead to regulatory consequences.
</p>
</div>', 'Dear ${recipient},

Following a review by the ARM Team, one or more documents previously uploaded to the platform have been identified as containing Personally Identifiable Information (PII) that is unsuitable for public release.

Depending on the outcome of the review, the document(s) will have been either:

1. Removed from the platform – where the PII was deemed unnecessary for the project and request.
2. Flagged as unsuitable for public release – where the document can remain on the platform but cannot be made public.

The affected documents have been provided under the body of this email.

Next Steps:

1. If the document was removed: The original uploader must review the content, redact any PII, and reupload a redacted version to the platform.
2. If the document was flagged: The uploader should assess whether the document is intended for public access. If so, they must redact the PII and reupload a revised version. If not, no further action is required.

Please ensure that any reuploaded documents are fully compliant with the General Data Protection Regulation (GDPR).
Best regards,

The Gold Standard Foundation

Request: ${request.name}
GSID: ${project.gsid}
Project: ${project.name}
<#list multiDocumentPii![] as documentPii>
Document name: ${documentPii.documentName}

Entity\tProbability\t<#if documentPii.probablePii[0].pageNumber??>Page Number\t</#if><#if documentPii.probablePii[0].location??>Location\t</#if>No. Occurrences
<#list documentPii.probablePii![] as probablePii>
  ${probablePii.entity!}\t${probablePii.probability!}\t<#if probablePii.pageNumber??>${probablePii.pageNumber!}\t</#if><#if probablePii.location??>${probablePii.location!}\t</#if>${probablePii.occurrences!}
</#list>
</#list>
As part of our commitment to data privacy and compliance with the General Data Protection Regulation (GDPR), we automatically scan uploaded documents to identify and protect sensitive information. PII includes data such as names, addresses, identification numbers, and other information that could be used to identify an individual.

Why this matters:
Under GDPR, organizations must ensure that personal data is only processed when necessary and lawful. Including unnecessary PII in documents can pose privacy risks and may lead to regulatory consequences.

', '2025-10-09 11:53:00.000');
