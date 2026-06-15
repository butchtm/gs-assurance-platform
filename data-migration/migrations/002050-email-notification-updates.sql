
INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'YOUR_TURN',
    'v0.0.3',
    '${project.gsid} ${request.name} ready for you to work on',
    '<div style="font-family: Arial,sans-serif;">
      <h1>Dear ${recipient},</h1>
      <p>
        The following request has had a status change and is now ready for you to work on it.
      </p>
      <table>
        <tr>
          <td><strong>Activity:</strong></td>
          <td>${request.latestRequestActivity.activityName}</td>
        </tr>
        <tr>
          <td><strong>Due:</strong></td>
          <td>${request.latestRequestActivity.plannedEndDate}</td>
        </tr>
      </table>
      <p>
        Should you have any questions on the process, contact <a href="mailto:help@goldstandard.org">help@goldstandard.org</a> with reference to the project GSID.
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
        <tr>
          <td><strong>Request Changelog:</strong></td>
          <td></td>
        </tr>
      </table>
      <table style="border: 1px solid #cccccc; padding: 6px; border-collapse: collapse;">
        <tr>
          <th style="border: 1px solid #cccccc; padding: 6px">Account</th>
          <th style="border: 1px solid #cccccc; padding: 6px">User</th>
          <th style="border: 1px solid #cccccc; padding: 6px">Changed at</th>
          <th style="border: 1px solid #cccccc; padding: 6px">From Status</th>
          <th style="border: 1px solid #cccccc; padding: 6px">To Status</th>
        </tr>
        <#list request.changeLog![] as logEntry>
          <tr>
            <td style="border: 1px solid #cccccc; padding: 6px">${logEntry.changedByAccount!}</td>
            <td style="border: 1px solid #cccccc; padding: 6px">${logEntry.changedByUser!}</td>
            <td style="border: 1px solid #cccccc; padding: 6px">${logEntry.changedAt!}</td>
            <td style="border: 1px solid #cccccc; padding: 6px">${logEntry.previousState!}</td>
            <td style="border: 1px solid #cccccc; padding: 6px">${logEntry.nextState!}</td>
          </tr>
        </#list>
      </table>
    </div>',
    'Dear ${recipient},

The following request has had a status change and is now ready for you to work on it.

Activity: ${request.latestRequestActivity.activityName}

Due Date: ${request.latestRequestActivity.plannedEndDate}

Review Due Date: ${request.latestRequestActivity.reviewDueDate!}

Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round: ${request.feedbackRound!}

Review Due Date: ${request.dueDate!}

Request Changelog:

Account\tUser\tChanged at\tFrom Status\tTo Status
<#list request.changeLog![] as logEntry>
${logEntry.changedByAccount!}\t${logEntry.changedByUser!}\t${logEntry.changedAt!}\t${logEntry.previousState!}\t${logEntry.nextState!}
</#list>',
    NOW())
;





INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_INITIATED',
    'v0.0.3',
    'Review initiated for ${project.gsid} ${request.name}',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    Thank you for your submission. The following review has been initiated.
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
            <td>${request.latestRequestActivity.reviewDueDate!}</td>
		</tr>
    </table>
</div>',
    'Dear ${recipient},

Thank you for your submission. The following review has been initiated.

Should you have any questions on the process, please contact help@goldstandard.org with reference to the project GSID.

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





INSERT INTO gs.notification_template (
  name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'REVIEW_ASSIGNED',
    'v0.0.3',
    '${request.name} received for ${project.gsid}',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    You have been assigned to review the following project’s request. Please log-in to your account to access project documents.
    </p>
    <p>
    Please upload the findings documents on GSIQ platform. These findings should be submitted by ${request.latestRequestActivity.reviewDueDate!}.
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
      <td>${request.latestRequestActivity.reviewDueDate!}</td>
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

Review Due Date: ${request.latestRequestActivity.reviewDueDate!}
',
    NOW())
;