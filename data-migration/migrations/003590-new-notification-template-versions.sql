-- COMPLETENESS_CHECK_FINDINGS v0.0.5
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'COMPLETENESS_CHECK_FINDINGS',
    'v0.0.5',
    '${request.name} for ${project.gsid} has not passed the completeness check stage',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
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
    For detailed information and subsequent steps, please refer to the message issued in the conversation section of the GS Assurance Platform associated with this request. Should you have any questions, please submit your enquiries via the conversation section of the GS Assurance Platform, which will remain available until the request is closed.
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

For detailed information and subsequent steps, please refer to the message issued in the conversation section of the GS Assurance Platform associated with this request. Should you have any questions, please submit your enquiries via the conversation section of the GS Assurance Platform, which will remain available until the request is closed.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}
',
    NOW())
;

UPDATE gs.request_status_notification SET request_owner_template_version = 'v0.0.5' WHERE request_owner_template_name = 'COMPLETENESS_CHECK_FINDINGS';
UPDATE gs.request_status_assignee_notification SET assignee_template_version = 'v0.0.5' WHERE assignee_template_name = 'COMPLETENESS_CHECK_FINDINGS';

-- REVIEW_FEEDBACK_UPLOADED v0.0.4
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_FEEDBACK_UPLOADED',
    'v0.0.4',
    'Feedback uploaded for ${project.gsid} ${request.name}',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    We have uploaded feedback on the following request.<br>
    Please review the findings and submit your responses and the revised documentation on the GS Assurance Platform within 6 weeks of this notification.
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
Please review the findings and submit your responses and the revised documentation on the GS Assurance Platform within 6 weeks of this notification.

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
UPDATE gs.request_status_notification SET request_owner_template_version = 'v0.0.4' WHERE request_owner_template_name = 'REVIEW_FEEDBACK_UPLOADED';
UPDATE gs.request_status_assignee_notification SET assignee_template_version = 'v0.0.4' WHERE assignee_template_name = 'REVIEW_FEEDBACK_UPLOADED';


-- REVIEW_ASSIGNED v0.0.4
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_ASSIGNED',
    'v0.0.4',
    '${request.name} received for ${project.gsid}',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    You have been assigned to review the following projects request. Please log-in to your account to access project documents.
    </p>
    <p>
    Please upload the findings documents on GS Assurance Platform. These findings should be submitted by ${request.latestRequestActivity.reviewDueDate!}.
    </p>
    <p>
    Please remember to complete the VVB assessment form after conducting your review. You can find the links to the specific project type forms here:
    <li><a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45URDlRU1hYTTIyWDRPWEtNTVYwMElERFZRUS4u">Project Activity</a>
    <li><a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45UNlNZNlJHSE0wMEpVSUdVSVpCWERUUlVCNS4u">PoA/VPA</a>
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

Please upload the findings documents on GS Assurance Platform. These findings should be submitted by ${request.latestRequestActivity.reviewDueDate!}.

Please remember to complete the VVB assessment form after conducting your review. You can find the links to the specific project type forms here:
<a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45URDlRU1hYTTIyWDRPWEtNTVYwMElERFZRUS4u">Project Activity</a>
<a href="https://forms.office.com/Pages/ResponsePage.aspx?id=OyQmOE9TRUqVORF62FHlSuHr-zBU2FFIk_VP12E9R45UNlNZNlJHSE0wMEpVSUdVSVpCWERUUlVCNS4u">PoA/VPA</a>
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
UPDATE gs.assignment_status_notification SET template_id = (SELECT id FROM gs.notification_template WHERE name = 'REVIEW_ASSIGNED' AND version = 'v0.0.4') WHERE id = 0;

-- CONSULTATION_STARTED v0.0.6
INSERT INTO gs.notification_template (name, version, subject, html_body, text_body, created_timestamp) VALUES('CONSULTATION_STARTED', 'v0.0.6', 'Consultation period has started for requests', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    We are pleased to inform you that the following reviews have been initiated.
    </p>
    <p>
    If you want to raise requests for clarification or corrective action, please use the `Add Comment` button on the <a href="${webUiHostname}/public-consultations">Public Consultations</a> page.
    A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
    </p>
    <p>
    The project documentation is available on Assurance Platform <a href="${webUiHostname}/public-consultations">Public Consultations</a> page. Please access the documents and provide your comments at your earliest convenience.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
	<table style="border: 1px solid #cccccc; padding: 6px; border-collapse: collapse;">
    	<tr>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">GSID</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Request</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Consultation Start Date</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Consultation End Date</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project Size</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project Stream</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">GS Version</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Country</th>
    	</tr>
    	<#list consultations![] as consultation>
    		<tr>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.name!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsid!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.request.name!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewStartDate!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewEndDate!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectSize!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectStream!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsVersion!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.country!}</td>
    		</tr>
    	</#list>
  	</table>
