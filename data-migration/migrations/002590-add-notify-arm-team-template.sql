
INSERT INTO gs.notification_template (
    name,
    version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
    'NOTIFY_ARM_ROUND_TWO',
    'v0.0.1',
    '${project.gsid} ${request.name} ready for you to work on',
    '<div style="font-family: Arial,sans-serif;">
    <h1>Dear ${recipient},</h1>
    <p>
    The following request has had a status change and is now ready for Reviewer to work on it.
    </p>
    <#if request.feedbackRound?? && request.feedbackRound != 0>
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
    </#if>
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
			<td><strong>Request Change Log:</strong></td>
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

The following request has had a status change and is now ready for Reviewer to work on it.

<#if request.feedbackRound?? && request.feedbackRound != 0>
Activity: ${request.latestRequestActivity.activityName}

Due Date: ${request.latestRequestActivity.plannedEndDate}
</#if>

Project: ${project.name}

GSID: ${project.gsid}

Request: ${request.name}

Project Type: ${project.projectTypes}

Feedback Round: ${request.feedbackRound!}

Review Due Date: ${request.latestRequestActivity.reviewDueDate!}

Request Change Log:

Account\tUser\tChanged at\tFrom Status\tTo Status
<#list request.changeLog![] as logEntry>
${logEntry.changedByAccount!}\t${logEntry.changedByUser!}\t${logEntry.changedAt!}\t${logEntry.previousState!}\t${logEntry.nextState!}
</#list>',
    NOW())
;

UPDATE gs.request_status_notification SET
  arm_team_template_name = 'NOTIFY_ARM_ROUND_TWO',
  arm_team_template_version = 'v0.0.1'
WHERE id IN (120, 130);

INSERT INTO gs.request_status_assignee_notification
(id,request_status_notification_id,assignment_type_id,assignee_template_name,assignee_template_version)
VALUES (12, 120, 2, 'NOTIFY_ARM_ROUND_TWO', 'v0.0.1');

INSERT INTO gs.request_status_assignee_notification
(id,request_status_notification_id,assignment_type_id,assignee_template_name,assignee_template_version)
VALUES (13, 130, 2, 'NOTIFY_ARM_ROUND_TWO', 'v0.0.1');