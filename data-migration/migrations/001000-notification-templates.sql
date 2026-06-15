DELETE FROM gs.notification_template;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REQUEST_RECEIVED',
    'v0.0.1',
    'Request ${request.name} received for ${project.gsid}',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    We acknowledge the following request has been received. We will now conduct a completeness check. We will let you know if any information is missing within 10-15 working days.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

We acknowledge the following request has been received. We will now conduct a completeness check. We will let you know if any information is missing within 10-15 working days.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}
',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'COMPLETENESS_CHECK_FINDINGS',
    'v0.0.1',
    'Request ${request.name} for ${project.gsid} has not passed the completeness check stage',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    The following request has not passed the completeness check stage.
    </p>
    <p>
    The review will not be initiated until all required documents are submitted.
    </p>
    <p>
    You can find a <a href="${required_documents_url!}">full list of required documents per request type here.</a>
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    </ul>
</div>', 
    'Dear ${recipient},

The following request has not passed the completeness check stage.

The review will not be initiated until all required documents are submitted.

You can find a full list of required documents per request type here, ${required_documents_url!}

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}
',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_INITIATED',
    'v0.0.1',
    'Review initiated for ${project.gsid} ${request.name}',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    Thank you for your submission. The following review has been initiated.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    <li>
        <strong>Feedback Round:</strong> ${request.feedbackRound!}
    </li>
    <li>
        <strong>Review Due Date:</strong> ${request.dueDate!}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

Thank you for your submission. The following review has been initiated.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round: ${request.feedbackRound!}

Review Due Date: ${request.dueDate!}
',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'YOUR_TURN',
    'v0.0.1',
    'Request ${project.gsid} ${request.name} ready for you to work on',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    The following request has had a status change and is now ready for you to work on it.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    <li>
        <strong>Feedback Round:</strong> ${request.feedbackRound!}
    </li>
    <li>
        <strong>Review Due Date:</strong> ${request.dueDate!}
    </li>
    <li>
        <strong>Request Change Log:</strong> ${request.changeLog!}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

The following request has had a status change and is now ready for you to work on it.


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round: ${request.feedbackRound!}

Review Due Date: ${request.dueDate!}

Request Change Log: ${request.changeLog!}
',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_FEEDBACK_UPLOADED',
    'v0.0.1',
    'Feedback uploaded for ${project.gsid} ${request.name}',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    We have uploaded feedback on the following request.<br>
    Please review the findings and submit your responses and the revised documentation on the GSIQ platform.
    </p>
    <p>
    The next round of review for the request will begin once the Validation/Verification Body submits a response.
    </p>
    <p>
    If no VVB is involved, the Project Developer shall submit the updated request directly.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    <li>
        <strong>Feedback Round:</strong> ${request.feedbackRound!}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

We have uploaded feedback on the following request.
Please review the findings and submit your responses and the revised documentation on the GSIQ platform.

The next round of review for the request will begin once the Validation/Verification Body submits a response.

If no VVB is involved, the Project Developer shall submit the updated request directly.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round:${request.feedbackRound!}',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'INVOICE_NOTIFICATION',
    'v0.0.1',
    'Invoice issued for ${project.gsid} ${request.name}',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    We have issued an invoice for the below request.
    </p>
    <p>
    We would appreciate it if you could ensure the invoice is paid as soon as possible, as the review will not be closed before the invoice is paid.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

We have issued an invoice for the below request.

We would appreciate it if you could ensure the invoice is paid as soon as possible, as the review will not be closed before the invoice is paid.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}
',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REQUEST_CLOSED',
    'v0.0.1',
    'Request ${project.gsid} ${request.name} approved',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    Congratulations! We have approved the following request.
    </p>
    <p>
    We will now make the public versions of obligatory documents available on the <a href="https://registry.goldstandard.org/">Gold Standard Registry</a> and update the registry information according to the results of the request. 
    </p>
    <p>
    Please review the project and request information on GSIQ and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Should any credits need to be issued on the basis of this request, the PD can now file an Issuance Request.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    <li>
        <strong>Project Status:</strong> ${project.projectStatus}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

Congratulations! We have approved the following request.

We will now make the public versions of obligatory documents available on the Gold Standard Registry, https://registry.goldstandard.org/, and update the registry information according to the results of the request. 

Please review the project and request information on GSIQ and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.

Should any credits need to be issued on the basis of this request, the PD can now file an Issuance Request.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Project Status: ${project.projectStatus}',
    NOW())
;


INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'VVB_ASSIGNMENT',
    'v0.0.1',
    'Project Developer has assigned you to ${project.gsid} ${request.name}',
    '<div style={{ fontFamily: ''Arial, sans-serif'' }}>
    <h1>Dear ${recipient},</h1>
    <p>
    Congratulations! A project developer has assigned you as the VVB of the following project’s request. Please enter in contact with the PD to arrange for the audit to be conducted.
    </p>
    <p>
    Please submit the audit report and all updated supporting documents on <a href="${request.link!}">GSIQ</a>.
    </p>
    <p>
    Note that the project developer will not be able to submit the request from their own account.
    </p>
    <p>
    The review will commence after a successful completeness check.
    </p>
    <p>
    Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style={{ border: ''1px solid #ccc'' }} />
    <ul style={{ listStyle: ''none'', padding: 0 }}>
    <li>
        <strong>Project:</strong> ${project.name}
    </li>
    <li>
        <strong>GSID:</strong> ${project.gsid}
    </li>
    <li>
        <strong>Request:</strong> ${request.name}
    </li>
    <li>
        <strong>Project Type:</strong> ${project.projectTypes}
    </li>
    <li>
        <strong>Project Status:</strong> ${project.projectStatus}
    </li>
    </ul>
</div>',
    'Dear ${recipient},

Congratulations! A project developer has assigned you as the VVB of the following project’s request. Please enter in contact with the PD to arrange for the audit to be conducted.

Please submit the audit report and all updated supporting documents on GSIQ, ${request.link!}.

Note that the project developer will not be able to submit the request from their own account.

The review will commence after a successful completeness check.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Project Status: ${project.projectStatus}',
    NOW())
;
