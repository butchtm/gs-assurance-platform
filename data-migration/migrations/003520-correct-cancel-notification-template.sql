UPDATE gs.notification_template
SET html_body = '<div style="font-family: Arial,sans-serif;">
                 <p>Dear ${recipient},</p>
                 <p>I hope this message finds you in good order.</p>
                 <p>
                 We are writing to formally notify you the outcome of the ${request.name} of Project ${project.gsid} (${project.name}).
                 </p>
                 <p>
                 After a comprehensive review of the submission and alignment with Gold Standard protocols (GS4GG Principles &amp; Requirements, relevant project activity requirements), the review has been <strong>Rejected</strong>.
                 </p>
                 <p>
                 A detail of the technical and procedural rationale for this decision, including specific non-compliance findings and gaps, is documented in the official GS review feedback form associated with this project.
                 </p>
                 <p>
                 Should you wish to seek clarification on the decision, please contact <a href="mailto:help@goldstandard.org">help@goldstandard.org</a> for further assistance.
                 </p>
                 <p>
                 Best regards,<br/>
                 The Gold Standard Foundation
                 </p>
             </div>'
WHERE name = 'REQUEST_REJECTED'
AND version = 'v0.0.1';