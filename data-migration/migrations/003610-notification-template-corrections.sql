-- COMPLETENESS_CHECK_FINDINGS
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'COMPLETENESS_CHECK_FINDINGS',
    'v0.0.6',
    '${request.name} for ${project.gsid} has not passed the completeness check stage',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    Your request has not passed the completeness check. Please submit the missing information/documents within 2 weeks of this notification.
    </p>
    <p>
    The review process will only begin once all required documents are submitted.
    </p>
    <p>
    For details and next steps, please refer to the email sent by the Assurance specialist, sent to the email address associated to the project.
    </p>
    <p>
    For detailed information and subsequent steps, please refer to the message issued in the conversation section of the GS Assurance Platform associated with this project.
    </p>
    <p>
    Should you have any questions, please submit your inquiries via the conversation section of the GS Assurance Platform, which will remain available until the review request is closed.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <ul style={{ listStyle: ''none'', padding: 0 }}>
	<table>
		<tr>
			<td><strong>Project:</strong></td>
			<td>${project.name}</td>
		</tr>
		<tr>
			<td><strong>GSID:</strong></td>
			<td>${project.gsid}</td>
		</tr>
		<tr>
			<td><strong>Request:</strong></td>
			<td>${request.name}</td>
		</tr>
		<tr>
			<td><strong>Project Type:</strong></td>
			<td>${project.projectTypes}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

Your request has not passed the completeness check. Please submit the missing information/documents within 2 weeks of this notification.

The review process will only begin once all required documents are submitted.

For detailed information and subsequent steps, please refer to the message issued in the conversation section of the GS Assurance Platform associated with this project.

Should you have any questions, please submit your inquiries via the conversation section of the GS Assurance Platform, which will remain available until the review request is closed.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}
',
    NOW())
;

UPDATE gs.request_status_assignee_notification
SET assignee_template_version = 'v0.0.6'
WHERE assignee_template_name = 'COMPLETENESS_CHECK_FINDINGS';

UPDATE gs.request_status_notification
SET request_owner_template_version = 'v0.0.6'
WHERE request_owner_template_name = 'COMPLETENESS_CHECK_FINDINGS';


-- REVIEW_FEEDBACK_UPLOADED
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_FEEDBACK_UPLOADED',
    'v0.0.5',
    'Feedback uploaded for ${project.gsid} ${request.name}',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    We have uploaded feedback on the following request.
    </p>
    <p>
    A message will be issued in the conversation sector confirming the affected documents and the steps required to resolve this action.
    </p>
    <p>
    Please review the findings and submit your responses and the revised documentation on the GS Assurance Platform within 6 weeks of this notification.
    </p>
    <p>
    The next round of review for the request will begin once the Validation/Verification Body submits a response.
    </p>
    <p>
    If no VVB is involved, the Project Developer shall submit the updated request directly.
    </p>
    <p>
    Should you have any questions on the process, please contact <a href="mailto:help@goldstandard.org">help@goldstandard.org</a> with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <ul style={{ listStyle: ''none'', padding: 0 }}>
	<table>
		<tr>
			<td><strong>Project:</strong></td>
			<td>${project.name}</td>
		</tr>
		<tr>
			<td><strong>GSID:</strong></td>
			<td>${project.gsid}</td>
		</tr>
		<tr>
			<td><strong>Request:</strong></td>
			<td>${request.name}</td>
		</tr>
		<tr>
			<td><strong>Project Type:</strong></td>
			<td>${project.projectTypes}</td>
		</tr>
		<tr>
            <td><strong>Feedback Round:</strong></td>
            <td>${request.feedbackRound!}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

We have uploaded feedback on the following request.

A message will be issued in the conversation sector confirming the affected documents and the steps required to resolve this action.

Please review the findings and submit your responses and the revised documentation on the GS Assurance Platform within 6 weeks of this notification.

The next round of review for the request will begin once the Validation/Verification Body submits a response.

If no VVB is involved, the Project Developer shall submit the updated request directly.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round:${request.feedbackRound!}',
    NOW())
;
UPDATE gs.request_status_notification SET request_owner_template_version = 'v0.0.5' WHERE request_owner_template_name = 'REVIEW_FEEDBACK_UPLOADED';
UPDATE gs.request_status_assignee_notification SET assignee_template_version = 'v0.0.5' WHERE assignee_template_name = 'REVIEW_FEEDBACK_UPLOADED';


