import { Box, Button, Typography } from "@mui/material";
import React from "react";

import { GSRequest } from "../../../../common/request/GSRequest";
import RequestStatusChangeDisplay from "../RequestStatusChangeDisplay";

interface InRoundCompletionControlsProps {
  statusChangePermitted: boolean;
  chosenNextStatus: number;
  request: GSRequest;
  handleClickSubmit: () => void;
  isReviewComplete: boolean | null;
  requestHasOpenFindings: boolean | null;
  requestIsRejected: boolean | null;
}

const InRoundCompletionControls: React.FC<InRoundCompletionControlsProps> = ({
  statusChangePermitted,
  chosenNextStatus,
  request,
  handleClickSubmit,
  isReviewComplete,
  requestHasOpenFindings,
  requestIsRejected,
}) => {
  if (request.inStakeholderConsultation) {
    return (
      <Typography sx={{ color: "red" }}>
        Requests of this type in Stakeholder Consultation cannot be submitted.
      </Typography>
    );
  }

  if (isReviewComplete === null) {
    return null;
  }

  const displayNextStatus =
    isReviewComplete &&
    ((!requestHasOpenFindings && requestHasOpenFindings != null) ||
      (requestHasOpenFindings && requestIsRejected !== null));

  if (statusChangePermitted) {
    return (
      <>
        {displayNextStatus && <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />}
        <Box mt={2}>
          <Button
            disabled={
              !isReviewComplete ||
              request.inStakeholderConsultation ||
              requestHasOpenFindings === null ||
              (requestHasOpenFindings && requestIsRejected == null)
            }
            onClick={handleClickSubmit}
            variant="contained"
            color="primary"
          >
            Submit
          </Button>
        </Box>
      </>
    );
  } else {
    return <Typography sx={{ color: "red" }}>Requests of this type cannot be submitted at this time.</Typography>;
  }
};

export default InRoundCompletionControls;
