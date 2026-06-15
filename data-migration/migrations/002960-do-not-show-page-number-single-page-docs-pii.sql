-- Auto-generated SQL script #202508200531
UPDATE gs.notification_template
	SET html_body='<div style="font-family: Arial,sans-serif;">
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
      <#if documentPii.probablePii[0].pageNumber??>
        <th style="border: 1px solid #cccccc; padding: 6px">Page Number</th>
      </#if>
      <th style="border: 1px solid #cccccc; padding: 6px">Occurrences</th>
    </tr>
    <#list documentPii.probablePii![] as probablePii>
      <tr>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.entity!}</td>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.probability!}</td>
        <#if probablePii.pageNumber??>
          <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.pageNumber!}</td>
        </#if>
        <td style="border: 1px solid #cccccc; padding: 6px">${probablePii.occurrences!}</td>
      </tr>
    </#list>
  </table>
</div>',text_body='Dear ${recipient},

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

Entity\tProbability\t<#if documentPii.probablePii[0].pageNumber??>Page Number\t</#if>Occurrences

<#list documentPii.probablePii![] as probablePii>
  ${probablePii.entity!}\t${probablePii.probability!}\t<#if probablePii.pageNumber??>${probablePii.pageNumber!}\t</#if>${probablePii.occurrences!}
</#list>'
	WHERE id=32;
