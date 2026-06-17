INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('REQUEST_MESSAGE_RECEIVED', 'v0.0.1', 'New message received for request', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    A new message has been received related to the below request. Please log-in to your account to view the message under the relevant request section.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <p><strong>Project:</strong> ${project.name}</p>
    <p><strong>GSID:</strong> ${project.gsid}</p>
    <p><strong>Request:</strong> ${request.name}</p>
    <p><strong>Project Type:</strong> ${project.projectTypes}</p>
    <p><strong>Project Status:</strong> ${project.projectStatus}</p>
</div>', 'Dear ${recipient},

A new message has been received related to the below request. Please log-in to your account to view the message under the relevant request section.

Best regards,

The Gold Standard Foundation

Project: ${project.name}
GSID: ${project.gsid}
Request: ${request.name}
Project Type: ${project.projectTypes}
Project Status: ${project.projectStatus}', '2025-11-27 00:00:00.000');