-- REQUEST_CLOSED
INSERT INTO gs.notification_template (
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REQUEST_CLOSED',
    'v0.0.4',
    '${project.gsid} ${request.name} approved',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    Congratulations! We have approved the following request.
    </p>
    <p>
    We will now make the public versions of obligatory documents available on the <a href="https://registry.goldstandard.org/">Gold Standard Registry</a> and update the registry information according to the results of the request.
    </p>
    <p>
    Please review the project and request information on the GS Assurance Platform and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Should any credits need to be issued, GSF will generate the issuance request and issue the corresponding invoice to proceed issuance of credits.
    </p>
    <p>
    Should you have any questions on the process, please contact <a href="mailto:help@goldstandard.org">help@goldstandard.org</a> with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <ul style={{ listStyle: ''none'', padding: 0 }}>
	<table>
		<tr>
			<td><strong>Project:</strong></td>
			<td>${project.name}</td>
		</tr>
		<tr>
			<td><strong>GSID:</strong></td>
			<td>${project.gsid}</td>
		</tr>
		<tr>
			<td><strong>Request:</strong></td>
			<td>${request.name}</td>
		</tr>
		<tr>
			<td><strong>Project Type:</strong></td>
			<td>${project.projectTypes}</td>
		</tr>
		<tr>
			<td><strong>Project Status:</strong></td>
			<td>${project.projectStatus}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

Congratulations! We have approved the following request.

We will now make the public versions of obligatory documents available on the Gold Standard Registry, https://registry.goldstandard.org/, and update the registry information according to the results of the request.

Please review the project and request information on the GS Assurance Platform and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.

Should any credits need to be issued, GSF will generate the issuance request and issue the corresponding invoice to proceed issuance of credits.

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

UPDATE gs.request_status_assignee_notification
SET assignee_template_version = 'v0.0.4'
WHERE assignee_template_name = 'REQUEST_CLOSED';

UPDATE gs.request_status_notification
SET request_owner_template_version = 'v0.0.4'
WHERE request_owner_template_name = 'REQUEST_CLOSED';

UPDATE gs.request_status_notification
SET registry_team_template_version = 'v0.0.4'
WHERE registry_team_template_name = 'REQUEST_CLOSED';

-- REVIEW_ASSIGNED
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_ASSIGNED',
    'v0.0.5',
    '${request.name} received for ${project.gsid}',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    You have been assigned to review the following projects request. Please log-in to your account to access project documents.
    </p>
    <p>
    Please upload the findings documents on the GS Assurance Platform. These findings should be submitted by ${request.latestRequestActivity.reviewDueDate!}.
    </p>
    <p>
    Please remember to complete the VVB assessment form after conducting your review. You can find the links to the specific project type forms here:
    <li><a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45URDlRU1hYTTIyWDRPWEtNTVYwMElERFZRUS4u">Project Activity</a>
    <li><a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45UNlNZNlJHSE0wMEpVSUdVSVpCWERUUlVCNS4u">PoA/VPA</a>
    </p>
    <p>
    Should you have any questions on the process, please contact the QA/QC officer assigned or <a href="mailto:FUNC_ARM@goldstandard.org">FUNC_ARM@goldstandard.org</a> with reference to the project GSID.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <ul style={{ listStyle: ''none'', padding: 0 }}>
	<table>
		<tr>
			<td><strong>Project:</strong></td>
			<td>${project.name}</td>
		</tr>
		<tr>
			<td><strong>GSID:</strong></td>
			<td>${project.gsid}</td>
		</tr>
		<tr>
			<td><strong>Request:</strong></td>
			<td>${request.name}</td>
		</tr>
		<tr>
			<td><strong>Project Type:</strong></td>
			<td>${project.projectTypes}</td>
		</tr>
		<tr>
      <td><strong>Feedback Round:</strong></td>
      <td>${request.feedbackRound!}</td>
		</tr>
		<tr>
      <td><strong>Review Due Date:</strong></td>
      <td>${request.latestRequestActivity.reviewDueDate!}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

You have been assigned to review the following projects request. Please log-in to your account to access project documents.

Please upload the findings documents on the GS Assurance Platform. These findings should be submitted by ${request.latestRequestActivity.reviewDueDate!}.

Please remember to complete the VVB assessment form after conducting your review. You can find the links to the specific project type forms here:
Project Activity (https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45URDlRU1hYTTIyWDRPWEtNTVYwMElERFZRUS4u)
POA/VPA (https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45UNlNZNlJHSE0wMEpVSUdVSVpCWERUUlVCNS4u)

Should you have any questions on the process, please contact the QA/QC officer assigned or FUNC_ARM@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round: ${request.feedbackRound!}

Review Due Date: ${request.latestRequestActivity.reviewDueDate!}
',
    NOW())
;
UPDATE gs.assignment_status_notification SET template_id = (SELECT id FROM gs.notification_template WHERE name = 'REVIEW_ASSIGNED' AND version = 'v0.0.5') WHERE id = 0;


