INSERT INTO gs.notification_template ("name", "version", subject, html_body, text_body, created_timestamp) VALUES('UNREAD_MESSAGES', 'v0.0.1', 'Unread messages', '<div style="font-family: Arial,sans-serif;">
    <p>Dear ${recipient},</p>
    <p>
    You have unread messages that require your attention. Please log-in to your account to view the message under the relevant request section.
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
                <th style="border: 1px solid #cccccc; padding: 6px">Project Type</th>
            </tr>
            <#list unread.requests![] as unread>
                <tr>
                <td style="border: 1px solid #cccccc; padding: 6px">${unread.project.name}</td>
                <td style="border: 1px solid #cccccc; padding: 6px">${unread.project.gsid}</td>
                <td style="border: 1px solid #cccccc; padding: 6px">${unread.request.name}</td>
                <td style="border: 1px solid #cccccc; padding: 6px">${unread.project.projectTypes}</td>
                </tr>
            </#list>
        </table>
        <br/>
</div>', 'Dear ${recipient},

You have unread messages that require your attention. Please log-in to your account to view the message under the relevant request section.

Best regards,

The Gold Standard Foundation
Project\tGSID\tRequest\tProject Type
<#list unread.requests![] as unread>
  ${unread.project.name!}\t${unread.project.gsid!}\t${unread.request.name!}\t${unread.project.projectTypes!}
</#list>',
 NOW());