update gs.notification_template nt
set html_body = replace(nt.html_body, '<h1>Dear ${recipient},</h1>', '<p>Dear ${recipient},</p>')
where nt.html_body like '%<h1>Dear ${recipient},</h1>%';