--Update CONSULTATION_STARTED template to v0.0.4--
INSERT INTO gs.notification_template (
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'CONSULTATION_STARTED',
    'v0.0.4',
    'Request ${request.name} consultation period started in ${project.gsid}',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    We are pleased to inform you that the following reviews have been initiated.
    </p>
    <p>
    If you want to raise requests for clarification or corrective action, please send these to help@goldstandard.org at your convenience.
    A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
    </p>
    <p>
    The project documentation is available on Assurance Platform. Please log-in to the platform to access the documents and provide your comments.
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
            <td><strong>Consultation Start Date:</strong></td>
            <td>${consultation.reviewStartDate}</td>
        </tr>
		<tr>
			<td><strong>Consultation End Date:</strong></td>
			<td>${consultation.reviewEndDate}</td>
		</tr>
		<tr>
			<td><strong>Project Size:</strong></td>
			<td>${project.projectSize}</td>
		</tr>
		<tr>
			<td><strong>Project Stream:</strong></td>
			<td>${project.projectStream}</td>
		</tr>
        <tr>
            <td><strong>GS Version:</strong></td>
            <td>${project.gsVersion}</td>
        </tr>
		<tr>
			<td><strong>Country:</strong></td>
			<td>${project.country}</td>
		</tr>
    </table>
</div>',
'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.
If you want to raise requests for clarification or corrective action, please send these to help@goldstandard.org at your convenience.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform. Please log-in to the platform to access the documents and provide your comments.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Consultation Start Date: ${consultation.reviewStartDate}

Consultation End Date: ${consultation.reviewEndDate}

Project Size: ${project.projectSize}

Project Stream: ${project.projectStream}

GS Version: ${project.gsVersion}

Country: ${project.country}
',
    NOW())
;
