INSERT INTO gs.notification_template (
    name, version,
    subject,
    html_body,
    text_body,
    created_timestamp
) VALUES (
             'REQUEST_CANCELLED',
             'v0.0.1',
             '${request.name} cancelled for ${project.gsid}',
             '<div style="font-family: Arial,sans-serif;">
             <p1>Dear ${recipient},</p>
             <p>I hope this correspondence finds you well.</p>
             <p>
             We are writing to formally notify you of a status change for the ${request.name} of Project ${project.gsid} (${project.name}).
             </p>
             <p>
             After a review of the submission and alignment with Gold Standard protocols (GS4GG Principles &amp; Requirements, relevant project lifecycle guidelines, and technical review frameworks), the review has been <strong>Cancelled</strong> per the details outlined below:
             </p>
             <p>
             ${request.cancellationReason}
             </p>
             <p>
             To ensure future compliance with GS processes, we advise your team to consult the official the user manual of <a href="https://globalgoals.goldstandard.org/standards/Assurance-Platform-Manual-Project-Developer.pdf">GS Assurance Platform</a> and <a href="https://globalgoals.goldstandard.org/certify-a-project/">GS certification process</a> before submitting any subsequent review requests, and to initiate only the review type that aligns with the projects lifecycle stage and eligibility criteria.
             </p>
             <p>
             Should you require further clarification on next steps or have questions regarding GS review protocols, please direct your inquiries to the Gold Standard support team at <a href="mailto:help@goldstandard.org">help@goldstandard.org</a>.
             </p>
             <p>
             Best regards,<br/>
             The Gold Standard Foundation
             </p>
         </div>',
             'Dear ${recipient},

         I hope this correspondence finds you well.

         We are writing to formally notify you of a status change for the ${request.name} of Project ${project.gsid} (${project.name}).

         After a review of the submission and alignment with Gold Standard protocols (GS4GG Principles & Requirements, relevant project lifecycle guidelines, and technical review frameworks), the review has been Cancelled per the details outlined below:

         ${request.cancellationReason}

         To ensure future compliance with GS processes, we advise your team to consult the official the user manual of GS Assurance Platform (https://globalgoals.goldstandard.org/standards/Assurance-Platform-Manual-Project-Developer.pdf) and GS certification process (https://globalgoals.goldstandard.org/certify-a-project/) before submitting any subsequent review requests, and to initiate only the review type that aligns with the project''s lifecycle stage and eligibility criteria.

         Should you require further clarification on next steps or have questions regarding GS review protocols, please direct your inquiries to the Gold Standard support team at help@goldstandard.org.

         Best regards,
         The Gold Standard Foundation
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
             'REQUEST_OMITTED',
             'v0.0.1',
             '${request.name} omitted for ${project.gsid}',
             '<div style="font-family: Arial,sans-serif;">
             <p1>Dear ${recipient},</p>
             <p>I hope this correspondence finds you well.</p>
             <p>
             We are writing to formally notify you of a status change for the ${request.name} of Project ${project.gsid} (${project.name}).
             </p>
             <p>
             After a review of the submission and alignment with Gold Standard protocols (GS4GG Principles &amp; Requirements, relevant project lifecycle guidelines, and technical review frameworks), the review has been <strong>Omitted</strong> per the details outlined below:
             </p>
             <p>
             ${request.cancellationReason}
             </p>
             <p>
             To ensure future compliance with GS processes, we advise your team to consult the official the user manual of <a href="https://globalgoals.goldstandard.org/standards/Assurance-Platform-Manual-Project-Developer.pdf">GS Assurance Platform</a> and <a href="https://globalgoals.goldstandard.org/certify-a-project/">GS certification process</a> before submitting any subsequent review requests, and to initiate only the review type that aligns with the project''s lifecycle stage and eligibility criteria.
             </p>
             <p>
             Should you require further clarification on next steps or have questions regarding GS review protocols, please direct your inquiries to the Gold Standard support team at <a href="mailto:help@goldstandard.org">help@goldstandard.org</a>.
             </p>
             <p>
             Best regards,<br/>
             The Gold Standard Foundation
             </p>
         </div>',
             'Dear ${recipient},

     I hope this correspondence finds you well.

     We are writing to formally notify you of a status change for the ${request.name} of Project ${project.gsid} (${project.name}).

     After a review of the submission and alignment with Gold Standard protocols (GS4GG Principles & Requirements, relevant project lifecycle guidelines, and technical review frameworks), the review has been Omitted per the details outlined below:

     ${request.cancellationReason}

     To ensure future compliance with GS processes, we advise your team to consult the official the user manual of GS Assurance Platform (https://globalgoals.goldstandard.org/standards/Assurance-Platform-Manual-Project-Developer.pdf) and GS certification process (https://globalgoals.goldstandard.org/certify-a-project/) before submitting any subsequent review requests, and to initiate only the review type that aligns with the project''s lifecycle stage and eligibility criteria.

     Should you require further clarification on next steps or have questions regarding GS review protocols, please direct your inquiries to the Gold Standard support team at help@goldstandard.org.

     Best regards,
     The Gold Standard Foundation
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
             'REQUEST_REJECTED',
             'v0.0.1',
             '${request.name} rejected for ${project.gsid}',
             '<div style="font-family: Arial,sans-serif;">
             <p1>Dear ${recipient},</p>
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
         </div>',
             'Dear ${recipient},

     I hope this message finds you in good order.

     We are writing to formally notify you the outcome of the ${request.name} of Project ${project.gsid} (${project.name}).

     After a comprehensive review of the submission and alignment with Gold Standard protocols (GS4GG Principles & Requirements, relevant project activity requirements), the review has been Rejected.

     A detail of the technical and procedural rationale for this decision, including specific non-compliance findings and gaps, is documented in the official GS review feedback form associated with this project.

     Should you wish to seek clarification on the decision, please contact help@goldstandard.org for further assistance.
     ',
             NOW())
;

INSERT INTO gs.request_status_notification
(id, min_round_index, max_round_index, from_status_id, to_status_id,
 request_owner_template_name, request_owner_template_version,
 arm_team_template_name, arm_team_template_version,
 registry_team_template_name, registry_team_template_version,
 stakeholder_template_name, stakeholder_template_version,
 vvb_template_name, vvb_template_version)
VALUES
    (140, 0, 99, null, (SELECT id from gs.request_status WHERE code = 'CANCELLED'),
     'REQUEST_CANCELLED', 'v0.0.1',
     null, null,
     null, null,
     null, null,
     'REQUEST_CANCELLED', 'v0.0.1')
;

INSERT INTO gs.request_status_notification
(id, min_round_index, max_round_index, from_status_id, to_status_id,
 request_owner_template_name, request_owner_template_version,
 arm_team_template_name, arm_team_template_version,
 registry_team_template_name, registry_team_template_version,
 stakeholder_template_name, stakeholder_template_version,
 vvb_template_name, vvb_template_version)
VALUES
    (150, 0, 99, null, (SELECT id from gs.request_status WHERE code = 'OMITTED'),
     'REQUEST_OMITTED', 'v0.0.1',
     null, null,
     null, null,
     null, null,
     'REQUEST_OMITTED', 'v0.0.1')
;

INSERT INTO gs.request_status_notification
(id, min_round_index, max_round_index, from_status_id, to_status_id,
 request_owner_template_name, request_owner_template_version,
 arm_team_template_name, arm_team_template_version,
 registry_team_template_name, registry_team_template_version,
 stakeholder_template_name, stakeholder_template_version,
 vvb_template_name, vvb_template_version)
VALUES
    (160, 0, 99, null, (SELECT id from gs.request_status WHERE code = 'COMPLETED_REJECTED'),
     'REQUEST_REJECTED', 'v0.0.1',
     null, null,
     null, null,
     null, null,
     'REQUEST_REJECTED', 'v0.0.1')
;


