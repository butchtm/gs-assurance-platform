--- Update Email to include deadlines ---
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'COMPLETENESS_CHECK_FINDINGS',
    'v0.0.3',
    '${request.name} for ${project.gsid} has not passed the completeness check stage',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    The following request has not passed the completeness check stage. Please submit the missing information/documents within 2 weeks of this notification. 
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

The following request has not passed the completeness check stage. Please submit the missing information/documents within 2 weeks of this notification. 

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
    'REVIEW_FEEDBACK_UPLOADED',
    'v0.0.3',
    'Feedback uploaded for ${project.gsid} ${request.name}',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    We have uploaded feedback on the following request.<br>
    Please review the findings and submit your responses and the revised documentation on the GSIQ platform within 6 weeks of this notification.
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
Please review the findings and submit your responses and the revised documentation on the GSIQ platform within 6 weeks of this notification.

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


-- Set New Versions in the Mapping Table --

UPDATE gs.request_status_assignee_notification
SET assignee_template_version = 'v0.0.3'
WHERE assignee_template_name IN ('COMPLETENESS_CHECK_FINDINGS', 'REVIEW_FEEDBACK_UPLOADED');