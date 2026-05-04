import { Box, Typography } from "@mui/material";
import React, { useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { RequestStatusCode } from "../../../common/request/GSRequest";

interface RequestStatusChangeDisplayProps {
  chosenNextStatus: number;
}

const RequestStatusChangeDisplay: React.FC<RequestStatusChangeDisplayProps> = ({ chosenNextStatus }) => {
  const { activeReferenceTypes } = useReferenceTypes();
  const [statusText, setStatusText] = useState<string>();

  const getStatusCodeDescription = (requestStatusCode: RequestStatusCode) => {
    switch (requestStatusCode) {
      case "IN_PREPARATION":
        return "Project developer for preparation";
      case "IN_AUDIT":
        return "VVB for audit";
      case "COMPLETENESS_CHECK":
        return "GS for completeness check";
      case "IN_PROGRESS":
        return "GS reviewer for review";
      case "IN_ROUND_COMPLETION":
        return "ARM team for round completion";
      case "IN_ASSIGNMENT":
        return "ARM team for assignment";
      case "COMPLETED_REJECTED":
        return "Rejected State";
      case "COMPLETED_APPROVED":
        return "Approved State";
      default:
        throw Error("Unexpected requestStatus code");
    }
  };

  useEffect(() => {
    const newStatusRef = activeReferenceTypes.RequestStatus.find(
      (requestStatus) => requestStatus.id === chosenNextStatus,
    );
    if (newStatusRef) {
      setStatusText(getStatusCodeDescription(newStatusRef.code as RequestStatusCode));
    }
  }, [activeReferenceTypes.RequestStatus, chosenNextStatus]);

  return (
    <Box display="flex" alignItems="center" justifyContent="center">
      <Typography>Submit this request to</Typography>
      &nbsp;
      {statusText && <Typography>{statusText}</Typography>}
    </Box>
  );
};

export default RequestStatusChangeDisplay;
