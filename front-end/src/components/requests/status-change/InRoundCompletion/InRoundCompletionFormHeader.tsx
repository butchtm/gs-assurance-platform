import { Box, Typography } from "@mui/material";
import React from "react";

import { GSRequest } from "../../../../common/request/GSRequest";
import CustomHelperText, { CustomTooltip } from "../../../Reusable/FormFields/CustomTooltipText";

import { AssignmentAuditInfo } from "./InRoundCompletionStatusForm";

interface InRoundCompletionFormHeaderProps {
  assignmentAuditRequestVVB: AssignmentAuditInfo | undefined;
  request: GSRequest;
}

const InRoundCompletionFormHeader: React.FC<InRoundCompletionFormHeaderProps> = ({
  assignmentAuditRequestVVB,
  request,
}) => {
  return (
    <>
      <Box mb={2}>
        <Typography variant="body1">
          <strong>Has VVB Assigned: </strong> {assignmentAuditRequestVVB?.accountId ? "Yes" : "No"}
        </Typography>
      </Box>

      <Box mb={2} sx={{ display: "flex", flexDirection: "column" }}>
        <Typography variant="body1" component="div" sx={{ display: "flex", alignItems: "center" }}>
          <strong>Request in Stakeholder Consultation?:</strong>&nbsp;
          {request?.inStakeholderConsultation ? "Yes" : "No"}
          <CustomHelperText
            text="If the Request is in Stakeholder Consultation, it won't be able to transition to certain statuses."
            type={CustomTooltip.HELP}
          />
        </Typography>
      </Box>
    </>
  );
};

export default InRoundCompletionFormHeader;