</div>', 'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.
If you want to raise requests for clarification or corrective action, please use the `Add Comment` button on the <a href="${webUiHostname}/public-consultations">Public Consultations</a> page.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform <a href="${webUiHostname}/public-consultations">Public Consultations</a> page. Please access the documents and provide your comments at your earliest convenience.

Best regards,

The Gold Standard Foundation

Project\tGSID\tRequest\tProject Type\tConsultation Start Date\tConsultation End Date\tProject Size\tProject Stream\tGS Version\tCountry
<#list consultations![] as consultation>
  ${consultation.project.name!}\t${consultation.project.gsid!}\t${consultation.request.name!}\t${consultation.reviewStartDate!}\t${consultation.reviewEndDate!}\t${consultation.project.projectSize!}\t${consultation.project.projectStream!}\t${consultation.project.gsVersion!}\t${consultation.project.country!}
</#list>', NOW());


-- NGO_CONSULTATION_NOTIFICATION v0.0.3
INSERT INTO gs.notification_template (name, version, subject, html_body, text_body, created_timestamp) VALUES('NGO_CONSULTATION_NOTIFICATION', 'v0.0.3', 'Request status changes to ''In_Assignment'' and request type requires stakeholder consultation.', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    We are pleased to inform you that the following reviews have been initiated.
    </p>
    <p>
    If you want to raise requests for clarification or corrective action, please use the `Add Comment` button on the <a href="${webUiHostname}/public-consultations">Public Consultations</a> page.
    A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
    </p>
    <p>
    The project documentation is available on Assurance Platform <a href="${webUiHostname}/public-consultations">Public Consultations</a> page. Please access the documents and provide your comments at your earliest convenience.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
  	<table style="border: 1px solid #cccccc; padding: 6px; border-collapse: collapse;">
    	<tr>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">GSID</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Request</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Consultation Start Date</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Consultation End Date</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project Size</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Project Stream</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">GS Version</th>
    		<th style="border: 1px solid #cccccc; padding: 6px">Country</th>
    	</tr>
    	<#list consultations![] as consultation>
    		<tr>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.name!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsid!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px"><a href="${webUiHostname}/public-consultations/request/${consultation.request.id?c}">${consultation.request.name!}</a></td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewStartDate!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewEndDate!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectSize!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectStream!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsVersion!}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.country!}</td>
    		</tr>
    	</#list>
  	</table>
  	<br/>
</div>', 'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.
If you want to raise requests for clarification or corrective action, please use the `Add Comment` button on the <a href="${webUiHostname}/public-consultations">Public Consultations</a> page.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform <a href="${webUiHostname}/public-consultations">Public Consultations</a> page. Please access the documents and provide your comments at your earliest convenience.

You can view more information about the project and submit any comments from its public consultation page.

Best regards,

The Gold Standard Foundation

Project\tGSID\tRequest\tProject Type\tConsultation Start Date\tConsultation End Date\tProject Size\tProject Stream\tGS Version\tCountry
<#list consultations![] as consultation>
  ${consultation.project.name!}\t${consultation.project.gsid!}\t${consultation.request.name!}\t${consultation.reviewStartDate!}\t${consultation.reviewEndDate!}\t${consultation.project.projectSize!}\t${consultation.project.projectStream!}\t${consultation.project.gsVersion!}\t${consultation.project.country!}
</#list>', NOW());


-- VVB_ASSIGNED v0.0.3
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'VVB_ASSIGNED',
    'v0.0.3',
    'Project Developer has assigned you to ${project.gsid} ${request.name}',
    '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    Congratulations! A project developer has assigned you as the VVB of the following projects request. Please enter in contact with the PD to arrange for the audit to be conducted.
    </p>
    <p>
    Please submit the audit report and all updated supporting documents on <a href="${request.link!}">GS Assurance Platform</a>.
    </p>
    <p>
    Note that the project developer will not be able to submit the request from their own account.
    </p>
    <p>
    The review will commence after a successful completeness check.
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

Congratulations! A project developer has assigned you as the VVB of the following projects request. Please enter in contact with the PD to arrange for the audit to be conducted.

Please submit the audit report and all updated supporting documents on <a href="${request.link!}">GS Assurance Platform</a>.

Note that the project developer will not be able to submit the request from their own account.

The review will commence after a successful completeness check.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

Best regards,

The Gold Standard Foundation


Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Project Status: ${project.projectStatus}
',
    NOW())
;
UPDATE gs.assignment_status_notification SET template_id = (SELECT id FROM gs.notification_template WHERE name = 'VVB_ASSIGNED' AND version = 'v0.0.3') WHERE id = 1 OR id = 2;


