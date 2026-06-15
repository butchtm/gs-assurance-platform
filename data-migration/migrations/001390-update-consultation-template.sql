--
-- fix notification template for new consultation
--
UPDATE gs.notification_template
SET text_body =
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

Review Start Date: ${consultation.reviewStartDate}

Review End Date: ${consultation.reviewEndDate}

Project Size: ${project.projectSize}

Project Stream: ${project.projectStream}

GS Version: ${project.gsVersion}

Country: ${project.country}
'
WHERE name = 'CONSULTATION_STARTED';
