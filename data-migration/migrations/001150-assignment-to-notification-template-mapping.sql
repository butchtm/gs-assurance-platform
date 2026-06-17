--
-- Adding assignment to notification template table
--
CREATE TABLE gs.assignment_status_notification (
    id int4 NOT NULL,
    assignment_status_id int4 NULL,
    assignment_type_id int4 NULL,
    template_id int4 NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (assignment_status_id, assignment_type_id)
);

ALTER TABLE gs.assignment_status_notification
ADD FOREIGN KEY (assignment_status_id) REFERENCES gs.assignment_status ON DELETE RESTRICT;
ALTER TABLE gs.assignment_status_notification
ADD FOREIGN KEY (assignment_type_id) REFERENCES gs.assignment_type ON DELETE RESTRICT;
ALTER TABLE gs.assignment_status_notification
ADD FOREIGN KEY (template_id) REFERENCES gs.notification_template ON DELETE RESTRICT;


--
-- Add new notification template for when a reviewer is assigned
--
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_ASSIGNED',
    'v0.0.2',
    '${request.name} received for ${project.gsid}',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    You have been assigned to review the following project’s request. Please log-in to your account to access project documents.
    </p>
    <p>
    Please upload the findings documents on GSIQ platform.
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
            <td><strong>Feedback Round:</strong></td>
            <td>${request.feedbackRound!}</td>
		</tr>
		<tr>
            <td><strong>Review Due Date:</strong></td>
            <td>${request.dueDate!}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

You have been assigned to review the following project’s request. Please log-in to your account to access project documents.

Please upload the findings documents on GSIQ platform.

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

--
-- Add new row to link assignment status and type update to a template
--
INSERT INTO gs.assignment_status_notification
  (id, assignment_status_id, assignment_type_id, template_id)
VALUES
   (0, (SELECT id FROM gs.assignment_status WHERE code = 'ASSIGNED'),
   (SELECT id FROM gs.assignment_type WHERE code = 'REVIEW'),
   (SELECT id FROM gs.notification_template WHERE name = 'REVIEW_ASSIGNED' AND version = 'v0.0.2'))
;