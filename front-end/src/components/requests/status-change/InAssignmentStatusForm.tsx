import LoadingButton from "@mui/lab/LoadingButton";
import { Alert, Box, FormControl, FormControlLabel, FormHelperText, FormLabel, Radio, RadioGroup } from "@mui/material";
import React, { ChangeEvent, useCallback, useEffect, useState } from "react";

import { useReferenceTypes } from "../../../common/data-types/DataReferenceTypes";
import { CreateUpdateRequestPayload, GSRequest, isPreliminaryReview } from "../../../common/request/GSRequest";
import { handleApiError } from "../../APIClient/APIErrorHandler";
import { hasReviewerOrRegistryAssignment } from "../actions/Actions";
import { getRequestAssignments } from "../assignment/Assignment";
import { MandatoryStatus } from "../MandatoryStatus";
import { checkIfAssignmentTypeRequiredForRequestType, updateRequest } from "../Requests";

import RequestStatusChangeDisplay from "./RequestStatusChangeDisplay";

interface RequestStatusFormProps {
  request: GSRequest;
  onSubmit: () => void;
}

const InAssignmentStatusForm: React.FC<RequestStatusFormProps> = ({ request, onSubmit }) => {
  const [chosenNextStatus, setChosenNextStatus] = useState<number>(request.requestStatus.id);
  const [isEverythingAssigned, setIsEverythingAssigned] = useState<boolean>(false);
  const [shouldSubmitForReview, setShouldSubmitForReview] = useState<boolean>(false);
  const [assignmentTypeRequiredForRequestType, setAssignmentTypeRequiredForRequestType] = useState<MandatoryStatus>();
  const [hasReviewerAssignment, setHasReviewerAssignment] = useState<boolean>(true);
  const { activeReferenceTypes } = useReferenceTypes();
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const inRoundCompletionStatus = activeReferenceTypes.RequestStatus.find(
    (requestStatus) => requestStatus.code === "IN_ROUND_COMPLETION",
  );

  const getNextStatus = (assignmentTypeRequired: MandatoryStatus) => {
    if (!isEverythingAssigned) {
      return request.requestStatus.id;
    }

    if (inRoundCompletionStatus == undefined) throw Error("Missing ReferenceTypes IN_ROUND_COMPLETION status");

    if (assignmentTypeRequired == MandatoryStatus.NOT_REQUIRED) {
      return inRoundCompletionStatus.id;
    }

    if (shouldSubmitForReview || assignmentTypeRequired == MandatoryStatus.REQUIRED) {
      const inProgressStatus = activeReferenceTypes.RequestStatus.find(
        (requestStatus) => requestStatus.code === "IN_PROGRESS",
      );
      if (inProgressStatus == undefined) throw Error("Missing ReferenceTypes IN_PROGRESS status");
      return inProgressStatus.id;
    }
    return inRoundCompletionStatus.id;
  };

  const checkCurrentStatusChange = useCallback(async () => {
    const assignmentTypeRequired = await checkIfReviewIsRequired();

    setAssignmentTypeRequiredForRequestType(assignmentTypeRequired);

    setChosenNextStatus(getNextStatus(assignmentTypeRequired));
  }, [activeReferenceTypes.RequestStatus, request.requestStatus.id, isEverythingAssigned, shouldSubmitForReview]);

  const checkIfReviewIsRequired = async (): Promise<MandatoryStatus> => {
    if (request.fastTracked && isPreliminaryReview(request)) {
      return MandatoryStatus.NOT_REQUIRED;
    }

    const reviewAssignmentTypeId = activeReferenceTypes.AssignmentType.find(
      (assignmentType) => assignmentType.code == "REVIEW",
    )?.id;

    if (reviewAssignmentTypeId == undefined) {
      throw Error("Can't find review assignment ID");
    }

    const assignmentTypeRequired = await checkIfAssignmentTypeRequiredForRequestType(
      request.requestType.id,
      reviewAssignmentTypeId,
    );

    return assignmentTypeRequired;
  };

  useEffect(() => {
    checkCurrentStatusChange();
  }, [checkCurrentStatusChange, request.requestRounds.length]);

  const handleInAssignmentRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    const everythingAssigned = event.target.value == "true";
    setIsEverythingAssigned(everythingAssigned);

    if (everythingAssigned) {
      checkForReviewerAssignment();
    }
  };

  const checkForReviewerAssignment = async () => {
    try {
      const assignments = await getRequestAssignments(request.id);
      const reviewerAssigned = hasReviewerOrRegistryAssignment(assignments);
      setHasReviewerAssignment(reviewerAssigned);
    } catch (error) {
      console.error("Error checking assignments:", error);
    }
  };

  const handleSubmitForReviewRadioChange = (event: ChangeEvent<HTMLInputElement>) => {
    setShouldSubmitForReview(event.target.value == "true");
  };

  const handleClickSubmit = async () => {
    setIsSubmitting(true);

    const requestToSubmit: Partial<CreateUpdateRequestPayload> = {
      id: request.id,
      requestStatusId: chosenNextStatus,
    };

    try {
      await updateRequest(requestToSubmit, request.project.id);
    } catch (error) {
      handleApiError(error);
    } finally {
      onSubmit();
      setIsSubmitting(false);
    }
  };

  const isMissingReviewerAssignment =
    isEverythingAssigned &&
    !hasReviewerAssignment &&
    (assignmentTypeRequiredForRequestType == MandatoryStatus.REQUIRED ||
      (assignmentTypeRequiredForRequestType == MandatoryStatus.OPTIONAL && shouldSubmitForReview));

  return (
    <Box display="flex" flexDirection="column" p={2} gap={2}>
      <Box>
        <FormControl>
          <FormLabel>Has everything been assigned?</FormLabel>
          <FormHelperText>To assign this request, close this window and click View Assignments.</FormHelperText>
          <RadioGroup name="assigned" value={isEverythingAssigned.toString()} onChange={handleInAssignmentRadioChange}>
            <FormControlLabel value="true" control={<Radio />} label="Yes" data-testid="everything-assigned-yes" />
            <FormControlLabel value="false" control={<Radio />} label="No" data-testid="everything-assigned-no" />
          </RadioGroup>
        </FormControl>
      </Box>
      {isEverythingAssigned && assignmentTypeRequiredForRequestType == MandatoryStatus.OPTIONAL && (
        <Box>
          <FormControl>
            <FormLabel>Do you want to submit this for review?</FormLabel>
            <RadioGroup
              name="assigned"
              value={shouldSubmitForReview.toString()}
              onChange={handleSubmitForReviewRadioChange}
            >
              <FormControlLabel data-testid="submit-for-review-yes" value="true" control={<Radio />} label="Yes" />
              <FormControlLabel data-testid="submit-for-review-no" value="false" control={<Radio />} label="No" />
            </RadioGroup>
          </FormControl>
        </Box>
      )}

      {isMissingReviewerAssignment && (
        <Box>
          <Alert severity="warning" data-testid="reviewer-assignment-warning">
            No reviewer or registry account has been assigned to this request. Please assign a reviewer or registry
            account before submitting.
          </Alert>
        </Box>
      )}

      {chosenNextStatus !== request.requestStatus.id && (
        <RequestStatusChangeDisplay chosenNextStatus={chosenNextStatus} />
      )}

      <Box mt={2}>
        <LoadingButton
          disabled={chosenNextStatus == request.requestStatus.id || isMissingReviewerAssignment}
          loading={isSubmitting}
          onClick={handleClickSubmit}
          variant="contained"
          color="primary"
        >
          Submit
        </LoadingButton>
      </Box>
    </Box>
  );
};

export default InAssignmentStatusForm;
