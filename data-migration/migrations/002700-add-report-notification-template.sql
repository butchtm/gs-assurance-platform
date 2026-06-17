INSERT INTO gs.notification_template (
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REPORT_GENERATED',
    'v0.0.1',
    'GS Assurance Platform Report Available',
    '<div style="font-family: Arial,sans-serif;">
     <p>Dear ${recipient},</p>
     <p>
     Please download your requested report <a href="${download.presignedUrl}">here</a> before it expires on ${download.expiryDate} at ${download.expiryTime}.
     </p>
     <p>
     Should you have any issues downloading your report, please contact help@goldstandard.org referencing report key ${download.key}.
     </p>
     <p>
     Best regards,
     </p>
     <p>
     The Gold Standard Foundation
     </p>
     </div>',
    'Dear ${recipient},

     Please download your requested report here: ${download.presignedUrl} before it expires on ${download.expiryDate} at ${download.expiryTime}.

     Should you have any issues downloading your report, please contact help@goldstandard.org referencing report key ${download.key}.

     Best regards,

     The Gold Standard Foundation',
     NOW()
 );