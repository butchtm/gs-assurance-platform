--- Update Email to include deadlines ---
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'COMPLETENESS_CHECK_FINDINGS',
    'v0.0.4',
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
    For any questions, please contact help@goldstandard.org with your project GSID reference.
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

For details and next steps, please refer to the email sent by the Assurance specialist, sent to the email address associated to the project.

For any questions on the process, please contact help@goldstandard.org with your project GSID reference.

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
SET assignee_template_version = 'v0.0.4'
WHERE assignee_template_name IN ('COMPLETENESS_CHECK_FINDINGS');