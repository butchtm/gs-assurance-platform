ALTER TABLE gs.stakeholder_consultation ADD notification_sent bool DEFAULT false NOT NULL;

INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('CONSULTATION_STARTED', 'v0.0.5', 'Consultation period has started for requests', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
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
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.name}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsid}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px"><a href="https://assurance-platform.goldstandard.org/public-consultations/request/${consultation.request.id}">${consultation.request.name}</a></td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewStartDate}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewEndDate}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectSize}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectStream}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsVersion}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.country}</td>
    		</tr>
    	</#list>
  	</table>
</div>', 'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.
If you want to raise requests for clarification or corrective action, please send these to help@goldstandard.org at your convenience.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform. Please log-in to the platform to access the documents and provide your comments.

Best regards,

The Gold Standard Foundation

Project\tGSID\tRequest\tProject Type\tConsultation Start Date\tConsultation End Date\tProject Size\tProject Stream\tGS Version\tCountry
<#list consultations![] as consultation>
  ${consultation.project.name!}\t${consultation.project.gsid!}\t${consultation.request.name!}\t${consultation.reviewStartDate!}\t${consultation.reviewEndDate!}\t${consultation.project.projectSize!}\t${consultation.project.projectStream!}\t${consultation.project.gsVersion!}\t${consultation.project.country!}
</#list>', '2025-10-06 08:56:00.000');
INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('NGO_CONSULTATION_NOTIFICATION', 'v0.0.2', 'Request status changes to ‘In_Assignment’ and request type requires stakeholder consultation.', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    We are pleased to inform you that the following reviews have been initiated.
    </p>
    <p>
    If you want to raise requests for clarification or corrective action, please send these to FUNC_ARM@goldstandard.org at your convenience.
    A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
    </p>
    <p>
	You can view more information about the project and submit any comments from its public consultation page.
    </p>
    <p>
    Best regards,
    </p>
    <p>
    The Gold Standard Foundation
    </p>
    <hr style="border: 1px solid #cccccc;"/>
    <ul style={{ listStyle: ''none'', padding: 0 }}>

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
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.name}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsid}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px"><a href="https://assurance-platform.goldstandard.org/public-consultations/request/${consultation.request.id}">${consultation.request.name}</a></td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewStartDate}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.reviewEndDate}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectSize}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.projectStream}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.gsVersion}</td>
    		<td style="border: 1px solid #cccccc; padding: 6px">${consultation.project.country}</td>
    		</tr>
    	</#list>
  	</table>
  	<br/>
</div>', 'Dear ${recipient},

We are pleased to inform you that the following reviews have been initiated.

If you want to raise requests for clarification or corrective action, please send these to FUNC_ARM@goldstandard.org at your convenience.
A synthesis of all comments will be prepared and will be sent to the project proponents shortly before the end of the review period.
The project documentation is available on Assurance Platform. Please log-in to the platform to access the documents and provide your comments.

You can view more information about the project and submit any comments from its public consultation page.

Best regards,

The Gold Standard Foundation

Project\tGSID\tRequest\tProject Type\tConsultation Start Date\tConsultation End Date\tProject Size\tProject Stream\tGS Version\tCountry
<#list consultations![] as consultation>
  ${consultation.project.name!}\t${consultation.project.gsid!}\t${consultation.request.name!}\t${consultation.reviewStartDate!}\t${consultation.reviewEndDate!}\t${consultation.project.projectSize!}\t${consultation.project.projectStream!}\t${consultation.project.gsVersion!}\t${consultation.project.country!}
</#list>', '2025-10-06 08:48:00.000');