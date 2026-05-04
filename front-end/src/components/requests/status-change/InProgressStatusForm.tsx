import { Box, Button, FormControl, FormControlLabel, FormLabel, Radio, RadioGroup, Typography } from "@mui/material";
import React, { ChangeEvent, useCallback, useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { ReferenceType } from "../../../common/project/Project";
import { CreateUpdateRequestPayload, GSRequest } from "../../../common/request/GSRequest";
import { handleApiError } from "../../APIClient/APIErrorHandler";
import LoadingSpinner from "../../Reusable/LoadingSpinner";
import { Assignment, getRequestAssignments } from "../assignment/Assignment";
import { updateRequest } from "../Requests";

import RequestStatusChangeDisplay from "./RequestStatusChangeDisplay";

interface RequestStatusFormProps {
  request: GSRequest;
  onSubmit: () => void;
}

const InProgressStatusForm: React.FC<RequestStatusFormProps> = ({ request, onSubmit }) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [requestReviewHasBeenAccepted, setRequestReviewHasBeenAccepted] = useState<boolean>(false);
  const [isReviewComplete, setIsReviewComplete] = useState<boolean>(false);
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const checkCurrentStatusChange = useCallback(() => {
    setChosenNextStatus(request.requestStatus.id);

    const inRoundCompletionStatus = activeReferenceTypes.RequestStatus.find(
      (requestStatus: ReferenceType) => requestStatus.code === "IN_ROUND_COMPLETION",
    );

    const isValidRoundCompletion = !!isReviewComplete && !!inRoundCompletionStatus;

    if (isValidRoundCompletion) {
      setChosenNextStatus(inRoundCompletionStatus.id);
    }
  }, [activeReferenceTypes.RequestStatus, isReviewComplete, request.requestStatus.id]);

  const checkRequestReviewAccepted = async () => {
    const fetchedAssignments = await getRequestAssignments(request.id);
    const hasReviewAccepted = fetchedAssignments ? hasAssignment(fetchedAssignments, "REVIEW", "ACCEPTED") : false;
    return setRequestReviewHasBeenAccepted(hasReviewAccepted);
  };

  const hasAssignment = (assignments: Assignment[], assignmentType: string, assignmentStatus: string) =>
    assignments.some(
      (assignment) =>
        assignment.assignmentType?.code === assignmentType && assignment.assignmentStatus?.code === assignmentStatus,
    );

  useEffect(() => {
    checkRequestReviewAccepted();
    checkCurrentStatusChange();
  }, [checkCurrentStatusChange, isReviewComplete]);

  const handleInProgressRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    const reviewIsComplete = event.target.value === "true";
    setIsReviewComplete(reviewIsComplete);
  };

  const handleClickSubmit = async () => {
    setIsSubmitting(true);

    if (!chosenNextStatus) {
      return;
    }

    const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
      id: request.id,
      requestStatusId: chosenNextStatus,
    };

    if (request.project) {
      try {
        await updateRequest(requestToSubmit, request.project?.id);
      } catch (error) {
        handleApiError(error);
      } finally {
        onSubmit();
        setIsSubmitting(false);
      }
    }
  };

  return (
    <Box display="flex" flexDirection="column" p={2}>
      <Box mb={2}>
        <FormControl>
          <FormLabel>Has the review been completed?</FormLabel>
          <RadioGroup
            name="review-completed"
            value={isReviewComplete ? "true" : "false"}
            onChange={handleInProgressRadioChange}
          >
            <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="review-complete-yes" />
            <FormControlLabel value="false" control={<Radio />} label="No" data-testid="review-complete-no" />
          </RadioGroup>
        </FormControl>
      </Box>

      {!requestReviewHasBeenAccepted ? (
        <Typography variant="body2" color="error" sx={{ fontSize: "0.875rem", marginTop: "0.5rem", margin: "auto" }}>
          Request needs reviewer acceptance to be submitted.
        </Typography>
      ) : (
        <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />
      )}

      <Box mt={2}>
        <Button onClick={handleClickSubmit} disabled={!isReviewComplete} variant="contained" color="primary">
          Submit
        </Button>
      </Box>
      {isSubmitting && <LoadingSpinner />}
    </Box>
  );
};

export default InProgressStatusForm;
