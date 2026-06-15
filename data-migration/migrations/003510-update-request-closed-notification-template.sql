INSERT INTO gs.notification_template (
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REQUEST_CLOSED',
    'v0.0.3',
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
    Please review the project and request information on GSIQ and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.
    </p>
    <p>
    Should any credits need to be issued, GSF will generate the issuance request and issue the corresponding invoice to proceed issuance of credits.
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
		<tr>
			<td><strong>Project Status:</strong></td>
			<td>${project.projectStatus}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

Congratulations! We have approved the following request.

We will now make the public versions of obligatory documents available on the Gold Standard Registry, https://registry.goldstandard.org/, and update the registry information according to the results of the request.

Please review the project and request information on GSIQ and ensure everything is correctly stated. If you see any discrepancies, please contact us at help@goldstandard.org with reference to the project GSID.

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
SET assignee_template_version = 'v0.0.3'
WHERE assignee_template_name = 'REQUEST_CLOSED';

UPDATE gs.request_status_notification
SET request_owner_template_version = 'v0.0.3'
WHERE request_owner_template_name = 'REQUEST_CLOSED';

UPDATE gs.request_status_notification
SET registry_team_template_version = 'v0.0.3'
WHERE registry_team_template_name = 'REQUEST_CLOSED';

