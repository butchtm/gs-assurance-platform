alter table gs.notification add recipient_id int4 not null default 0;
alter table gs.notification add recipient_type varchar default 'ACCOUNT' not null;

update gs.notification set recipient_id = account_id;

alter table gs.notification alter column account_id drop not null;

CREATE TABLE gs.ngo (
	id int4 NOT NULL,
	email_address varchar NOT NULL,
	"name" varchar NOT NULL,
	CONSTRAINT ngo_pkey PRIMARY KEY (id)
);

INSERT INTO gs.notification_template
("name", "version", subject, html_body, text_body, created_timestamp)
VALUES('NGO_CONSULTATION_NOTIFICATION', 'v0.0.1', 'Request status changes to ‘In_Assignment’ and request type requires stakeholder consultation.', '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    We are pleased to inform you that the following reviews have been initiated.
    </p>
    <p>
    If you want to raise requests for clarification or corrective action, please send these to FUNC_ARM@goldstandard.org at your convenience.
    A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
    </p>
    <p>
	You can view more information about the project and submit any comments from its <a href="https://assurance-platform.goldstandard.org/public-consultations/request/${request.id}">public consultation page</a>.
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
			<td><strong>Request Details:</strong></td>
		</tr>
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
</div>', 'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.

If you want to raise requests for clarification or corrective action, please send these to FUNC_ARM@goldstandard.org at your convenience.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform. Please log-in to the platform to access the documents and provide your comments.

You can view more information about the project and submit any comments from its public consultation page: https://assurance-platform.goldstandard.org/public-consultations/request/${request.id}.

Best regards,

The Gold Standard Foundation

Request Details:

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
', '2025-04-30 14:33:30.219');